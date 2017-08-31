#version 130
attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec3 color0;

uniform mat4 T_MVP;


void main()
{
      gl_Position = T_MVP * vec4(position, 1.0);
      color0 = normal; 
}

