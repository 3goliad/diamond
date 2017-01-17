#include <vector>

#include <GL/glew.h>
#include "glm/glm.hpp"

#include "generate.hpp"
#include "shader.hpp"
#include "camera.hpp"
#include "window.hpp"

#include "render.hpp"

namespace render {
shader default_program;

void init() {
  GLfloat vertices[];
  GLfloat indices[];

  glEnable(GL_DEPTH_TEST);

  default_program.compile();
  default_program.use();

  GLuint vertex_buffer;
  GLuint index_buffer;
  glGenBuffers(1, &vertex_buffer);
  glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
  glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

  glGenBuffers(1, &index_buffer);
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_buffer);
  glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices,
               GL_STATIC_DRAW);

  //glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
  //// Position attribute
  //glVertexAttribPointer(attribute_index, num_components_in_attribute, GL_FLOAT,
  //                      GL_FALSE, stride, (GLvoid *)0);
  //glEnableVertexAttribArray(0);

  //// TexCoord attribute
  //glVertexAttribPointer(attribute_index, 2, GL_FLOAT, GL_FALSE,
  //                      5 * sizeof(GLfloat), (GLvoid *)(3 * sizeof(GLfloat)));
  //glEnableVertexAttribArray(2);
  glBindBuffer(GL_ARRAY_BUFFER, 0);

  //// Load and create a texture
  //GLuint texture1;
  //GLuint texture2;
  //// --== TEXTURE 1 == --
  //glGenTextures(1, &texture1);
  //glBindTexture(GL_TEXTURE_2D, texture1); // All upcoming GL_TEXTURE_2D
  //                                        // operations now have effect on our
  //                                        // texture object
  //// Set our texture parameters
  //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
  //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
  //// Set texture filtering
  //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  //// Load, create texture and generate mipmaps
  //int width, height;
  //unsigned char *image = generate::gen_default_texture(&width, &height);
  //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB,
  //             GL_UNSIGNED_BYTE, image);
  //glGenerateMipmap(GL_TEXTURE_2D);
  //SOIL_free_image_data(image);
  //glBindTexture(GL_TEXTURE_2D, 0); // Unbind texture when done, so we won't
}

void draw() {
  window::update();

  // Clear the colorbuffer
  glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  // Draw our first triangle
  default_program.use();

  // Bind Textures using texture units
  //glActiveTexture(GL_TEXTURE0);
  //glBindTexture(GL_TEXTURE_2D, gen_texture);
  //glUniform1i(glGetUniformLocation(default_program.program, "gen_texture"), 0);

  // Create camera transformation
  glm::mat4 view;
  view = camera::getviewmat4();
  glm::mat4 projection;
  projection = glm::perspective(
      camera::zoom, (float)screenWidth / (float)screenHeight, 0.1f, 1000.0f);
  // Get the uniform locations
  GLint modelLoc = glGetUniformLocation(ourShader.Program, "model");
  GLint viewLoc = glGetUniformLocation(ourShader.Program, "view");
  GLint projLoc = glGetUniformLocation(ourShader.Program, "projection");
  // Pass the matrices to the shader
  glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
  glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));

  glBindVertexArray(VAO);
  // Calculate the model matrix for each object and pass it to shader before
  // drawing
  glm::mat4 model;
  model = glm::translate(model, cubePositions[i]);
  GLfloat angle = 20.0f * i;
  model = glm::rotate(model, angle, glm::vec3(1.0f, 0.3f, 0.5f));
  glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));

  glDrawArrays(GL_TRIANGLES, 0, 36);
 
  glBindVertexArray(0);

  window::swap();
}
}
