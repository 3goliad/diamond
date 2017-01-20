#pragma once

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"

enum camera_dir { FORWARD, BACKWARD, LEFT, RIGHT };

struct camera {
  // camera attributes
  static glm::vec3 pos;
  static glm::vec3 dir_front;
  static glm::vec3 dir_up;
  static glm::vec3 dir_right;
  const static glm::vec3 world_up;
  // euler Angles
  static float yaw;
  static float pitch;
  // camera options
  const static float mov_speed;
  const static float sensitivity;
  static float zoom;
  static float velocity;
  // mouse data
  static double xpos;
  static double ypos;
  static double xpos_last;
  static double ypos_last;

  static void init(GLFWwindow *win);
  static void set_velocity(float time_delta);
  static glm::mat4 view();
  static void key_callback(GLFWwindow *window, int key, int scancode, int action, int mode);
  static void mouse_callback(GLFWwindow *window, double xpos, double ypos);
  static void scroll_callback(GLFWwindow *window, double xoffset, double yoffset);
  static void update_vecs();
};

const float camera::mov_speed = 3.0f;
const float camera::sensitivity = 0.25f;
float camera::yaw = 90.0f;
float camera::pitch = 0.0f;
float camera::zoom = 45.0f;
float camera::velocity;
glm::vec3 camera::pos(0.0f, 0.0f, 0.0f);
glm::vec3 camera::dir_front(0.0f, 0.0f, -1.0f);
glm::vec3 camera::dir_up;
glm::vec3 camera::dir_right;
const glm::vec3 camera::world_up(0.0f, 1.0f, 0.0f);
double camera::xpos;
double camera::ypos;
double camera::xpos_last;
double camera::ypos_last;

void camera::init(GLFWwindow *win) {
  glfwGetCursorPos(win, &xpos, &ypos);
  xpos_last = xpos;
  xpos_last = ypos;
  update_vecs();
}

void camera::set_velocity(float time_delta) {
  velocity = mov_speed * time_delta;
}

glm::mat4 camera::view() {
  return glm::lookAt(pos, pos + dir_front, dir_up);
}

void camera::key_callback(GLFWwindow *window, int key, int scancode,
                                 int action, int mode) {
  if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
    glfwSetWindowShouldClose(window, GL_TRUE);
  }

  if (key == GLFW_KEY_W && action == GLFW_PRESS) {
    pos += dir_front * velocity;
  } else if (key == GLFW_KEY_S && action == GLFW_PRESS) {
    pos -= dir_front * velocity;
  } else if (key == GLFW_KEY_A && action == GLFW_PRESS) {
    pos -= dir_right * velocity;
  } else if (key == GLFW_KEY_D && action == GLFW_PRESS) {
    pos += dir_right * velocity;
  }
}

void camera::mouse_callback(GLFWwindow *window, double xpos,
                                   double ypos) {
  float xoffset = (xpos - xpos_last) * sensitivity;
  float yoffset =
      (ypos_last - ypos) *
      sensitivity; // Reversed since y-coordinates go from bottom to left

  xpos_last = xpos;
  xpos_last = ypos;

  yaw += xoffset;
  pitch += yoffset;

  if (pitch > 89.0f) {
    pitch = 89.0f;
  } else if (pitch < -89.0f) {
    pitch = -89.0f;
  }
}

void camera::scroll_callback(GLFWwindow *window, double xoffset,
                                    double yoffset) {
  if (zoom >= 1.0f && zoom <= 45.0f) {
    zoom -= yoffset;
  } else if (zoom <= 1.0f) {
    zoom = 1.0f;
  } else if (zoom >= 45.0f) {
    zoom = 45.0f;
  }
}

// Calculates the front vector from the Camera's (updated) Eular Angles
void camera::update_vecs() {
  // Calculate the new Front vector
  glm::vec3 front;
  front.x = cos(glm::radians(yaw)) * cos(glm::radians(pitch));
  front.y = sin(glm::radians(pitch));
  front.z = sin(glm::radians(yaw)) * cos(glm::radians(pitch));
  dir_front = glm::normalize(front);
  // Also re-calculate the dir_right and dir_up vector
  dir_right = glm::normalize(
      glm::cross(dir_front, world_up)); // Normalize the vectors, because their
                                        // length gets closer to 0 the more you
                                        // look up or down which results in
                                        // slower movement.
  dir_up = glm::normalize(glm::cross(dir_right, dir_front));
}
