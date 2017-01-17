#include <cstdlib>
#include <cstdio>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "input.hpp"

namespace window {

GLFWwindow *handle;

using namespace input;

void error_callback(int error, const char *description) {
  printf("glfw error: %s\n", description);
}

void resize_callback(GLFWwindow *win, int w, int h) {
  glViewport(0, 0, w, h);
}

void create(int w, int h) {
    glfwSetErrorCallback(error_callback);

    if (glfwInit() == 0) {
      printf("GLFW window init failed\n");
      exit(EXIT_FAILURE);
    }

    glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

    handle = glfwCreateWindow(w, h, "diamond", NULL, NULL);
    if (handle == nullptr) {
      printf("GLFW window creation failed\n");
      glfwTerminate();
      exit(EXIT_FAILURE);
    }
    glfwMakeContextCurrent(handle);

    glfwSetFramebufferSizeCallback(handle, resize_callback);
    glfwSetKeyCallback(window, key_callback);
    glfwSetCursorPosCallback(window, mouse_callback);
    glfwSetScrollCallback(window, scroll_callback);

    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    glewInit();

    int width, height;
    glfwGetFramebufferSize(handle, &width, &height);
    glViewport(0, 0, width, height);

    glfwSwapInterval(1);

    // get version info
    const GLubyte *renderer = glGetString(GL_RENDERER); // get renderer string
    const GLubyte *version = glGetString(GL_VERSION);   // version as a string
    printf("renderer: %s\n", renderer);
    printf("opengl version: %s\n", version);
}

void swap() {
  glfwSwapBuffers(handle);
}

void destroy() {
  glfwDestroyWindow(handle);
  glfwTerminate();
}

}
