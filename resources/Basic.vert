#version 430 core
// Basic Vertex Shader that performs geometric transformations

// Author: Karsten Lehn
// Version: 7.10.2018

// User definied in variables
// position and color of vertex
layout (location = 0) in vec3 vposition;
layout (location = 1) in vec3 vInNormal;
// Definition of uniforms
// Projection and model-view matrix
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;

// User definied out variable
// Color of the vertex
out vec4 vColor;

void main(void) {
	// Calculation of the model-view-perspective transform
	gl_Position = pMatrix * mvMatrix * vec4(vposition, 0.1);
	// The color information is forwarded
	// to the next pipeline stage
	float brightness = max(vInNormal.y, 0.0);
	vColor = vec4(vec3(brightness), 1.0);
}
