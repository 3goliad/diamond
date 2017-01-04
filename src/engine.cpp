#include <GL/glew.h>
#include <GL/glut.h>

#include "engine.hpp"
#include "shaders.hpp"

GLuint shaderProgram;

void init_engine() {
  shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");
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
