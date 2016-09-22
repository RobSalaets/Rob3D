
in vec2 texCoord0;
in vec3 worldPos0;
in vec4 shadowMapCoords0;
in mat3 tbnMatrix;

uniform sampler2D normalMap;
uniform sampler2DArray diffuseTextures;

uniform float numTextures;
in float texPol;

uniform sampler2D R_shadowMap;
uniform float R_shadowVarianceMin;
uniform float R_shadowLightBleedReduction;

uniform vec3 R_fogColor;
uniform float R_fogStart;
uniform float R_fogSpan;

layout(location = 0) out vec4 outColor;
layout(early_fragment_tests) in;

#include "lighting.glh"

