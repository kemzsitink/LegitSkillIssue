uniform vec2 location;
uniform vec2 rectSize;
uniform vec4 color;
uniform float radius;

float roundedRectSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b + r, 0.0)) - r;
}

void main() {
    vec2 rectCenter = location + rectSize / 2.0;
    float dist = roundedRectSDF(gl_FragCoord.xy - rectCenter, rectSize / 2.0, radius);
    
    // Anti-aliasing
    float alpha = color.a * (1.0 - smoothstep(0.0, 1.5, dist));
    
    if (dist > 1.5) discard;
    
    gl_FragColor = vec4(color.rgb, alpha);
}
