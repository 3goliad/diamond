#include "shaders.hpp"

#include <algorithm>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

GLuint load_shader(const char *path, GLenum shader_type) {
  // Read shader
  std::string shader_str;
  std::ifstream source(path);

  if (!source) {
    printf("Unable to open file %s\n", path);
  }

  shader_str.assign((std::istreambuf_iterator<char>(source)),
                      std::istreambuf_iterator<char>());
  const char *shader_src = shader_str.c_str();


  // compile
  GLuint shader = glCreateShader(shader_type);
  std::cout << "Compiling shader." << std::endl;
  glShaderSource(shader, 1, &shader_src, nullptr);
  glCompileShader(shader);

  // check for errors
  GLint result = GL_FALSE;
  int log_length;
  glGetShaderiv(shader, GL_COMPILE_STATUS, &result);
  glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &log_length);
  std::vector<GLchar> shader_error((log_length > 1) ? log_length : 1);
  glGetShaderInfoLog(shader, log_length, nullptr, &shader_error[0]);
  std::cout << &shader_error[0] << std::endl;

  return shader;
}

GLuint load_program(const char *vertex_path, const char *fragment_path) {
  std::cout << "Loading vertex shader" << std::endl;
  GLuint vs = load_shader(vertex_path, GL_VERTEX_SHADER);
  std::cout << "Loading fragment shader" << std::endl;
  GLuint fs = load_shader(fragment_path, GL_FRAGMENT_SHADER);
  std::cout << "Linking program" << std::endl;
  GLuint program = glCreateProgram();
  glAttachShader(program, vs);
  glAttachShader(program, fs);
  glLinkProgram(program);

  //check for errors
  GLint result = GL_FALSE;
  int log_length;
  glGetProgramiv(program, GL_LINK_STATUS, &result);
  glGetProgramiv(program, GL_INFO_LOG_LENGTH, &log_length);
  std::vector<char> programError((log_length > 1) ? log_length : 1);
  glGetProgramInfoLog(program, log_length, NULL, &programError[0]);
  std::cout << &programError[0] << std::endl;

  glDeleteShader(fs);
  glDeleteShader(fs);
  return program;
}
