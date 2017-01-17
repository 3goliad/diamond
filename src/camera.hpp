#pragma once

#include "glm/glm.hpp"

namespace camera {
  // movement directions
  enum struct direction : char { up, down, left, right };
  // get view matrix
  glm::mat4 get_viewmat4();
  // move camera in a single direction for a fixed amount of time
  void move(direction, GLfloat delta_time);
  // direct camera with mouse movement
  void process_mouse(GLfloat xoffset, GLfloat yoffset, bool constrain_pitch);
  // zoom camera with scroll
  void scroll(GLfloat yoffset);
}
