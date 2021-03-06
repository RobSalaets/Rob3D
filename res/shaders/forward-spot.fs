#version 120
#include "lighting.fsh"

uniform SpotLight R_spotLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos, float specCoefficient)
{
	return CalcSpotLight(R_spotLight, normal, worldPos, specCoefficient);
}

#include "lightingMain.fsh"
