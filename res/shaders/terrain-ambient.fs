#version 420
layout(early_fragment_tests) in;
in vec2 texCoord0;
uniform vec3 R_ambient;
uniform sampler2DArray diffuseTextures;

uniform float numTextures;
in float texPol;
layout(location = 0) out vec4 outColor;


void main()
{
	
	float texIndex = clamp(texPol * numTextures, 0, numTextures - 1.0);
	vec3 diffuseSum = texture2DArray(diffuseTextures, vec3(texCoord0, floor(texIndex))).xyz * (1.0 - fract(texIndex)) + texture2DArray(diffuseTextures, vec3(texCoord0, ceil(texIndex))).xyz * fract(texIndex);
	outColor = vec4(diffuseSum, 1.0) * vec4(R_ambient, 1);
}
