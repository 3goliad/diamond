use rand::Rng;
use rand::distributions::Range;
use na::Point2;

/// A bounding box centered at the origin
#[derive(Clone, Copy)]
struct Bounds(usize, usize);

/// Any closed figure on a plane that contains points
trait PlaneFigure {
    fn contains_point(p: &Point2) -> bool;
    // must return a box large enough to contain the entire shape
    fn bounds() -> Bounds;
}

/// An annulus with an origin at (0,0)
#[derive(Clone, Copy)]
pub struct Annulus {
    inner_radius: usize,
    outer_radius: usize,
}

impl Annulus {
    /// Create a new annulus
    pub fn new(inner_radius: usize, outer_radius: usize) -> Annulus {
        Annulus {
            inner_radius: inner_radius,
            outer_radius: outer_radius,
        }
    }
}

impl PlaneFigure for Annulus {
    /// Test if a given point lies inside this annulus
    fn contains_point(&self, p: &Point2) -> bool {
        ((self.inner_radius as f32) < p.coords.norm()) <= self.outer_radius as f32
    }

    /// Return a box large enough to contain this annulus
    fn bounds(&self) -> Bounds {
        Bounds(self.outer_radius * 2, self.outer_radius * 2)
    }
}

struct Polygon {
    center: Point2,
    halfedge: HalfedgeID,
}

struct Vertex {
    location: Point2,
    halfedge: HalfedgeID,
}

struct HalfEdge {
    target: VertexID,
    face: PolygonID,
    next: HalfedgeID,
    prev: HalfedgeID,
}

type PolygonID = usize;
type VertexID = usize;
type HalfEdgeID = usize;

struct VoronoiGraph<S: PlaneFigure> {
    shape: S,
    bounds: Bounds,
    vertices: Vec<Vertex>,
    edges: Vec<Halfedge>,
    faces: Vec<Polygon>,
}



impl<S: PlaneFigure> VoronoiGraph<S> {
    pub fn new<R: Rng>(shape: S, rng: R) -> VoronoiGraph {
        struct Site {
            y: f32,
            x: f32,
            is_boundary: bool,
        }
        let seen_sites: Vec<Site> = Vec::new();
        // a priority queue of points in the bounding plane, ordered lexicographically, that is,
        // for a point p{x, y}, p1 < p2 if p1.y <= p2.y, p1.x < p2.x
        // points are stored as indices into the sites vector
        let sites_queue: VecDeque<usize> = VecDeque::new();
        let bounds = shape.bounds();
        let Bounds(xdim, ydim) = bounds;
        let (x_range, y_range) = (Range::new((xdim as f32 / 2), (xdim as f32 / 2) * -1.0),
                                  Range::new((ydim as f32 / 2), (ydim as f32 / 2) * -1.0));
        for n in 0..99 {
            seen_sites[n] = Site {
                y: y_range.sample(),
                x: x_range.sample(),
                is_boundary: false,
            };
            sites_queue.push_back(n);
        }
        sites.sort_by(|a, b| (a.y <= b.y) & (a.x < b.x));
        enum Poly {
            Face(usize),
            Boundary(usize, usize),
        }
        // an ordered sequence of Poly components of the form Face, Boundary, Face, Boundary, Face
        let polys: Vec<Poly> = Vec::new();
        let p = sites_queue.pop_front();
        polys.push(Poly::Face(p));
        while !points.is_empty() {
            let p = sites_queue.pop_front();
            match seen_sites[p].is_boundary { 
                false => {
                    // find a face in polys containing p
                    // find the parabola that divides the origin of the face from p
                    // add the two new regions to the list
                    // add the left and right parabola components to the list as boundaries
                    // check if the left parabola intersects with a neighbor, add the intersection
                    // to the seen sites list and queue
                    // perform the same check for the right parabola
                }
                true => {
                    // p intersects boundaries (q, r) and (r, s)
                    // create a new boundary (q, s)
                    // replace the sequence Boundary(q, r), Face(r), Boundary(r, s) in polys with
                    // the new Boundary(q, s)
                    // delete any intersections with (q, r) and (r, s) from the sites_queue
                    // add any new intersections to the sites list and queue
                    // p is now a vertex which is the endpoint of Boundaries (q,r), (r, s), (q, s)
                }
            }
            // all remaining regions are voronoi cell, all remaining boundaries are edges
        }

        VoronoiGraph {
            shape: shape,
            vertices: vertices,
            edges: edges,
            faces: faces,
        }
    }
}
