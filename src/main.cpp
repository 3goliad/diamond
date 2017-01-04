#include <GL/glew.h>

#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include "engine.hpp"
#include "window.hpp"

// Window dimensions
const GLuint SCREEN_WIDTH = 800;
const GLuint SCREEN_HEIGHT = 600;

int main(int argc, char* argv[]) {
  init_glut(&argc, argv, SCREEN_WIDTH, SCREEN_HEIGHT);
  init_engine();

#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(draw, 0, 1);
#else
  start_glut();
#endif
}
