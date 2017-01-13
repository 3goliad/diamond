#ifndef SHADERS_H
#define SHADERS_H

#include <GL/glew.h>
#include <algorithm>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

GLuint compile_shader(const char *shader_src, GLenum shader_type) {
  // compile
  GLuint shader = glCreateShader(shader_type);
  glShaderSource(shader, 1, &shader_src, nullptr);
  glCompileShader(shader);

  // check for errors
  GLint result = GL_FALSE;
  int log_length;
  glGetShaderiv(shader, GL_COMPILE_STATUS, &result);
  glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &log_length);
  std::vector<GLchar> shader_error((log_length > 1) ? log_length : 1);
  glGetShaderInfoLog(shader, log_length, nullptr, &shader_error[0]);
  if (log_length > 1) {
    std::cout << &shader_error[0] << std::endl;
  }

  return shader;
}

GLuint load_program() {
  const char *frag = R"shader(

void main() {
  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

)shader";

  const char *vert = R"shader(

attribute vec4 vPosition;

void main() {
  gl_Position = vPosition;
}

)shader";

  std::cout << "Compiling vertex shader" << std::endl;
  GLuint vs = compile_shader(vert, GL_VERTEX_SHADER);
  std::cout << "Compiling fragment shader" << std::endl;
  GLuint fs = compile_shader(frag, GL_FRAGMENT_SHADER);
  std::cout << "Linking program" << std::endl;
  GLuint program = glCreateProgram();
  glAttachShader(program, vs);
  glAttachShader(program, fs);
  glLinkProgram(program);

  // check for errors
  GLint result = GL_FALSE;
  int log_length;
  glGetProgramiv(program, GL_LINK_STATUS, &result);
  glGetProgramiv(program, GL_INFO_LOG_LENGTH, &log_length);
  std::vector<char> programError((log_length > 1) ? log_length : 1);
  glGetProgramInfoLog(program, log_length, NULL, &programError[0]);
  if (log_length > 1) {
    std::cout << &programError[0] << std::endl;
  }

  glDeleteShader(vs);
  glDeleteShader(fs);
  return program;
}
#endif
