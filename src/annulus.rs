use na::Point3;

struct Hex {
    center: Point3,
}

struct Annulus {
    map: HashMap<Point3, Hex>,
}
