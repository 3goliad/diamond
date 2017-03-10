use std::collections::HashMap;

use na::Point2;

/// Hexes are addressed using 3d coordinates
type HexCoord = [i8; 3];

/// A single cell of the map
#[derive(Hash, Eq, PartialEq, Debug)]
struct Hex {
    center: Point2,
    edges: [Vec<f32>; 6],
}

const CELL_SIZE: f32 = 10;

impl Hex {
    pub fn corners(&self) -> [Point2; 6] {
        let mut out = [Point2::origin(); 6]
        for n in 0..6 {
            let angle = (60.0 * n as f32   + 30.0).to_radians();
            out[n] = Point2::new(self.center.x + CELL_SIZE * angle.cos(), self.center.y + CELL_SIZE * angle.sin());
        }
        return out;
    }
}

/// A ring of cells
struct Annulus {
    map: HashMap<Point3, Hex>,
    bounds: (usize, usize),
}

impl Annulus {
    /// Returns a small annulus consisting of a single ring of 6 cells
    pub fn new() -> Annulus {
        Annulus {
            map: HashMap::new(),
            bounds: (1, 1),
        }
    }

    pub fn get_neighbors(&self, p: HexCoord) -> [HexCoord; 6] {
        let x = p.0;
        let y = p.1;
        let z = p.2;
        [
            [x - 1, y, z + 1],
            [x + 1, y, z - 1],
            [x + 1, y - 1, z],
            [x - 1, y + 1, z],
            [x, y + 1, z - 1],
            [x, y - 1, z + 1],
        ]
    }

    pub fn get(&self, p: HexCoord) -> &Hex {
        self.map.get(p)
    }

    pub fn get_mut(&mut self, p: HexCoord) -> &mut Hex {
        self.map.get_mut(p)
    }

    pub fn coords(&self) -> Vec<HexCoord> {
        self.map.keys().collect()
    }
}
