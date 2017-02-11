use noise::Seed;
use rand::{self, Rng};
use genmesh::{Triangulate, Vertices};
use genmesh::generators::{Plane, SharedVertex, IndexedPolygon};
use super::Vertex;
use noise::perlin2;

pub struct Engine {
    seed: Seed,
    plane: Plane,
}

impl Engine {
    pub fn new() -> Engine {
        let rand_seed = rand::thread_rng().gen();

        Engine {
            seed: Seed::new(rand_seed),
            plane: Plane::subdivide(256, 256),
        }
    }

    pub fn to_vertex(&self) -> (Vec<Vertex>, Vec<u32>) {
        (self.plane
             .shared_vertex_iter()
             .map(|(x, y)| {
                let h = perlin2(&self.seed, &[x, y]) * 32.0;
                Vertex {
                    pos: [25.0 * x, 25.0 * y, h],
                    color: calculate_color(h),
                }
            })
             .collect(),
         self.plane
             .indexed_polygon_iter()
             .triangulate()
             .vertices()
             .map(|i| i as u32)
             .collect())
    }
}

fn calculate_color(height: f32) -> [f32; 3] {
    if height > 8.0 {
        [0.9, 0.9, 0.9] // white
    } else if height > 0.0 {
        [0.7, 0.7, 0.7] // greay
    } else if height > -5.0 {
        [0.2, 0.7, 0.2] // green
    } else {
        [0.2, 0.2, 0.7] // blue
    }
}
