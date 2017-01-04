#include <cstdlib>
#include <iostream>

#include <GL/glew.h>
#include <GL/glut.h>
#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif


#include "shaders.hpp"

GLuint shaderProgram;

// Window dimensions
const GLuint SCREEN_WIDTH = 800;
const GLuint SCREEN_HEIGHT = 600;

void reshape(int x, int y) {
  glViewport(0, 0, x, y);
}

void draw() {
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

  glutSwapBuffers();
}

int main(int argc, char* argv[]) {
  unsigned int glutDisplayMode = GLUT_RGBA | GLUT_DOUBLE;
  glutInit(&argc, argv);
  glutInitWindowSize(SCREEN_WIDTH, SCREEN_HEIGHT);
  glutInitDisplayMode(glutDisplayMode);
  glutCreateWindow("diamond");
  glutReshapeFunc(reshape);
  glutDisplayFunc(draw);  
  glewInit();

  // load shaders
  shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");

#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(draw, 60, 1);
#else
  glutMainLoop();
#endif
}
