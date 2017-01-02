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

  GLFWwindow *window = glfwCreateWindow(640, 480, "My Title", NULL, NULL);
  if (window == NULL) {
    std::cerr << "Window creation failed!" << std::endl;
    return 1;
  }

  glfwSetKeyCallback(window, key_callback);

  glfwMakeContextCurrent(window);


  //double time = glfwGetTime();

  glfwSwapInterval(1);

  while (glfwWindowShouldClose(window) == GLFW_TRUE) {
    int width;
    int height;
    glfwGetFramebufferSize(window, &width, &height);
    glViewport(0, 0, width, height);

    glfwSwapBuffers(window);
    glfwPollEvents();
  }

  std::clog << "Success!" << std::endl;
  glfwDestroyWindow(window);
  glfwTerminate();
  exit(EXIT_SUCCESS);
}
