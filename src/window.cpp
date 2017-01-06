#include <iostream>
#include <cstdlib>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "window.hpp"

GLFWwindow* window;

void error_callback(int error, const char* description)
{
  std::cerr << "GLFW Error: " << description << std::endl;
}

void resize_callback(GLFWwindow* win, int w, int h) {
  glViewport(0, 0, w, h);
}

void init_window(int w, int h) {
  glfwSetErrorCallback(error_callback);

  if(glfwInit() == 0) {
    std::cerr << "GLFW init failed" << std::endl;
    exit(EXIT_FAILURE);
  }

  glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
  glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
  glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

  window = glfwCreateWindow(w, h, "diamond", NULL, NULL);
  if(window == nullptr) {
    std::cerr << "GLFW window creation failed" << std::endl;
    glfwTerminate();
    exit(EXIT_FAILURE);
  }

  glfwSetFramebufferSizeCallback(window, resize_callback);

  glfwMakeContextCurrent(window);

  glewInit();

  int width, height;
  glfwGetFramebufferSize(window, &width, &height);
  glViewport(0, 0, width, height);

  glfwSwapInterval(1);

  // get version info
  const GLubyte* renderer = glGetString(GL_RENDERER); // get renderer string
  const GLubyte* version = glGetString(GL_VERSION); // version as a string
  std::clog << "Renderer: " << renderer << std::endl;
  std::clog << "OpenGL version: " << version << std::endl;
}

void drawloop(void(*draw)(void)) {
  while(glfwWindowShouldClose(window) == 0) {
    draw();
    glfwSwapBuffers(window);
    glfwPollEvents();
  }
}

void close_window() {
  glfwDestroyWindow(window);
  glfwTerminate();
}
