use rand::{self, Rng};

use genmesh::{Triangulate, Vertices};
use genmesh::generators::{Plane, SharedVertex, IndexedPolygon};

use noise::Seed;
use noise::perlin2;

use ncollide::TriMesh3;

use super::Vertex;

use annulus::Annulus;

pub struct Engine {
    seed: Seed,
    ring: Annulus,
}

impl Engine {
    pub fn new() -> Engine {
        let rand_seed = rand::thread_rng().gen();

        Engine {
            seed: Seed::new(rand_seed),
            ring: Annulus::new(),
        }
    }

    pub fn to_vertex(&self) -> (Vec<Vertex>, Vec<u32>) {
        let vertices = Vec::new();
        let indices = Vec::new();
        heights = HeightMap::new(&self.seed);
        for (coords, center) in self.ring.cells() {
            let h = heights.height(coords);
            vertices.push(Vertex {
                pos: [xy.x, xy.y, h],
                color: [h, h, h],
            });
            let center_index: u32 = vertices.len() - 1;
            let corner_indices = [u32; 6];
            for n in 0..6 {
                let angle: f32 = 60.0 * n as f32   + 30.0;
                let angle = angle.to_radians();
                vertices.push(Vertex {
                    pos: [xy.x + self.ring.cell_size * angle_rad.cos(), xy.y + self.ring.cell_size * angle_rad.cos(), 0.0],
                    color: [h, h, h]
                });
                corner_indices[n] = vertices.len() - 1;
            }
            for n in 0..5 {
                indices.push(center_index);
                indices.push(corner_indices[n]);
                indices.push(corner_indices[n + 1]);
            }
            indices.push(center_index);
            indices.push(corner_indices[5]);
            indices.push(corner_indices[0]);
        }
        (vertices, indices)
    }

}

struct HeightMap {
    rng: Perlin,
}

impl HeightMap {
    fn new(s: &Seed) -> HeightMap {
        let perlin = Perlin::new().set_seed(s);
        HeightMap {
            rng: Perlin::new().set_seed(s),
        }
    }

    fn height(&self, HexCoord) -> f32 {
        self.rng.get(HexCoord)
    }
}
