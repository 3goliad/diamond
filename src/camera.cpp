#include <GL/glew.h>
#include <glm/glm.hpp>

#include "camera.hpp"

namespace camera {

static glm::vec3 pos = glm::vec3(0.0f, 0.0f, 0.0f);
static glm::vec3 dir_front;
static glm::vec3 dir_up;
static glm::vec3 dir_right;
static glm::vec3 world_up = glm::vec3(0.0f, 1.0f, 0.0f);

static GLfloat yaw = -90.0f;
static GLfloat pitch = 0.0f;

static GLfloat mov_speed = 3.0f;
static GLfloat sensitivity = 0.25f;
static GLfloat zoom 45.0f;

// Returns the view matrix calculated using Eular Angles and the LookAt Matrix
glm::mat4 get_viewmat4() {
  update_vecs();
  return glm::lookAt(pos, pos + dir_front, up);
}

// Processes input received from any keyboard-like input system. Accepts input
// parameter in the form of camera defined ENUM (to abstract it from windowing
// systems)
void move(direction dir, GLfloat delta_time) {
  update_vecs();
  GLfloat velocity = mov_speed * delta_time;
  if (dir == direction::forwards)
    pos += dir_front * velocity;
  if (dir == direction::backwards)
    pos -= dir_front * velocity;
  if (dir == direction::left)
    pos -= dir_right * velocity;
  if (dir == direction::right)
    pos += dir_right * velocity;
}

void process_mouse(GLfloat xoffset, GLfloat yoffset,
                   bool constrain_pitch = true) {
  xoffset *= sensitivity;
  yoffset *= sensitivity;

  yaw += xoffset;
  pitch += yoffset;

  if (constrain_pitch) {
    if (pitch > 89.0f)
      pitch = 89.0f;
    if (pitch < -89.0f)
      pitch = -89.0f;
  }

  update_vecs();
}

// Processes input received from a mouse scroll-wheel event. Only requires input
// on the vertical wheel-axis
void scroll(GLfloat yoffset) {
  if (zoom >= 1.0f && zoom <= 45.0f)
    zoom -= yoffset;
  if (zoom <= 1.0f)
    zoom = 1.0f;
  if (zoom >= 45.0f)
    zoom = 45.0f;
}

// Calculates the front vector from the Camera's (updated) Eular Angles
void update_vecs() {
  // Calculate the new Front vector
  glm::vec3 front;
  front.x = cos(glm::radians(yaw)) * cos(glm::radians(pitch));
  front.y = sin(glm::radians(pitch));
  front.z = sin(glm::radians(yaw)) * cos(glm::radians(pitch));
  dir_front = glm::normalize(front);
  // Also re-calculate the Right and Up vector
  dir_right = glm::normalize(
      glm::cross(dir_front, world_up)); // Normalize the vectors, because their
                                        // length gets closer to 0 the more you
                                        // look up or down which results in
                                        // slower movement.
  dir_up = glm::normalize(glm::cross(dir_right, dir_front));
}

}
