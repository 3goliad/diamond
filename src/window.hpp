#pragma once

#include <stdio.h>
#include <stdlib.h>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

void error_callback(int error, const char *description) {
  printf("glfw error: %s\n", description);
}

void resize_callback(GLFWwindow *win, int w, int h) {
    glViewport(0, 0, w, h);
}

GLFWwindow *window_create(int w, int h) {
  GLFWwindow *handle;
  glfwSetErrorCallback(error_callback);
  if (glfwInit() == 0) {
    printf("GLFW window init failed\n");
    exit(EXIT_FAILURE);
  }
  glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
  glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
  glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
  handle = glfwCreateWindow(w, h, "diamond", NULL, NULL);
  if (handle == nullptr) {
    printf("GLFW window creation failed\n");
    glfwTerminate();
    exit(EXIT_FAILURE);
  }
  glfwMakeContextCurrent(handle);
  glfwSetFramebufferSizeCallback(handle, resize_callback);

  glewInit();
  int width, height;
  glfwGetFramebufferSize(handle, &width, &height);
  glViewport(0, 0, width, height);
  glfwSwapInterval(1);

  return handle;
}
