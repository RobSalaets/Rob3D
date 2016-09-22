#version 120
attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 ssTexCoord;
varying vec3 vViewRay;
varying vec3 vNormal;

uniform mat4 T_model;
uniform mat4 T_view;
uniform mat4 T_MVP;



void main()
{
      gl_Position = T_MVP * vec4(position, 1.0);
      ssTexCoord = T_MVP * vec4(position, 1.0);
      vViewRay = (T_view * T_model * vec4(position, 1.0)).xyz;
	  vNormal = (T_view * T_model * vec4(normal, 0.0)).xyz;
}

