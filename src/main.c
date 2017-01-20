#include <cstdlib>
#include <cstdio>

#ifdef __EMSCRIPTEN__
#include <emscripten/emscripten.h>
#endif

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include "shader.h"
#include "window.h"
#include "camera.h"

// Window dimensions
const unsigned int SCREEN_WIDTH = 800;
const unsigned int SCREEN_HEIGHT = 600;

// frame times
float current_frame = 0.0f;
float last_frame = 0.0f;
float delta_time = 0.0f;

void draw();
void render_init();

int main(int argc, char* argv[]) {
  GLFWwindow *window = window_create(SCREEN_WIDTH, SCREEN_HEIGHT);
  opengl_init(window);
  camera::init(window);
  glfwSetKeyCallback(window, camera::key_callback);
  glfwSetCursorPosCallback(window, camera::mouse_callback);
  glfwSetScrollCallback(window, camera::scroll_callback);
  shader default_program(vertex_shader, fragment_shader);
  default_program.use();
  render_init();
#ifdef __EMSCRIPTEN__
  emscripten_set_main_loop(draw, 0, 1);
#else
  while (glfwWindowShouldClose(window) == 0) {
    draw();
  }
#endif
  glfwDestroyWindow(window);
  glfwTerminate();
  exit(EXIT_SUCCESS);
}

//void draw() {
//  glfwPollEvents();
//
//  static float current_frame = glfwGetTime();
//  static float delta_time = current_frame - last_frame;
//  static float last_frame = current_frame;
//
//  camera::set_velocity(delta_time);
//}

void render_init() {
  //GLfloat vertices[4];
  //GLfloat indices[4];

  //glEnable(GL_DEPTH_TEST);


  //GLuint vertex_buffer;
  //GLuint index_buffer;
  //glGenBuffers(1, &vertex_buffer);
  //glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
  //glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

  //glGenBuffers(1, &index_buffer);
  //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_buffer);
  //glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices,
  //             GL_STATIC_DRAW);

  ////glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
  ////// Position attribute
  ////glVertexAttribPointer(attribute_index, num_components_in_attribute, GL_FLOAT,
  ////                      GL_FALSE, stride, (GLvoid *)0);
  ////glEnableVertexAttribArray(0);

  ////// TexCoord attribute
  ////glVertexAttribPointer(attribute_index, 2, GL_FLOAT, GL_FALSE,
  //                      5 * sizeof(GLfloat), (GLvoid *)(3 * sizeof(GLfloat)));
  //glEnableVertexAttribArray(2);
  //glBindBuffer(GL_ARRAY_BUFFER, 0);

  ////// Load and create a texture
  ////GLuint texture1;
  ////GLuint texture2;
  ////// --== TEXTURE 1 == --
  ////glGenTextures(1, &texture1);
  ////glBindTexture(GL_TEXTURE_2D, texture1); // All upcoming GL_TEXTURE_2D
  ////                                        // operations now have effect on our
  ////                                        // texture object
  ////// Set our texture parameters
  ////glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
  ////glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
  ////// Set texture filtering
  ////glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  ////glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  ////// Load, create texture and generate mipmaps
  ////int width, height;
  ////unsigned char *image = generate::gen_default_texture(&width, &height);
  ////glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB,
  ////             GL_UNSIGNED_BYTE, image);
  ////glGenerateMipmap(GL_TEXTURE_2D);
  ////SOIL_free_image_data(image);
  ////glBindTexture(GL_TEXTURE_2D, 0); // Unbind texture when done, so we won't
}

void draw() {
//  // Clear the colorbuffer
//  glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
//  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//  // Bind Textures using texture units
//  //glActiveTexture(GL_TEXTURE0);
//  //glBindTexture(GL_TEXTURE_2D, gen_texture);
//  //glUniform1i(glGetUniformLocation(default_program.program, "gen_texture"), 0);
//
//  // Create camera transformation
//  glm::mat4 view;
//  view = camera::view();
//  glm::mat4 projection;
//  projection = glm::perspective(
//      camera::zoom, (float)SCREEN_WIDTH / (float)SCREEN_HEIGHT, 0.1f, 1000.0f);
//  // Get the uniform locations
//  GLint model_uniform = glGetUniformLocation(default_program.program, "model");
//  GLint view_uniform = glGetUniformLocation(default_program.program, "view");
//  GLint proj_uniform = glGetUniformLocation(default_program.program, "projection");
//  // Pass the matrices to the shader
//  glUniformMatrix4fv(view_uniform, 1, GL_FALSE, glm::value_ptr(view));
//  glUniformMatrix4fv(proj_uniform, 1, GL_FALSE, glm::value_ptr(projection));
//
//  glBindVertexArray(VAO);
//  // Calculate the model matrix for each object and pass it to shader before
//  // drawing
//  glm::mat4 model;
//  //model = glm::translate(model, cubePositions[i]);
//  //GLfloat angle = 20.0f * i;
//  //model = glm::rotate(model, angle, glm::vec3(1.0f, 0.3f, 0.5f));
//  glUniformMatrix4fv(model_uniform, 1, GL_FALSE, glm::value_ptr(model));
//
//  glDrawArrays(GL_TRIANGLES, 0, 36);
// 
//  glBindVertexArray(0);
}

void log_renderer() {
    // get version info
    const GLubyte *renderer = glGetString(GL_RENDERER); // get renderer string
    const GLubyte *version = glGetString(GL_VERSION);   // version as a string
    printf("renderer: %s\n", renderer);
    printf("opengl version: %s\n", version);
}
