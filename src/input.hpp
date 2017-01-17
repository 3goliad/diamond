#pragma once

#include <GLFW/glfw3.h>

#include "camera.hpp"

namespace input {

float last_x= 400;
float last_y = 300;

float current_frame = 0.0f;
float last_frame = 0.0f;
float delta_time = 0.0f;

bool keys[1024];

// Moves/alters the camera positions based on user input
void update() {
  glfwPollEvents();

  current_frame = glfwGetTime();
  delta_time = current_frame - last_frame;
  last_frame = current_frame;

  // Camera controls
  if(keys[GLFW_KEY_W])
      camera::move(camera::direction::forwards, delta_time);
  if(keys[GLFW_KEY_S])
      camera::move(camera::direction::backwards, delta_time);
  if(keys[GLFW_KEY_A])
      camera::move(camera::direction::left, delta_time);
  if(keys[GLFW_KEY_D])
      camera::move(camera::direction::right, delta_time);
}

// Is called whenever a key is pressed/released via GLFW
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mode)
{
  if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
    glfwSetWindowShouldClose(window, GL_TRUE);

  if(action == GLFW_PRESS)
    keys[key] = true;
  else if(action == GLFW_RELEASE)
    keys[key] = false;	
}

void mouse_callback(GLFWwindow* window, double xpos, double ypos)
{
    if(first_mouse)
    {
        last_x = xpos;
        last_x = ypos;
        first_mouse = false;
    }

    float xoffset = xpos - last_x;
    float yoffset = last_y - ypos;  // Reversed since y-coordinates go from bottom to left
    
    last_x = xpos;
    last_y = ypos;

    camera::process_mouse(xoffset, yoffset);
}	


void scroll_callback(GLFWwindow* window, double xoffset, double yoffset)
{
  camera::scroll(yoffset);
}
