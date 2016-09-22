#version 120

varying vec2 texCoord0;
varying vec4 lightPosSS;
uniform sampler2D R_filterTexture;
uniform float density;
uniform float weight;
uniform float decay;
uniform float exposure;
const float NUM_SAMPLES = 50;

void main()
{
	
	vec2 texCoord = texCoord0;
	vec2 deltaTexCoord = (texCoord - (lightPosSS.xyz / lightPosSS.w).xy);
	
	deltaTexCoord *= 1.0 / NUM_SAMPLES * density;
	vec3 color = texture2D(R_filterTexture, texCoord0).xyz;
	float illuminationDecay = 1.0;
	
	for(int i = 0; i < NUM_SAMPLES; i++){
		texCoord -= deltaTexCoord;
		vec3 samp = texture2D(R_filterTexture, texCoord).xyz;
		samp *= illuminationDecay * weight;
		color += samp;
		illuminationDecay *= decay;
	}
	
	gl_FragColor = vec4(color * exposure, 1);   
}
