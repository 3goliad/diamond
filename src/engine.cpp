#include <GL/glew.h>

#include "engine.hpp"
#include "shaders.hpp"

GLuint shaderProgram;

void init_engine() {
  shaderProgram = load_program("shaders/vs.glsl", "shaders/fs.glsl");

}

void draw() {
  glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
  glClear(GL_COLOR_BUFFER_BIT);

  glUseProgram(shaderProgram);
  GLfloat vertices[] = {
      -0.5f, -0.5f, 0.0f, // Left
      0.5f,  -0.5f, 0.0f, // Right
      0.0f,  0.5f,  0.0f  // Top
  };
  GLuint VBO;
  glGenBuffers(1, &VBO);
  glBindBuffer(GL_ARRAY_BUFFER, VBO);
  glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
  glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (GLvoid*)0);
  glEnableVertexAttribArray(0);

  glDrawArrays(GL_TRIANGLES, 0, 3);
}
