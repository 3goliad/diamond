#ifndef WINDOW_H
#define WINDOW_H
#include <GL/glut.h>

#include "engine.hpp"

void reshape(int x, int y) {
  glViewport(0, 0, x, y);
}

void init_glut(int *argc, char* argv[], GLuint w, GLuint h) {
  unsigned int glutDisplayMode = GLUT_RGBA | GLUT_DOUBLE;
  glutInit(argc, argv);
  glutInitWindowSize(w, h);
  glutInitDisplayMode(glutDisplayMode);
  glutCreateWindow("diamond");
  glutReshapeFunc(reshape);
  glutDisplayFunc(draw);  
  glewInit();
}

void start_glut() {
  glutMainLoop();
}

#endif
