#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;

float roundNum(float f){
	f = floor(abs(f)+0.5);
	return f;
}

void main()
{
	float detail = 0.15;
	vec4 color =  texture2D(R_filterTexture, texCoord0);
	color.x = roundNum(color.x / detail) * detail;
	color.y = roundNum(color.y / detail) * detail;
	color.z = roundNum(color.z / detail) * detail;
	gl_FragColor = vec4(color.xyz, 1.0);
}
