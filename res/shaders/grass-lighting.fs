#version 420

in vec2 texCoord0;
in vec3 normal0;

uniform sampler2D diffuse;
uniform vec3 lightColor;
uniform vec3 lightDirection;
uniform float lightIntensity;
uniform vec3 R_ambient;

layout(location = 0) out vec4 outColor;

void main()
{
	float diffuseF = dot(normalize(normal0), lightDirection);
	vec4 diffuseColor = vec4(R_ambient,1.0);
	if(diffuseF > 0.0){
		diffuseColor += vec4(lightColor, 1.0) * lightIntensity * diffuseF * 2;
	}
	
	vec4 color = texture2D(diffuse, texCoord0);
	if(color.a < 0.5){
		discard;
	}
	outColor =  color * diffuseColor;
}
