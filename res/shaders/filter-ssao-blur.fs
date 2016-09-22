#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;
uniform sampler2D R_displayTexture;
uniform vec3 R_ssaoDimension;
const float blurSize = 7;

void main() {
	vec4 color = vec4(0.0);
	vec2 center = texCoord0;
	vec2 sample;
	float sum = 0.0;
	float coefG,coefZ,finalCoef;
	float Zp = texture2D(R_filterTexture, texCoord0).w;
	
	const float epsilon = 0.01;
	
	for(int i = int(-(blurSize-1)/2); i <= int((blurSize-1)/2); i++) {
		for(int j = int(-(blurSize-1)/2); j <= int((blurSize-1)/2); j++) {
			sample = center + vec2(i,j) / R_ssaoDimension.xy;
			coefG = 1 / blurSize;
			vec4 zTmp = texture2D(R_filterTexture, sample);
			coefZ = 1.0 / (epsilon + abs(Zp - zTmp.w));
			finalCoef = coefG * coefZ;
			sum += finalCoef;
			color += finalCoef * zTmp;
		}
	}

	gl_FragColor =  texture2D(R_displayTexture, texCoord0.xy) * (color / sum);
} 
