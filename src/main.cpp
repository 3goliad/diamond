#include <cstdlib>
#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include "engine.hpp"
#include "window.hpp"

// Window dimensions
const int SCREEN_WIDTH = 800;
const int SCREEN_HEIGHT = 600;

int main(int argc, char* argv[]) {
  init_window(SCREEN_WIDTH, SCREEN_HEIGHT);
  init_engine();

#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(draw, 0, 1);
#else
  drawloop(draw);
#endif

  close_window();
  exit(EXIT_SUCCESS);
}
