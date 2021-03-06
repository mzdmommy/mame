$input v_texcoord0, v_texcoord1, v_texcoord2, v_texcoord3, v_texcoord4

/*
 * Copyright 2011-2015 Branimir Karadzic. All rights reserved.
 * License: http://www.opensource.org/licenses/BSD-2-Clause
 */

#include "common.sh"

SAMPLER2D(u_texColor, 0);
SAMPLER2D(u_texLum, 1);
SAMPLER2D(u_texBlur, 2);

void main()
{
	vec3 rgb = decodeRGBE8(texture2D(u_texColor, v_texcoord0) );
	float lum = clamp(decodeRE8(texture2D(u_texLum, v_texcoord0) ), 0.1, 0.7);

	vec3 Yxy = convertRGB2Yxy(rgb);

	float middleGray = u_tonemap.x;
	float whiteSqr   = u_tonemap.y;
	float treshold   = u_tonemap.z;
	float offset     = u_tonemap.w;

	float lp = Yxy.x * middleGray / (lum + 0.0001);
	Yxy.x = reinhard2(lp, whiteSqr);

	rgb = convertYxy2RGB(Yxy);

	vec4 blur = blur9(u_texBlur
					, v_texcoord0
					, v_texcoord1
					, v_texcoord2
					, v_texcoord3
					, v_texcoord4
					);

	rgb += 0.6 * blur.xyz;

	gl_FragColor = toGamma(vec4(rgb, 1.0) );
}
