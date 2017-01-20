#pragma once

#include <stdio.h>

#include <GL/glew.h>

// vertex shader
const GLchar* vertex_shader = R"shader(
#version 100

attribute in vec3 position;
attribute in vec2 texCoord;

out vec2 TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0f);
    TexCoord = vec2(texCoord.x, 1.0 - texCoord.y);
}

)shader";

// fragment shader
const GLchar* fragment_shader = R"shader(
#version 100
in vec2 TexCoord;

out vec4 color;

uniform sampler2D ourTexture1;
uniform sampler2D ourTexture2;

void main()
{
    glFragColor = mix(texture(ourTexture1, TexCoord), texture(ourTexture2, TexCoord), 0.2);
}

)shader";

class shader {
public:
  GLuint program;
  bool is_compiled;
  //shaders cannot be compiled before we have an opengl context, but must be
  //compiled before they are used
  shader(const GLchar* vertex_shader, const GLchar* fragment_shader) : is_compiled(false) {
    // Vertex Shader
    GLuint vertex = compile_shader(GL_VERTEX_SHADER, vertex_shader);
    check_compile_err(vertex, GL_COMPILE_STATUS);
    // Fragment Shader
    GLuint fragment = compile_shader(GL_FRAGMENT_SHADER, fragment_shader);
    check_compile_err(fragment, GL_COMPILE_STATUS);
    // Shader Program
    this->program = glCreateProgram();
    glAttachShader(this->program, vertex);
    glAttachShader(this->program, fragment);
    glLinkProgram(this->program);
    check_compile_err(this->program, GL_LINK_STATUS);
    // Delete the shaders as they're linked into our program now and no longer
    // necessery
    glDeleteShader(vertex);
    glDeleteShader(fragment);
    this->is_compiled = true;
  }

  // Uses the current shader
  void use() {
    if(this->is_compiled) {
      glUseProgram(this->program); 
    } else {
      printf("You tried to use a shader without compiling it!\n");
      printf("bad things are gonna start happening now\n");
    }
  }

private:
  
  // compiles the given source as a shader of shader_type
  GLuint compile_shader(GLenum shader_type, const GLchar *source) {
    GLuint shader = glCreateShader(shader_type);
    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);
    check_compile_err(shader, GL_COMPILE_STATUS);
    return shader;
  }

  // checks either compile or link errors
  void check_compile_err(GLuint shader, GLenum pname) {
    GLint success;
    GLchar infoLog[1024];
    if (pname == GL_COMPILE_STATUS) {
      glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
      if (!success) {
        glGetShaderInfoLog(shader, 1024, NULL, infoLog);
        printf("shader compile error: %s", infoLog);
      }
    } else if (pname == GL_LINK_STATUS) {
      glGetProgramiv(shader, GL_LINK_STATUS, &success);
      if (!success) {
        glGetProgramInfoLog(shader, 1024, NULL, infoLog);
        printf("program link error: %s", infoLog);
      }
    } else {
      printf("an unknown error occurred in the shader compilation process\n");
    }
  };
};
