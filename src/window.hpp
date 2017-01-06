#ifndef WINDOW_H
#define WINDOW_H

void init_window(int w, int h);
void drawloop(void(*draw)(void));
void close_window();

#endif
