#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <iostream>

void error_callback(int error, const char *description) {
  std::cerr << "Err: " << error << " " << description << std::endl;
}

static void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods)
{
    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
        glfwSetWindowShouldClose(window, GLFW_TRUE);
}

int main() {
  if (glfwInit() == GLFW_FALSE) {
    std::cerr << "GLFW Init failed!" << std::endl;
    return 1;
  }

  glfwSetErrorCallback(error_callback);

  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
  GLFWwindow *window = glfwCreateWindow(640, 480, "diamond", NULL, NULL);
  if (window == NULL) {
    std::cerr << "Window creation failed!" << std::endl;
    return 1;
  }

  glfwSetKeyCallback(window, key_callback);

  glfwMakeContextCurrent(window);
  gladLoadGLES2Loader((GLADloadproc) glfwGetProcAddress);

  int w = 0;
  int h = 0;
  glfwGetFramebufferSize(window, &w, &h);
  glViewport(0, 0, w, h);

  double time = glfwGetTime();

  glfwSwapInterval(1);

  while (glfwWindowShouldClose(window) == GLFW_FALSE) {
    glfwPollEvents();

    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glfwSwapBuffers(window);
  }

  std::clog << "Success!" << std::endl;
  glfwDestroyWindow(window);
  glfwTerminate();
  exit(EXIT_SUCCESS);
}
