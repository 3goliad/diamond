#include <cstdio>
#include <cstdlib>

#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "window.hpp"
#include "render.hpp"
#include "engine.hpp"

void log_renderer() {
    // get version info
    const GLubyte* renderer = glGetString(GL_RENDERER); // get renderer string
    const GLubyte* version = glGetString(GL_VERSION);   // version as a string
    printf("renderer: %s\n", renderer);
    printf("opengl version: %s\n", version);
}

int main(int argc, char* argv[]) {
    //asserts so we don't have to use opengl types
    if((sizeof(GLfloat)) != (sizeof(float))) {
        printf("bad opengl types, time to go");
        exit(1);
    }
    //getting a window
    //for unspeakable reasons, those constants are defined in render.hpp
    GLFWwindow* window = window_create(SCREEN_WIDTH, SCREEN_HEIGHT);
    log_renderer();
    //get us a camera
    camera::init(window);
    //set callbacks to respond to input
    glfwSetKeyCallback(window, camera::key_callback);
    glfwSetCursorPosCallback(window, camera::mouse_callback);
    glfwSetScrollCallback(window, camera::scroll_callback);
    //get some vertexes
    render_init();
#ifdef __EMSCRIPTEN__
    emscripten_set_main_loop(draw, 0, 1);
#else
    while (glfwWindowShouldClose(window) == 0) {
        draw();
    }
#endif
    glfwDestroyWindow(window);
    glfwTerminate();
    exit(EXIT_SUCCESS);
}
