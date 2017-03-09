use na::{PerspectiveMatrix3, Matrix4, Isometry3, Vector3, Point3, Origin, ToHomogeneous, Eye};

pub struct Camera {
    pos: Point3<f32>,
    target: Point3<f32>,
    persp: PerspectiveMatrix3<f32>,
    view: Matrix4<f32>,
}

impl Camera {
    pub fn new() -> Camera {
        let position = Point3::new(0.0, 32.0, 0.0);
        let target = Point3::origin();
        Camera {
            pos: position,
            target: target,
            persp: PerspectiveMatrix3::new(1.0, 90.0, 0.1, 100.0),
            view: view_matrix(&position, &target),
        }
    }

    pub fn view(&self) -> [[f32; 4]; 4] {
        self.view.as_ref().clone()
    }

    pub fn perspective(&self) -> [[f32; 4]; 4] {
        self.persp.as_matrix().as_ref().clone()
    }

    pub fn set_aspect_ratio(&mut self, aspect: f32) {
        self.persp.set_aspect(aspect);
    }

    pub fn move_to(&mut self, position: &[f32; 3]) {
        self.pos = Point3::from(position);
        self.view = view_matrix(&self.pos, &self.target);
    }
}

fn view_matrix(position: &Point3<f32>, target: &Point3<f32>) -> Matrix4<f32> {
    Isometry3::look_at_rh(&position, &target, &Vector3::z()).to_homogeneous()
}

pub fn identity_matrix() -> [[f32; 4]; 4] {
    Matrix4::new_identity(4).as_ref().clone()
}
