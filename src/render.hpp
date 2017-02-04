#pragma once

#include <GL/glew.h>

#include "camera.hpp"
#include "shader.hpp"

class render {
    public:
        // Window dimensions
        const unsigned int SCREEN_WIDTH = 800;
        const unsigned int SCREEN_HEIGHT = 600;
        float current_frame = 0.0f;
        float last_frame = 0.0f;
        float delta_time = 0.0f;
        void draw();
        void render_init();
        camera cam;
}
