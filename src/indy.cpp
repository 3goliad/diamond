#include "SDL2/SDL_image.h"
#include "SDL2/SDL.h"
#include <iostream>
#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif
#pragma clang diagnostic ignored "-Wconstant-conversion"
#include "image_loader.h"
#pragma clang diagnostic pop

SDL_Window *window;
SDL_Surface *screen;
SDL_Surface *hello;

void display_once() {
	std::clog << "Running main loop\n";
  SDL_BlitSurface(hello, NULL, screen, NULL);
   SDL_UpdateWindowSurface( window );
}

int main() {
	window = NULL;
  screen = NULL;
  hello = NULL;

  std::clog << "Initializing SDL\n";
  if ((SDL_Init(SDL_INIT_VIDEO) == -1)) {
    std::cerr << "Could not initialize SDL\n";
    std::cerr << SDL_GetError();
    exit(-1);
  }

  atexit(SDL_Quit);

  std::clog << "Trying to set video mode\n";
  window = SDL_CreateWindow( "DIAMOND", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, 640, 480, SDL_WINDOW_RESIZABLE | SDL_WINDOW_OPENGL);
  if (window == NULL) {
    std::cerr << "Could not get video mode\n";
    std::cerr << SDL_GetError();
    exit(-1);
  }

  screen = SDL_GetWindowSurface(window);

  std::clog << "Trying to load hello image\n";
  hello = IMG_Load("assets/alpine.jpg");
  if (hello == NULL) {
    std::cerr << "Could not load asset\n";
    exit(-1);
  }
  std::clog << "Should have loaded image\n";

#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(display_once, 0, 1);
#else
  while (true) {
    display_once();
    // Delay to keep frame rate constant (using SDL)
    SDL_Delay(2000);
    break;
  }
#endif
  SDL_FreeSurface(hello);
  SDL_DestroyWindow(window);

  std::clog << "Quitting SDL\n";
  SDL_Quit();

  std::clog << "Quitting\n";
  return 0;
}
