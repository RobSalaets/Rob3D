#version 120
attribute vec3 position;
attribute vec2 texCoord;

varying vec2 texCoord0;
varying vec3 worldPos0;
varying vec4 reflectionCoord0; 
varying vec4 refractionCoord0;

uniform mat4 T_MVP;
uniform mat4 T_model;
uniform mat4 R_waterReflectionMatrix;
uniform mat4 R_biasMatrix;


void main()
{
	 gl_Position = T_MVP * vec4(position, 1.0);
	 worldPos0 = (T_model * vec4(position, 1.0)).xyz;
     texCoord0 = texCoord; 
	 reflectionCoord0 = R_biasMatrix * R_waterReflectionMatrix * vec4(position, 1.0);
	 refractionCoord0 = R_biasMatrix * T_MVP * vec4(position, 1.0);
}

