#[macro_use]
extern crate glium;

use std::time::{Instant, Duration};
use std::thread;

use glium::Surface;
use glium::glutin::{self, GlRequest, Api};
use glium::index::PrimitiveType;

fn main() {
    use glium::DisplayBuild;

    // building the display, ie. the main object
    let display = glutin::WindowBuilder::new()
        .with_gl(GlRequest::Specific(Api::OpenGl, (4, 1)))
        .build_glium()
        .unwrap();

    // building the vertex buffer, which contains all the vertices that we will draw
    let vertex_buffer = {
        #[derive(Copy, Clone)]
        struct Vertex {
            position: [f32; 2],
        }

        implement_vertex!(Vertex, position);

        glium::VertexBuffer::new(&display,
                                 &[Vertex { position: [1.0, 1.0] },
                                   Vertex { position: [-1.0, 1.0] },
                                   Vertex { position: [1.0, -1.0] },
                                   Vertex { position: [-1.0, -1.0] }])
                .unwrap()
    };

    // building the index buffer
    let indices: [u16; 6] = [0u16, 1, 2, 1, 3, 2];
    let index_buffer = glium::IndexBuffer::new(&display, PrimitiveType::TrianglesList, &indices)
        .unwrap();

    // compiling shaders and linking them together
    let shader_source = include_bytes!("diamond.frag");
    let fragment_shader = std::str::from_utf8(shader_source).unwrap();
    let program = program!(&display,
        410 => {
            vertex: "
                #version 410 core

                in vec2 position;

                void main() {
                    gl_Position = vec4(position, 0.0, 1.0);
                }
            ",

            fragment: fragment_shader,
        },
    )
            .unwrap();

    let (res_x, res_y) = display.get_framebuffer_dimensions();

    // the main loop
    start_loop(|| {
        // building the uniforms
        let uniforms = uniform! {
            u_resolution: [res_x, res_y],
        };

        // drawing a frame
        let mut target = display.draw();
        target.clear_color(0.0, 0.0, 0.0, 0.0);
        target.draw(&vertex_buffer,
                    &index_buffer,
                    &program,
                    &uniforms,
                    &Default::default())
            .unwrap();
        target.finish().unwrap();

        // polling and handling the events received by the window
        for event in display.poll_events() {
            match event {
                glutin::Event::Closed => return Action::Stop,
                _ => (),
            }
        }

        Action::Continue
    });
}

pub fn start_loop<F>(mut callback: F)
    where F: FnMut() -> Action
{
    let mut accumulator = Duration::new(0, 0);
    let mut previous_clock = Instant::now();

    loop {
        match callback() {
            Action::Stop => break,
            Action::Continue => (),
        };

        let now = Instant::now();
        accumulator += now - previous_clock;
        previous_clock = now;

        let fixed_time_stamp = Duration::new(0, 16666667);
        while accumulator >= fixed_time_stamp {
            accumulator -= fixed_time_stamp;

            // if you have a game, update the state here
        }

        thread::sleep(fixed_time_stamp - accumulator);
    }
}

pub enum Action {
    Stop,
    Continue,
}
