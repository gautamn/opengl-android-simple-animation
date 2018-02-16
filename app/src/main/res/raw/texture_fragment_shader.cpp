precision mediump float;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;

void main(){

     lowp vec4 textureColor = texture2D(u_Texture, v_TexCoordinate);
     gl_FragColor = vec4((textureColor.rgb + vec3(0.1)), textureColor.w);
}
