#include <cstdlib>
#include <iostream>

#include <SDL2/SDL.h>
#include <GL/glew.h>
#include <SDL2/SDL_opengl.h>
#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif


#include "shaders.hpp"

// Window dimensions
const GLuint SCREEN_WIDTH = 800;
const GLuint SCREEN_HEIGHT = 600;

//grabs current error and prints it
void sdlErr(int line = -1) {
  	std::string error = SDL_GetError();
    if (error != "") {
      std::cout << "SDL Error : " << error << std::endl;

      if (line != -1) {
        std::cout << "\nLine : " << line << std::endl;
      }
		SDL_ClearError();
  }
}


void initSDL(SDL_Window **win, SDL_GLContext *context) {
  if (SDL_Init(SDL_INIT_VIDEO) != 0) {
    sdlErr(__LINE__);
    exit(EXIT_FAILURE);
  }

  //set opengl to only use the core functions
  SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);

  *win = SDL_CreateWindow("diamond", SDL_WINDOWPOS_UNDEFINED,
                                     SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH,
                                     SCREEN_HEIGHT, SDL_WINDOW_OPENGL| SDL_WINDOW_SHOWN);
  sdlErr(__LINE__);
  if (*win == nullptr) {
    SDL_Quit();
    exit(EXIT_FAILURE);
  }

  *context = SDL_GL_CreateContext(*win);
  sdlErr(__LINE__);

  SDL_GL_SetSwapInterval(1);

  glewInit();
}

void draw(SDL_Window **win, GLuint shaderProgram) {
  SDL_Event event;
  while (SDL_PollEvent(&event)) {
    if (event.type == SDL_QUIT)
      exit(EXIT_SUCCESS);

    if (event.type == SDL_KEYDOWN) {
      switch (event.key.keysym.sym) {
      case SDLK_ESCAPE:
        exit(EXIT_SUCCESS);
        break;
      default:
        break;
      }
    }
  }

  GLfloat vertices[] = {
      -0.5f, -0.5f, 0.0f, // Left
      0.5f,  -0.5f, 0.0f, // Right
      0.0f,  0.5f,  0.0f  // Top
  };

  glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
  glClear(GL_COLOR_BUFFER_BIT);

  glUseProgram(shaderProgram);

  glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
  glEnableVertexAttribArray(0);

  glDrawArrays(GL_TRIANGLES, 0, 3);
  SDL_GL_SwapWindow(*win);
}

int main() {

  SDL_Window *win;
  SDL_GLContext context;
  initSDL(&win, &context);

  // load shaders
  GLuint shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");

  int w, h;
  SDL_GL_GetDrawableSize(win, &w, &h);
  glViewport(0, 0, w, h);

#ifdef __EMSCRIPTEN__
  // void emscripten_set_main_loop(em_callback_func func, int fps, int
  // simulate_infinite_loop);
  emscripten_set_main_loop(draw, 60, 1);
#else
  while (1) {
    draw(&win, shaderProgram);
    SDL_Delay(200);
  }
#endif

  // Delete our OpengL context
  SDL_GL_DeleteContext(context);
  // Destroy our window
  SDL_DestroyWindow(win);
  // Shutdown SDL 2
  SDL_Quit();
}
