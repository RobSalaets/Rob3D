#version 130
attribute vec3 position;
attribute vec2 texCoord;

varying vec2 texCoord0;

uniform mat4 T_MVP;
uniform mat4 T_model;
uniform vec4 R_clipPlane;


void main()
{
      gl_Position = T_MVP * vec4(position, 1.0);
      texCoord0 = texCoord; 
	  gl_ClipDistance[0] = dot(T_model * vec4(position, 1.0), R_clipPlane);
}

