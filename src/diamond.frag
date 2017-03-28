// Created by javier maldonado
// from code by inigo quilez - iq/2016
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// runs at 60
// geforce gt750m
// 1600 x 1600
// horizon at top third of screen
// rivers
// towns
// biomes
// farms
// roads

uniform vec3 iResolution; // viewport resolution (z is pixel aspect ratio, usually 1.0)
uniform float iGlobalTime; // Current time in seconds
uniform int iFrame; // Current frame
uniform float iFrameRate; // Number of frames rendered per second
uniform sampler2D iChanneli; // Sampler for input textures i

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 p = fragCoord/iResolution.xy;

	vec3 color = texture( iChannel0, p ).xyz;
	//vec3 col = texelFetch( iChannel0, ivec2(fragCoord-0.5), 0 ).xyz;

	color *= 0.5 + 0.5*pow( 16.0*p.x*p.y*(1.0-p.x)*(1.0-p.y), 0.05 );

	fragColor = vec4( color, 1.0 );
}

// Normals are analytical (true derivatives) for the terrain and for the clouds, that
// includes the noise, the fbm and the smoothsteps involved chain derivatives correctly.
//
// See here for more info: http://iquilezles.org/www/articles/morenoise/morenoise.htm
//
// Lighting and art composed for this shot/camera
//
// The trees are really cheap (ellipsoids with noise), but they kind of do the job in
// distance and low image resolutions.
//
// I used some cheap reprojection technique to smooth out the render, although it creates
// halows and blurs the image way too much (I don't the time now to do the tricks used in
// TAA). Enable the STATIC_CAMERA define to see a sharper image.


//#define STATIC_CAMERA
#define LOWQUALITY

//==========================================================================================
// general utilities
//==========================================================================================

float sdEllipsoidY( in vec3 p, in vec2 r )
{
	return (length( p/r.xyx ) - 1.0) * r.x;
}

// return smoothstep and its derivative
vec2 smoothstepd( float a, float b, float x)
{
	if( x<a ) return vec2( 0.0, 0.0 );
	if( x>b ) return vec2( 1.0, 0.0 );
	float ir = 1.0/(b-a);
	x = (x-a)*ir;
	return vec2( x*x*(3.0-2.0*x), 6.0*x*(1.0-x)*ir );
}

mat3 setCamera( in vec3 ro, in vec3 ta, float cr )
{
	vec3 cw = normalize(ta-ro);
	vec3 cp = vec3(sin(cr), cos(cr),0.0);
	vec3 cu = normalize( cross(cw,cp) );
	vec3 cv = normalize( cross(cu,cw) );
	return mat3( cu, cv, cw );
}

//==========================================================================================
// hashes
//==========================================================================================

float hash1( vec2 p )
{
	p  = 50.0*fract( p*0.3183099 );
	return fract( p.x*p.y*(p.x+p.y) );
}

float hash1( float n )
{
	return fract( n*17.0*fract( n*0.3183099 ) );
}

vec2 hash2( float n ) { return fract(sin(vec2(n,n+1.0))*vec2(43758.5453123,22578.1459123)); }


vec2 hash2( vec2 p )
{
	const vec2 k = vec2( 0.3183099, 0.3678794 );
	p = p*k + k.yx;
	return fract( 16.0 * k*fract( p.x*p.y*(p.x+p.y)) );
}
//==========================================================================================
// specifics to the actual painting
//==========================================================================================

//------------------------------------------------------------------------------------------
// global
//------------------------------------------------------------------------------------------

const vec3  kSunDir = vec3(-0.624695,0.468521,-0.624695);
const float kMaxTreeHeight = 2.0;

vec3 fog( in vec3 col, float t )
{
	vec3 fogCol = vec3(0.4,0.6,1.15);
	return mix( col, fogCol, 1.0-exp(-0.000001*t*t) );
}

//------------------------------------------------------------------------------------------
// sky
//------------------------------------------------------------------------------------------

vec3 renderSky( in vec3 ro, in vec3 rd )
{
	// background sky
	vec3 col = 0.9*vec3(0.4,0.65,1.0) - rd.y*vec3(0.4,0.36,0.4);

	// clouds
	float t = (1000.0-ro.y)/rd.y;
	if( t>0.0 )
	{
		vec2 uv = (ro+t*rd).xz;
		float cl = fbm_9( uv*0.002 );
		float dl = smoothstep(-0.2,0.6,cl);
		col = mix( col, vec3(1.0), 0.4*dl );
	}

	// sun glare
	float sun = clamp( dot(kSunDir,rd), 0.0, 1.0 );
	col += 0.6*vec3(1.0,0.6,0.3)*pow( sun, 32.0 );

	return col;
}


//------------------------------------------------------------------------------------------
// main image making function
//------------------------------------------------------------------------------------------

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 o = hash2( float(iFrame) ) - 0.5;

	vec2 p = (-iResolution.xy + 2.0*(fragCoord+o))/ iResolution.y;

	//----------------------------------
	// setup
	//----------------------------------

	// camera
#ifdef  STATIC_CAMERA
	vec3 ro = vec3(0.0, -99.25, 5.0);
	vec3 ta = vec3(0.0, -99.0, 0.0);
#else
	float time = iGlobalTime;
	vec3 ro = vec3(0.0, -99.25, 5.0) + vec3(10.0*sin(0.02*time),0.0,-10.0*sin(0.2+0.031*time));
	vec3 ta = vec3(0.0, -98.25, -45.0 + ro.z );
#endif

	// ray
	mat3 ca = setCamera( ro, ta, 0.0 );
	vec3 rd = ca * normalize( vec3(p.xy,1.5));

	float resT = 1000.0;

	//----------------------------------
	// sky
	//----------------------------------

	vec3 col = renderSky( ro, rd );

	//----------------------------------
	// terrain
	//----------------------------------
	vec2 teDistance;
	float teShadow;

	vec2 tmima = vec2(15.0,1000.0);
	{
		vec4 res = renderTerrain( ro, rd, tmima, teShadow, teDistance, resT );
		col = col*(1.0-res.w) + res.xyz;
	}

	//----------------------------------
	// trees
	//----------------------------------
	if( teDistance.y>0.0 )
	{
		tmima = vec2( teDistance.y, (teDistance.x>0.0)?teDistance.x:tmima.y );
		vec4 res = renderTrees( ro, rd, tmima.x, tmima.y, teShadow, resT );
		col = col*(1.0-res.w) + res.xyz;
	}

	//----------------------------------
	// clouds
	//----------------------------------
	{
		vec4 res = renderClouds( ro, rd, 0.0, (teDistance.x>0.0)?teDistance.x:tmima.y, resT );
		col = col*(1.0-res.w) + res.xyz;
	}

	//----------------------------------
	// final
	//----------------------------------

	// sun glare
	float sun = clamp( dot(kSunDir,rd), 0.0, 1.0 );
	col += 0.25*vec3(1.0,0.4,0.2)*pow( sun, 4.0 );

	// gamma
	col = sqrt(col);

	//----------------------------------
	// color grading
	//----------------------------------

	col = col*0.15 + 0.85*col*col*(3.0-2.0*col);            // contrast
	col = pow( col, vec3(1.0,0.92,1.0) );   // soft green
	col *= vec3(1.02,0.99,0.99);            // tint red
	col.z = (col.z+0.1)/1.1;                // bias blue
	col = mix( col, col.yyy, 0.15 );       // desaturate

	col = clamp( col, 0.0, 1.0 );


	//------------------------------------------
	// reproject from previous frame and average
	//------------------------------------------

	mat4 oldCam = mat4( textureLod(iChannel0,vec2(0.5,0.5)/iResolution.xy, 0.0),
			textureLod(iChannel0,vec2(1.5,0.5)/iResolution.xy, 0.0),
			textureLod(iChannel0,vec2(2.5,0.5)/iResolution.xy, 0.0),
			0.0, 0.0, 0.0, 1.0 );

	// world space
	vec4 wpos = vec4(ro + rd*resT,1.0);
	// camera space
	vec3 cpos = (wpos*oldCam).xyz; // note inverse multiply
	// ndc space
	vec2 npos = 1.5 * cpos.xy / cpos.z;
	// screen space
	vec2 spos = 0.5 + 0.5*npos*vec2(iResolution.y/iResolution.x,1.0);
	// undo dither
	spos -= o/iResolution.xy;
	// raster space
	vec2 rpos = spos * iResolution.xy;

	if( rpos.y<1.0 && rpos.x<3.0 )
	{
	}
	else
	{
		vec3 ocol = textureLod( iChannel0, spos, 0.0 ).xyz;
		if( iFrame==0 ) ocol = col;
		col = mix( ocol, col, 0.1 );
	}

	//----------------------------------

	if( fragCoord.y<1.0 && fragCoord.x<3.0 )
	{
		if( abs(fragCoord.x-2.5)<0.5 ) fragColor = vec4( ca[2], -dot(ca[2],ro) );
		if( abs(fragCoord.x-1.5)<0.5 ) fragColor = vec4( ca[1], -dot(ca[1],ro) );
		if( abs(fragCoord.x-0.5)<0.5 ) fragColor = vec4( ca[0], -dot(ca[0],ro) );
	}
	else
	{
		fragColor = vec4( col, 1.0 );
	}
}
