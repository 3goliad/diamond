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
    println!("{:?}, {:?}", res_x, res_y);

    // the main loop
    let mut running_time = Duration::new(0, 0);
    let mut frame = 0;
    'tight: loop {
        frame += 1;
        let now = Instant::now();

        // drawing a frame
        let mut target = display.draw();
        target.clear_color(0.0, 0.0, 0.0, 0.0);
        target.draw(&vertex_buffer,
                    &index_buffer,
                    &program,
                    //Uniforms for inputting data into diamond.frag
                    &uniform! {
                        u_resolution: [res_x as f32, res_y as f32],
                        u_frame: frame as f32,
                    },
                    &Default::default())
            .unwrap();
        target.finish().unwrap();

        // polling and handling the events received by the window
        for event in display.poll_events() {
            match event {
                glutin::Event::Closed => break 'tight,
                _ => (),
            }
        }

        let delta = now.elapsed();
        println!("{:?}", (frame as f32 / running_time.as_secs() as f32));
        running_time += delta;
        let fixed_time_stamp = Duration::new(0, 16666667);
        thread::sleep(match fixed_time_stamp.checked_sub(delta) {
            Some(d) => d,
            None => fixed_time_stamp,
        });
    }
}
