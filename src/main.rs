#[macro_use]
extern crate log;
extern crate env_logger;
extern crate glutin;
#[macro_use]
extern crate gfx;
extern crate gfx_device_gl;
extern crate gfx_window_glutin;
extern crate nalgebra;
extern crate rand;
extern crate genmesh;
extern crate noise;

use glutin::{Event, VirtualKeyCode};
use gfx::Device;
use gfx::traits::FactoryExt;

use std::time::Instant;

mod engine;
use engine::Engine;
mod camera;
use camera::Camera;

pub type ColorFormat = gfx::format::Rgba8;
pub type DepthFormat = gfx::format::DepthStencil;

gfx_defines!{
    vertex Vertex {
        pos: [f32; 3] = "a_Pos",
        color: [f32; 3] = "a_Color",
    }

    constant Locals {
        model: [[f32; 4]; 4] = "u_Model",
        view: [[f32; 4]; 4] = "u_View",
        proj: [[f32; 4]; 4] = "u_Proj",
    }

    pipeline pipe {
        vbuf: gfx::VertexBuffer<Vertex> = (),
        locals: gfx::ConstantBuffer<Locals> = "Locals",
        model: gfx::Global<[[f32; 4]; 4]> = "u_Model",
        view: gfx::Global<[[f32; 4]; 4]> = "u_View",
        proj: gfx::Global<[[f32; 4]; 4]> = "u_Proj",
      out_color: gfx::RenderTarget<ColorFormat> = "Target0",
        out_depth: gfx::DepthTarget<DepthFormat> =
            gfx::preset::depth::LESS_EQUAL_WRITE,
    }
}

pub fn main() {
    env_logger::init().unwrap();
    let mut camera = Camera::new();
    let engine = Engine::new();
    let builder = glutin::WindowBuilder::new()
        .with_title("diamond".to_string())
        .with_vsync();
    let (window, mut device, mut factory, main_color, main_depth) =
        gfx_window_glutin::init::<ColorFormat, DepthFormat>(builder);
    let mut encoder: gfx::Encoder<_, _> = factory.create_command_buffer().into();
    let (screen_width, screen_height) = window.get_inner_size_points().unwrap();
    camera.set_aspect_ratio(screen_width as f32 / screen_height as f32);
    let pso = factory.create_pipeline_simple(include_bytes!("shader/terrain.glslv"),
                                include_bytes!("shader/terrain.glslf"),
                                pipe::new())
        .unwrap();
    let (vertex_data, index_data) = engine.to_vertex();
    let (vbuf, slice) = factory.create_vertex_buffer_with_slice(&vertex_data, index_data.as_slice());
    let mut data = pipe::Data {
        vbuf: vbuf,
        locals: factory.create_constant_buffer(1),
        model: camera::identity_matrix(),
        view: camera.view(),
        proj: camera.perspective(),
        out_color: main_color,
        out_depth: main_depth,
    };
    let start_time = Instant::now();
    'main: loop {
        for event in window.poll_events() {
            match event {
                Event::Closed | Event::KeyboardInput(_, _, Some(VirtualKeyCode::Escape)) => break 'main,
                Event::Resized(width, height) => {
                    camera.set_aspect_ratio(width as f32 / height as f32);
                    data.proj = camera.perspective();
                    gfx_window_glutin::update_views(&window,
                                                    &mut data.out_color,
                                                    &mut data.out_depth);
                },
                _ => {},
            }
        }
        // draw a frame
        let elapsed = start_time.elapsed();
        let time = elapsed.as_secs() as f32 + elapsed.subsec_nanos() as f32 / 1000_000_000.0;
        camera.move_to(&[time.sin() * 32.0, time.cos() * 32.0, 32.0]);
        data.view = camera.view();

        let locals = Locals {
            model: data.model,
            view: data.view,
            proj: data.proj,
        };

        encoder.update_buffer(&data.locals, &[locals], 0).unwrap();
        encoder.clear(&data.out_color, [0.1, 0.2, 0.3, 1.0]);
        encoder.clear_depth(&data.out_depth, 1.0);
        encoder.draw(&slice, &pso, &data);
        encoder.flush(&mut device);
        window.swap_buffers().unwrap();
        device.cleanup();
    }
}
