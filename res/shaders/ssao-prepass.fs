#version 120

varying vec2 texCoord0;
varying float depth;

void main()
{
	gl_FragColor = vec4(depth,depth,depth, depth);
}
