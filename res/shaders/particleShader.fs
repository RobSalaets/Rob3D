#version 120

varying vec2 texCoord0;
varying vec3 color0;

uniform sampler2D diffuse;

void main()
{
	gl_FragColor =  texture2D(diffuse, texCoord0) * vec4(color0,1.0);
}
