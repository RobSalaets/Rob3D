#version 130

varying vec2 texCoord0;
uniform sampler2D rayMap;
uniform float hasRays;

void main()
{
	if(hasRays == 1.0)
		gl_FragColor = texture2D(rayMap, texCoord0);
	else
		gl_FragColor = vec4(0,0,0,1);
}
