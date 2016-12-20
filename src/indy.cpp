#include <iostream>
#include <SDL/SDL.h>
#include "image_loader.h"

int main() {
  SDL_Surface* hello = NULL;
  SDL_Surface* screen = NULL;

  std::clog << "Initializing SDL\n";
  if ((SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO) == -1)) {
	  std::cerr << "Could not initialize SDL\n";
	  std::cerr << SDL_GetError();
          exit(-1);
    }

  atexit(SDL_Quit);

  screen = SDL_SetVideoMode(800, 600, 32, SDL_SWSURFACE);
  if (screen == NULL ) {
	  std::cerr << "Could not get video mode\n";
	  std::cerr << SDL_GetError();
	  exit(-1);
  }

  hello = load_image("assets/alpine.jpg");
  if(hello == 0) {
	  std::cerr << "Could not load asset\n";
	  exit(-1);
  }

  SDL_BlitSurface(hello, NULL, screen, NULL);
  SDL_Flip(screen);
  SDL_Delay(2000);
  SDL_FreeSurface(hello);

  std::clog << "Quitting SDL\n";
  SDL_Quit();

  std::clog << "Quitting\n";
  return 0;
}
