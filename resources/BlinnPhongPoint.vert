#version 430 core

// Algorithm based on algorithm from
// Sellers, Graham, Wright, Richard S., Haemel, Nicholas (2014).
// OpenGL Super Bible. 6th edition. Addison Wesley.

// Point light source
// To use this shader set for generating a directional light source,
// put the light source very far away from the objects to be lit

// Author: Karsten Lehn
// Version: 25.10.2017, 16.9.2019

// position and color of vertex as input vertex attribute
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vInColor;
layout (location = 2) in vec3 vNormal;
//layout (location = 3) in vec3 vNormal;
// Projection and model-view matrix as input uniform variables
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;
layout (location = 2) uniform mat4 nMatrix;
layout (location = 3) uniform vec4 lightPosition;

// Outputs from vertex shader
out VS_OUT
{
    vec3 N;
    vec3 L;
    vec3 V;
} vs_out;

void main(void)
{
    // Calculate view-space coordinate
    vec4 P = mvMatrix * vec4(vPosition, 1.0);

    // Calculate normal in view-space
//    vs_out.N = mat3(nvMatrix) * vNormal;
    vs_out.N = (mat4(nMatrix) * vec4(vNormal, 0.0)).xyz;

    // Calculate light vector
    vs_out.L = lightPosition.xyz - P.xyz;

    // Calculate view vector
    vs_out.V = -P.xyz;

    // Calculate the clip-space position of each vertex
    gl_Position = pMatrix * P;
}


