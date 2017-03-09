use std::cmp::Ordering;

use na::Point2;
use ncollide::utils::circumcircle;

type Point = Point2<f32>;

type Event = Option<Point>;

struct Arc {
    p: Point,
    event: Event,
    l_edge: Option<EdgeHandle>,
    r_edge: Option<EdgeHandle>,
}

impl Arc {
    fn new(p: Point) -> Arc {
        Arc {
            p: p,
            event: None,
            l_edge: None,
            r_edge: None,
        }
    }
}

struct Beachline {
    l: Vec<Arc>,
    r: Vec<Arc>,
}

impl Beachline {
    fn new(p: Point) -> Beachline {
        Beachline {
            l: Vec::new(),
            r: Vec::new(),
        }
    }

    fn insert(&mut self, p: Point) {
        (if self.l.len() <= self.r.len() {
                self.l
            } else {
                self.r
            })
            .push(Arc::new())
    }


    fn join_at(&mut self, p: Point) -> Option<Event> {
        let new_root = self.l.pop();
        let new_edge = graph.start_edge(p);
        if let Some(a) = self.l.last_mut() {
            a.r_edge = new_edge;
        }
        if let Some(a) = self.r.last_mut() {
            a.l_edge = new_edge;
        }
        graph.finish_edge(self.current.l_edge, p);
        graph.finish_edge(self.current.r_edge, p);
        (Some(check_circle_event(self.left.last(), p.x)),
         Some(check_circle_event(self.right.last(), p.x)))
    }

    fn has_event(&self, p: &Point) -> bool {
        if let Some(a) = self.l.iter.find(|a| a.p == p) {
            a.event.is_some()
        } else if let Some(a) = self.r.iter.find(|a| a.p == p) {
            a.event.is_some()
        }
    }
}

struct VoronoiGraph {
    output: EdgeList,
    bline: Beachline,
}

impl VoronoiGraph {
    pub fn new(sites: Vec<Point>) -> VoronoiGraph {
        let events = BinaryHeap::from(sites);
        let graph = EdgeList::new();
        let bline = Beachline::new();
        bline.insert(events.pop());

        'main: while let Some(event) = events.pop() {
            if bline.has_event(&event) {
                bline.join_at(&event);
            } else {
                // find if any arcs have the same height as our point
                while let Some(arc) = beachline.next {
                    if let Some(intersection) = intersection(site, arc) {
                        // new parabola intersects arc i.
                        // if neccesary, duplicate i
                        if arc.next.is_some() & !intersection(site, arc.next) {
                            arc.next.prev = Arc::new(arc.point, arc, arc.next);
                            arc.next = arc.next.prev;
                        } else {
                            arc.next = Arc::new(arc.point, arc);
                        }
                        arc.next.one = arc.one;

                        // add the site between arc and arc.next
                        arc.next.prev = Arc::new(site, arc, arc.next);
                        arc.next = arc.next.prev;

                        arc = arc.next; // now arc points to the new arc

                        // add new half edges at arc's endpoints
                        arc.prev.right_endpoint = arc.left_endpoint =
                                                      Segment::PartialSeg(intersection);
                        arc.next.left_endpoint = arc.right_endpoint =
                                                     Segment::PartialSeg(intersection);
                        check_circle_event(arc, site.x);
                        check_circle_event(arc.prev, site.x);
                        check_circle_event(arc.next, site.x);

                        continue 'main;
                    }
                }

                // if the site does not have any intersections add it to the list
                arc.last.next = Arc::new(site, arc.last);
                // insert a new half edge between the last arc and our site
                arc.last.right_endpoint =
                    arc.last.next.left_endpoint =
                        Segment::PartialSeg(Point::new(MAX_X, (arc.last.y + arc.last.next.y) / 2));
            }
        }
        while let Some(e) = events.pop() {
            //process_event();
        }
        //clean up extra edges
    }

    fn check_circle_event(arc: Arc, x: f32) {
        if arc.event.is_some() & arc.event.x != x {
            arc.event.valid = false;
        }
        arc.event = None;

        if arc.prev.is_none() | arc.next.is_none() {
            return;
        }

        if let (Some(new_x), Some(p)) = circumcircle(arc.prev.point, arc.point, arc.next.point) {
            arc.event = Event::new(x, p, arc);
            events.push(arc.event);
        }
    }

    fn intersects(p: Point, arc: Arc) -> Option<Point> {
        if arc.point.x == p.x {
            return None;
        }

        if let Some(prev) = arc.prev {
            let a = intersection(prev.point, arc.point, p.x).y;
        }
        if let Some(next) = a.next {
            let b = intersection(arc.point, next.point, p.x).y;
        }
        if (a.prev.is_none() || a <= p.y) && (arc.next.is_none() || b <= p.y) {
            Some(Point::new((arc.point.x.pow(2) + arc.point.y.pow(2) - p.x.pow(2)) / 2 *
                            (arc.point.x - p.x)))
        } else {
            None
        }
    }

    fn intersection(pa: Point, pb: Point, sweepline: f32) -> Point {
        let mut result = Point::new();
        let p = pa;

        if (pa.x == pb.x) {
            result.y = (pa.y + pb.y) / 2;
        } else if (pb.x == l) {
            result.y = pb.y;
        } else if (pa.x == l) {
            res.y = pa.y;
            let p = pb;
        } else {
            // Use the quadratic formula.
            let z0 = 2 * (pa.x - l);
            let z1 = 2 * (pb.x - l);

            let a = z0.recip() - z1.recip();
            let b = -2 * (pa.y / z0 - pb.y / z1);
            let c = (pa.y.powi(2) + pa.x.powi(2) - l.powi(2)) / z0 -
                    (pb.y.powi(2) + pb.x.powi(2) - l.powi(2)) / z1;

            result.y = (-b - sqrt(b.powi(2) - 4 * a * c)) / (2 * a);
        }
        // Plug back into one of the parabola equations.
        result.x = (p.x.powi(2) + (p.y - result.y).powi(2) - l.powi(2)) / (2 * p.x - 2 * l);
        return res;
    }
}
