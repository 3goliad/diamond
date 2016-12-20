#include <stdio.h>
#include <SDL/SDL.h>

#define STB_IMAGE_IMPLEMENTATION
#pragma clang diagnostic ignored "-Wall"
#include "stb_image.h"
#pragma clang diagnostic pop

SDL_Surface *load_image(const std::string &filename)
{
    int x, y, comp;
    unsigned char *data;
    uint32_t rmask, gmask, bmask, amask;
    SDL_Surface *rv;

    FILE *file = fopen(filename.c_str(), "rb");
    if (!file)
        return 0;

    data = stbi_load_from_file(file, &x, &y, &comp, 0);
    fclose(file);

#if SDL_BYTEORDER == SDL_BIG_ENDIAN
    rmask = 0xff000000;
    gmask = 0x00ff0000;
    bmask = 0x0000ff00;
    amask = 0x000000ff;
#else
    rmask = 0x000000ff;
    gmask = 0x0000ff00;
    bmask = 0x00ff0000;
    amask = 0xff000000;
#endif

    if (comp == 4) {
        rv = SDL_CreateRGBSurface(0, x, y, 32, rmask, gmask, bmask, amask);
    } else if (comp == 3) {
        rv = SDL_CreateRGBSurface(0, x, y, 24, rmask, gmask, bmask, 0);
    } else {
        stbi_image_free(data);
        return 0;
    }

    memcpy(rv->pixels, data, comp * x * y);
    stbi_image_free(data);

    return rv;
}
