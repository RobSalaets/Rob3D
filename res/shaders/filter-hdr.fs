#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;

vec3 toneMap(vec3 x)
{
    float A = 0.15;
	float B = 0.50;
	float C = 0.10;
	float D = 0.20;
	float E = 0.02;
	float F = 0.30;

    return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}

void main()
{
	vec3 color = texture2D(R_filterTexture, texCoord0).xyz;
	//vec3 hdr = 1.0 - exp(-color * 1.0));
	//if(texCoord0.x < 0.5){
	gl_FragColor = vec4(toneMap(color)*2.3/toneMap(vec3(11.2)),1.0);
	//}else{
	//gl_FragColor = vec4(color,1.0);
	//}
	
}
