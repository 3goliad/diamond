#include <iostream>

#include <GLFW/glfw3.h>
#include <glad/glad.h>

#include "shaders.hpp"

// Function prototypes
void key_callback(GLFWwindow *window, int key, int scancode, int action,
                  int mode);
void error_callback(int error, const char *description);

// Window dimensions
const GLuint WIDTH = 800, HEIGHT = 600;

int main() {
  // Init GLFW
  glfwInit();
  glfwSetErrorCallback(error_callback);

  // Set all the required options for GLFW
  glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

  // Create a GLFWwindow object that we can use for GLFW's functions
  GLFWwindow *window =
      glfwCreateWindow(WIDTH, HEIGHT, "diamond", nullptr, nullptr);
  glfwMakeContextCurrent(window);

  // load opengl functions from glad
  gladLoadGLES2Loader((GLADloadproc)glfwGetProcAddress);

  // Set the required callback functions
  glfwSetKeyCallback(window, key_callback);

  // Define the viewport dimensions
  int width, height;
  glfwGetFramebufferSize(window, &width, &height);
  glViewport(0, 0, width, height);

  // load shaders
  GLuint shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");

  // Set up vertex data (and buffer(s)) and attribute pointers
  GLfloat vertices[] = {
      -0.5f, -0.5f, 0.0f, // Left
      0.5f,  -0.5f, 0.0f, // Right
      0.0f,  0.5f,  0.0f  // Top
  };

  // Game loop
  while (glfwWindowShouldClose(window) == GLFW_FALSE) {
    // Check if any events have been activiated (key pressed, mouse moved etc.)
    // and call corresponding response functions
    glfwPollEvents();

    // Render
    // Clear the colorbuffer
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // Draw our first triangle
    glUseProgram(shaderProgram);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(0);

    glDrawArrays(GL_TRIANGLES, 0, 3);

    // Swap the screen buffers
    glfwSwapBuffers(window);
  }

  // Terminate GLFW, clearing any resources allocated by GLFW.
  glfwDestroyWindow(window);
  glfwTerminate();
  return 0;
}

// Is called whenever a key is pressed/released via GLFW
void key_callback(GLFWwindow *window, int key, int scancode, int action,
                  int mode) {
  if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
    glfwSetWindowShouldClose(window, GL_TRUE);
  }
}

void error_callback(int error, const char *description) {
  std::cerr << "Err: " << error << " " << description << std::endl;
}
