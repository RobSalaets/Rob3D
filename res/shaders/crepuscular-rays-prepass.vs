#version 130
attribute vec3 position;
attribute vec2 texCoord;

uniform mat4 T_MVP;
varying vec2 texCoord0;

void main()
{
      gl_Position = T_MVP * vec4(position, 1.0);
	  texCoord0 = texCoord;
}

