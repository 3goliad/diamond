//------------------------------------------------------------------------------------------
// trees
//------------------------------------------------------------------------------------------

float treesMap( in vec3 p, in float rt, out float oHei, out float oMat, out float oDis )
{
	oHei = 1.0;
	oDis = 0.1;
	oMat = 0.0;

	float base = terrainMap(p.xz).x;

	float d = 10.0;
	vec2 n = floor( p.xz );
	vec2 f = fract( p.xz );
	for( int j=-1; j<=1; j++ )
		for( int i=-1; i<=1; i++ )
		{
			vec2  g = vec2( float(i), float(j) );
			vec2  o = hash2( n + g );
			vec2  v = hash2( n +g + vec2(13.1,71.7) );
			vec2  r = g - f + o;

			float height = kMaxTreeHeight * (0.4+0.8*v.x);
			float width = 0.9*(0.5 + 0.2*v.x + 0.3*v.y);
			vec3  q = vec3(r.x,p.y-base-height*0.5,r.y);
			float k = sdEllipsoidY( q, vec2(width,0.5*height) );

			if( k<d )
			{
				d = k;
				//oMat = hash1(o); //fract(o.x*7.0 + o.y*15.0);
				oMat = o.x*7.0 + o.y*15.0;
				oHei = (p.y - base)/height;
				oHei *= 0.5 + 0.5*length(q) / width;
			}
		}
	oMat = fract(oMat);

	// distort ellipsoids to make them look like trees (works only in the distance really)
#ifdef LOWQUALITY
	if( rt<350.0 )
#else
		if( rt<500.0 )
#endif
		{
			float s = fbm_4( p*3.0 );
			s = s*s;
			oDis = s;
#ifdef LOWQUALITY
			float att = 1.0-smoothstep(150.0,350.0,rt);
#else
			float att = 1.0-smoothstep(200.0,500.0,rt);
#endif
			d += 2.0*s*att*att;
		}

	return d;
}

float treesShadow( in vec3 ro, in vec3 rd )
{
	float res = 1.0;
	float t = 0.02;
#ifdef LOWQUALITY
	for( int i=0; i<50; i++ )
	{
		float kk1, kk2, kk3;
		float h = treesMap( ro + rd*t, t, kk1, kk2, kk3 );
		res = min( res, 32.0*h/t );
		t += h;
		if( res<0.001 || t>20.0 ) break;
	}
#else
	for( int i=0; i<150; i++ )
	{
		float kk1, kk2, kk3;
		float h = treesMap( ro + rd*t, t, kk1, kk2, kk3 );
		res = min( res, 32.0*h/t );
		t += h;
		if( res<0.001 || t>120.0 ) break;
	}
#endif
	return clamp( res, 0.0, 1.0 );
}

vec3 treesNormal( in vec3 pos, in float t )
{
	const float eps = 0.005;
	vec2 e = vec2(1.0,-1.0)*0.5773*eps;
	float kk1, kk2, kk3;
	return normalize( e.xyy*treesMap( pos + e.xyy, t, kk1, kk2, kk3 ) +
			e.yyx*treesMap( pos + e.yyx, t, kk1, kk2, kk3 ) +
			e.yxy*treesMap( pos + e.yxy, t, kk1, kk2, kk3 ) +
			e.xxx*treesMap( pos + e.xxx, t, kk1, kk2, kk3 ) );
}

vec3 treesShade( in vec3 pos, in vec3 tnor, in vec3 enor, in float hei, in float mid, in float dis, in float rt, in vec3 rd, float terrainShadow )
{
	vec3 nor = normalize( tnor + 2.5*enor );

	// --- lighting ---
	float sha = terrainShadow;
	vec3  ref = reflect(rd,nor);
	float occ = clamp(hei,0.0,1.0) * pow(1.0-2.0*dis,3.0);
	float dif = clamp( 0.1 + 0.9*dot( nor, kSunDir), 0.0, 1.0 );
	if( dif>0.0001 && terrainShadow>0.001 )
	{
		//sha *= clamp( 10.0*dot(tnor,kSunDir), 0.0, 1.0 ) * pow(clamp(1.0-13.0*dis,0.0,1.0),4.0);//treesShadow( pos+nor*0.1, kSunDir ); // only cast in non-terrain-occluded areas
		sha *= treesShadow( pos+nor*0.1, kSunDir ); // only cast in non-terrain-occluded areas
	}
	float dom = clamp( 0.5 + 0.5*nor.y, 0.0, 1.0 );
	float fre = clamp(1.0+dot(nor,rd),0.0,1.0);
	float spe = pow( clamp(dot(ref,kSunDir),0.0, 1.0), 9.0 )*dif*sha*(0.2+0.8*pow(fre,5.0))*occ;

	// --- lights ---
	vec3 lin  = 1.0*0.5*mix(0.1*vec3(0.1,0.2,0.0),vec3(0.6,1.0,1.0),dom*occ);
#ifdef SOFTTREES
	lin += 1.0*15.0*vec3(1.0,0.9,0.8)*dif*occ*sha;
#else
	lin += 1.0*10.0*vec3(1.0,0.9,0.8)*dif*occ*sha;
#endif
	lin += 1.0*0.5*vec3(0.9,1.0,0.8)*pow(fre,3.0)*occ;
	lin += 1.0*0.05*vec3(0.15,0.4,0.1)*occ;

	// --- material ---
	float brownAreas = fbm_4( pos.zx*0.03 );
	vec3 col = vec3(0.08,0.09,0.02);
	col = mix( col, vec3(0.09,0.07,0.02), smoothstep(0.2,1.0,mid) );
	col = mix( col, vec3(0.06,0.05,0.01)*1.1, 1.0-smoothstep(0.9,0.91,enor.y) );
	col = mix( col, vec3(0.25,0.16,0.01)*0.15, 0.7*smoothstep(0.1,0.3,brownAreas)*smoothstep(0.5,0.8,enor.y) );
	col *= 1.6;

	// --- brdf * material ---
	col *= lin;
	col += spe*1.2*vec3(1.0,1.1,2.5);

	// --- fog ---
	col = fog( col, rt );

	return col;
}

vec4 renderTrees( in vec3 ro, in vec3 rd, float tmin, float tmax, float terrainShadow, inout float resT )
{
	//if( tmin>300.0 ) return vec4(0.0);
	float t = tmin;
	float hei, mid, displa;

	for(int i=0; i<64; i++)
	{
		vec3  pos = ro + t*rd;
		float dis = treesMap( pos, t, hei, mid, displa);
		if( dis<(0.0001*t) ) break;
		t += dis;
		if( t>tmax ) return vec4(0.0);
	}

	vec3 pos = ro + t*rd;

	vec3 enor = terrainNormal( pos.xz );
	vec3 tnor = treesNormal( pos, t );

	vec3 col = treesShade( pos, tnor, enor, hei, mid, displa, t, rd, terrainShadow );
	resT = t;

	return vec4(col,1.0);
}
