#version 410 core

uniform vec2 u_resolution;
out vec4 frag_color;

void main() {
	vec2 norm_point = gl_FragCoord/u_resolution;
}
