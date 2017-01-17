#include <cstdlib>
#include <cstdio>

#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "window.hpp"
#include "render.hpp"

// Window dimensions
const int SCREEN_WIDTH = 800;
const int SCREEN_HEIGHT = 600;

int main(int argc, char* argv[]) {
  window::create(SCREEN_WIDTH, SCREEN_HEIGHT);
  render::init();

#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(render::draw, 0, 1);
#else
  while (glfwWindowShouldClose(window::handle) == 0) {
    render::draw();
  }
#endif

  window::destroy();
  exit(EXIT_SUCCESS);
}
