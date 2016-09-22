#version 120
attribute vec3 position;
attribute vec2 texCoord;

uniform vec3 R_lightPos;

varying vec2 texCoord0;
varying vec4 lightPosSS;
uniform mat4 T_PrePPMVP;
uniform mat4 R_biasMatrix;

void main()
{
    gl_Position = vec4(position, 1.0);
	lightPosSS = R_biasMatrix * T_PrePPMVP * vec4(R_lightPos, 1.0);
    texCoord0 = texCoord; 
}


