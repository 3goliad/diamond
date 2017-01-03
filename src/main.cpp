#include <iostream>

#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include <glad/glad.h>
#include <SDL2/SDL.h>

#include "shaders.hpp"

// Window dimensions
const GLuint SCREEN_WIDTH = 800;
const GLuint SCREEN_HEIGHT = 600;

int main() {
  if (SDL_Init(SDL_INIT_VIDEO) != 0) {
    std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
    return 1;
  }

  SDL_Window *win = SDL_CreateWindow(
      "diamond", 
      SDL_WINDOWPOS_UNDEFINED, 
      SDL_WINDOWPOS_UNDEFINED, 
      SCREEN_WIDTH, 
      SCREEN_HEIGHT, 
      SDL_WINDOW_OPENGL);
  if (win == nullptr){
    std::cout 
      << "SDL_CreateWindow Error: " 
      << SDL_GetError() 
      << std::endl;
    SDL_Quit();
    return 1;
  }

  SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);
  SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);
  SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);

  SDL_Surface *screenSurface = SDL_GetWindowSurface(window);
  SDL_UpdateWindowSurface(window);
  SDL_Delay(2000);
  SDL_DestroyWindow(window);
  SDL_Quit();

  //glViewport(0, 0, width, height);

  //// load shaders
  //GLuint shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");

  //// Set up vertex data (and buffer(s)) and attribute pointers
  //GLfloat vertices[] = {
  //    -0.5f, -0.5f, 0.0f, // Left
  //    0.5f,  -0.5f, 0.0f, // Right
  //    0.0f,  0.5f,  0.0f  // Top
  //};

  //// Game loop
  //while (glfwWindowShouldClose(window) == GLFW_FALSE) {
  //  // Check if any events have been activiated (key pressed, mouse moved etc.)
  //  // and call corresponding response functions
  //  glfwPollEvents();

  //  // Render
  //  // Clear the colorbuffer
  //  glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
  //  glClear(GL_COLOR_BUFFER_BIT);

  //  // Draw our first triangle
  //  glUseProgram(shaderProgram);
  //  glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
  //  glEnableVertexAttribArray(0);

  //  glDrawArrays(GL_TRIANGLES, 0, 3);
  return 0;
}
