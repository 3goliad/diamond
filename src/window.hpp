#pragma once

#include <GLFW/glfw3.h>

namespace window {
  // handle to glfw
  GLFWwindow* handle;
  // create window and opengl context
  void create(int w, int h);
  // free window resources after the window is closed
  void destroy();
  // collect and process input
  void update();
  // swap screen buffer
  void swap();
}
