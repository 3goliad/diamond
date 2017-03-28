//==========================================================================================
// noises
//==========================================================================================

// value noise, and its analytical derivatives
vec4 noised( in vec3 x )
{
	vec3 p = floor(x);
	vec3 w = fract(x);

	vec3 u = w*w*w*(w*(w*6.0-15.0)+10.0);
	vec3 du = 30.0*w*w*(w*(w-2.0)+1.0);

	float n = p.x + 317.0*p.y + 157.0*p.z;

	float a = hash1(n+0.0);
	float b = hash1(n+1.0);
	float c = hash1(n+317.0);
	float d = hash1(n+318.0);
	float e = hash1(n+157.0);
	float f = hash1(n+158.0);
	float g = hash1(n+474.0);
	float h = hash1(n+475.0);

	float k0 =   a;
	float k1 =   b - a;
	float k2 =   c - a;
	float k3 =   e - a;
	float k4 =   a - b - c + d;
	float k5 =   a - c - e + g;
	float k6 =   a - b - e + f;
	float k7 = - a + b + c - d + e - f - g + h;

	return vec4( -1.0+2.0*(k0 + k1*u.x + k2*u.y + k3*u.z + k4*u.x*u.y + k5*u.y*u.z + k6*u.z*u.x + k7*u.x*u.y*u.z),
			2.0* du * vec3( k1 + k4*u.y + k6*u.z + k7*u.y*u.z,
				k2 + k5*u.z + k4*u.x + k7*u.z*u.x,
				k3 + k6*u.x + k5*u.y + k7*u.x*u.y ) );
}

float noise( in vec3 x )
{
	vec3 p = floor(x);
	vec3 w = fract(x);

	vec3 u = w*w*w*(w*(w*6.0-15.0)+10.0);

	float n = p.x + 317.0*p.y + 157.0*p.z;

	float a = hash1(n+0.0);
	float b = hash1(n+1.0);
	float c = hash1(n+317.0);
	float d = hash1(n+318.0);
	float e = hash1(n+157.0);
	float f = hash1(n+158.0);
	float g = hash1(n+474.0);
	float h = hash1(n+475.0);

	float k0 =   a;
	float k1 =   b - a;
	float k2 =   c - a;
	float k3 =   e - a;
	float k4 =   a - b - c + d;
	float k5 =   a - c - e + g;
	float k6 =   a - b - e + f;
	float k7 = - a + b + c - d + e - f - g + h;

	return -1.0+2.0*(k0 + k1*u.x + k2*u.y + k3*u.z + k4*u.x*u.y + k5*u.y*u.z + k6*u.z*u.x + k7*u.x*u.y*u.z);
}

vec3 noised( in vec2 x )
{
	vec2 p = floor(x);
	vec2 w = fract(x);

	vec2 u = w*w*w*(w*(w*6.0-15.0)+10.0);
	vec2 du = 30.0*w*w*(w*(w-2.0)+1.0);

	float a = hash1(p+vec2(0,0));
	float b = hash1(p+vec2(1,0));
	float c = hash1(p+vec2(0,1));
	float d = hash1(p+vec2(1,1));

	float k0 = a;
	float k1 = b - a;
	float k2 = c - a;
	float k4 = a - b - c + d;

	return vec3( -1.0+2.0*(k0 + k1*u.x + k2*u.y + k4*u.x*u.y),
			2.0* du * vec2( k1 + k4*u.y,
				k2 + k4*u.x ) );
}

float noise( in vec2 x )
{
	vec2 p = floor(x);
	vec2 w = fract(x);
	vec2 u = w*w*w*(w*(w*6.0-15.0)+10.0);

#if 0
	p *= 0.3183099;
	float kx0 = 50.0*fract( p.x );
	float kx1 = 50.0*fract( p.x+0.3183099 );
	float ky0 = 50.0*fract( p.y );
	float ky1 = 50.0*fract( p.y+0.3183099 );

	float a = fract( kx0*ky0*(kx0+ky0) );
	float b = fract( kx1*ky0*(kx1+ky0) );
	float c = fract( kx0*ky1*(kx0+ky1) );
	float d = fract( kx1*ky1*(kx1+ky1) );
#else
	float a = hash1(p+vec2(0,0));
	float b = hash1(p+vec2(1,0));
	float c = hash1(p+vec2(0,1));
	float d = hash1(p+vec2(1,1));
#endif

	return -1.0+2.0*( a + (b-a)*u.x + (c-a)*u.y + (a - b - c + d)*u.x*u.y );
}

//==========================================================================================
// fbm constructions
//==========================================================================================

const mat3 m3  = mat3( 0.00,  0.80,  0.60,
		-0.80,  0.36, -0.48,
		-0.60, -0.48,  0.64 );
const mat3 m3i = mat3( 0.00, -0.80, -0.60,
		0.80,  0.36, -0.48,
		0.60, -0.48,  0.64 );
const mat2 m2 = mat2(  0.80,  0.60,
		-0.60,  0.80 );
const mat2 m2i = mat2( 0.80, -0.60,
		0.60,  0.80 );

//------------------------------------------------------------------------------------------

float fbm_4( in vec3 x )
{
	float f = 2.0;
	float s = 0.5;
	float a = 0.0;
	float b = 0.5;
	for( int i=0; i<4; i++ )
	{
		float n = noise(x);
		a += b*n;
		b *= s;
		x = f*m3*x;
	}
	return a;
}

vec4 fbmd_8( in vec3 x )
{
	float f = 1.92;
	float s = 0.5;
	float a = 0.0;
	float b = 0.5;
	vec3  d = vec3(0.0);
	mat3  m = mat3(1.0,0.0,0.0,
			0.0,1.0,0.0,
			0.0,0.0,1.0);
	for( int i=0; i<7; i++ )
	{
		vec4 n = noised(x);
		a += b*n.x;          // accumulate values
		d += b*m*n.yzw;      // accumulate derivatives
		b *= s;
		x = f*m3*x;
		m = f*m3i*m;
	}
	return vec4( a, d );
}

float fbm_9( in vec2 x )
{
	float f = 1.9;
	float s = 0.55;
	float a = 0.0;
	float b = 0.5;
	for( int i=0; i<9; i++ )
	{
		float n = noise(x);
		a += b*n;
		b *= s;
		x = f*m2*x;
	}
	return a;
}

vec3 fbmd_9( in vec2 x )
{
	float f = 1.9;
	float s = 0.55;
	float a = 0.0;
	float b = 0.5;
	vec2  d = vec2(0.0);
	mat2  m = mat2(1.0,0.0,0.0,1.0);
	for( int i=0; i<9; i++ )
	{
		vec3 n = noised(x);
		a += b*n.x;          // accumulate values
		d += b*m*n.yz;       // accumulate derivatives
		b *= s;
		x = f*m2*x;
		m = f*m2i*m;
	}
	return vec3( a, d );
}

float fbm_4( in vec2 x )
{
	float f = 1.9;
	float s = 0.55;
	float a = 0.0;
	float b = 0.5;
	for( int i=0; i<4; i++ )
	{
		float n = noise(x);
		a += b*n;
		b *= s;
		x = f*m2*x;
	}
	return a;
}
