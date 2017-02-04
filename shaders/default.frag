#version 100
in vec2 TexCoord;

out vec4 color;

uniform sampler2D ourTexture1;
uniform sampler2D ourTexture2;

void main()
{
  glFragColor = mix(
      texture(ourTexture1, TexCoord), texture(ourTexture2, TexCoord), 0.2);
}
