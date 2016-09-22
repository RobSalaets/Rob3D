#version 120
#include "lighting.fsh"

uniform PointLight R_pointLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos, float specCoefficient)
{
	return CalcPointLight(R_pointLight, normal, worldPos, specCoefficient);
}

#include "lightingMain.fsh"
