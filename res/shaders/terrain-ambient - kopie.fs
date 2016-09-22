#version 130

varying vec2 texCoord0;
uniform vec3 R_ambient;
uniform sampler2DArray diffuseTextures;

uniform float numTextures;
varying float texPol;



void main()
{
	float texIndex = clamp(texPol * numTextures, 0, numTextures - 1.0);
	vec3 diffuseSum = texture2DArray(diffuseTextures, vec3(texCoord0, floor(texIndex))).xyz * (1.0 - fract(texIndex)) + texture2DArray(diffuseTextures, vec3(texCoord0, ceil(texIndex))).xyz * fract(texIndex);
	gl_FragColor = vec4(diffuseSum, 1.0) * vec4(R_ambient, 1);
}
