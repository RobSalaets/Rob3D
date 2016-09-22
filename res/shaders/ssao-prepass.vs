#version 120
attribute vec3 position;
attribute vec2 texCoord;

varying vec2 texCoord0;
varying float depth;

uniform mat4 T_MVP;
uniform mat4 T_model;
uniform mat4 T_view;
uniform float C_far;
uniform float C_near;

void main()
{
      gl_Position = T_MVP * vec4(position, 1.0);
      texCoord0 = texCoord; 
	  vec3 vPos = (T_view * T_model * vec4(position, 1.0)).xyz;
	  depth = (vPos.z-C_near)/C_far;
}

