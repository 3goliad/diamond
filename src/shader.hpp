#pragma once

#include <fstream>
#include <string>
#include <cerrno>

#include <GL/glew.h>

std::string get_file_contents(std::string filename)
{
    std::ifstream in(filename, std::ios::in | std::ios::binary);
    if (in)
    {
        std::string contents;
        in.seekg(0, std::ios::end);
        contents.resize(in.tellg());
        in.seekg(0, std::ios::beg);
        in.read(&contents[0], contents.size());
        in.close();
        return(contents);
    }
    throw(errno);
}

class shader {
    public:
        GLuint program;
        // shaders cannot be compiled before we have an opengl context, but must be
        // compiled before they are used
        shader(std::string name) {
            std::string vertex_shader = get_file_contents("shaders/" + name + ".vert");
            std::string fragment_shader= get_file_contents("shaders/" + name + ".frag");

            // Vertex Shader
            GLuint vertex = compile_shader(GL_VERTEX_SHADER, vertex_shader.c_str());
            check_compile_err(vertex, GL_COMPILE_STATUS);
            // Fragment Shader
            GLuint fragment = compile_shader(GL_FRAGMENT_SHADER, fragment_shader.c_str());
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
        }

        // Uses the current shader
        void use()
        {
            glUseProgram(this->program);
        }

    private:
        // compiles the given source as a shader of shader_type
        GLuint compile_shader(GLenum shader_type, const GLchar* source)
        {
            GLuint shader = glCreateShader(shader_type);
            glShaderSource(shader, 1, &source, NULL);
            glCompileShader(shader);
            check_compile_err(shader, GL_COMPILE_STATUS);
            return shader;
        }

        // checks either compile or link errors
        void check_compile_err(GLuint shader, GLenum pname)
        {
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
