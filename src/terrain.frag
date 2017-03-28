//------------------------------------------------------------------------------------------
// terrain
//------------------------------------------------------------------------------------------

vec2 terrainMap( in vec2 p )
{
	const float sca = 0.0010;
	const float amp = 300.0;

	p *= sca;
	float e = fbm_9( p + vec2(1.0,-2.0) );
	float a = 1.0-smoothstep( 0.12, 0.13, abs(e+0.12) ); // flag high-slope areas (-0.25, 0.0)
	e = e + 0.15*smoothstep( -0.08, -0.01, e );
	e *= amp;
	return vec2(e,a);
}

vec4 terrainMapD( in vec2 p )
{
	const float sca = 0.0010;
	const float amp = 300.0;
	p *= sca;
	vec3 e = fbmd_9( p + vec2(1.0,-2.0) );
	vec2 c = smoothstepd( -0.08, -0.01, e.x );
	e.x = e.x + 0.15*c.x;
	e.yz = e.yz + 0.15*c.y*e.yz;
	e.x *= amp;
	e.yz *= amp*sca;
	return vec4( e.x, normalize( vec3(-e.y,1.0,-e.z) ) );
}

vec3 terrainNormal( in vec2 pos )
{
#if 1
	return terrainMapD(pos).yzw;
#else
	vec2 e = vec2(0.03,0.0);
	return normalize( vec3(terrainMap(pos-e.xy).x - terrainMap(pos+e.xy).x,
				2.0*e.x,
				terrainMap(pos-e.yx).x - terrainMap(pos+e.yx).x ) );
#endif
}

float terrainShadow( in vec3 ro, in vec3 rd, in float mint )
{
	float res = 1.0;
	float t = mint;
#ifdef LOWQUALITY
	for( int i=0; i<32; i++ )
	{
		vec3  pos = ro + t*rd;
		vec2  env = terrainMap( pos.xz );
		float hei = pos.y - env.x;
		res = min( res, 32.0*hei/t );
		if( res<0.0001 ) break;
		t += clamp( hei, 1.0+t*0.1, 50.0 );
	}
#else
	for( int i=0; i<128; i++ )
	{
		vec3  pos = ro + t*rd;
		vec2  env = terrainMap( pos.xz );
		float hei = pos.y - env.x;
		res = min( res, 32.0*hei/t );
		if( res<0.0001 ) break;
		t += clamp( hei, 0.5+t*0.05, 25.0 );
	}
#endif
	return clamp( res, 0.0, 1.0 );
}

vec2 raymarchTerrain( in vec3 ro, in vec3 rd, float tmin, float tmax )
{
	//float tt = (150.0-ro.y)/rd.y; if( tt>0.0 ) tmax = min( tmax, tt );

	float dis, th;
	float t2 = -1.0;
	float t = tmin;
	float ot = t;
	float odis = 0.0;
	float odis2 = 0.0;
	for( int i=0; i<400; i++ )
	{
		th = 0.001*t;

		vec3  pos = ro + t*rd;
		vec2  env = terrainMap( pos.xz );
		float hei = env.x;

		// tree envelope
		float dis2 = pos.y - (hei+kMaxTreeHeight*1.1);
		if( dis2<th )
		{
			if( t2<0.0 )
			{
				t2 = ot + (th-odis2)*(t-ot)/(dis2-odis2); // linear interpolation for better accuracy
			}
		}
		odis2 = dis2;

		// terrain
		dis = pos.y - hei;
		if( dis<th ) break;

		ot = t;
		odis = dis;
		t += dis*0.8*(1.0-0.75*env.y); // slow down in step areas
		if( t>tmax ) break;
	}

	if( t>tmax ) t = -1.0;
	else t = ot + (th-odis)*(t-ot)/(dis-odis); // linear interpolation for better accuracy
	return vec2(t,t2);
}

vec4 renderTerrain( in vec3 ro, in vec3 rd, in vec2 tmima, out float teShadow, out vec2 teDistance, inout float resT )
{
	vec4 res = vec4(0.0);
	teShadow = 0.0;
	teDistance = vec2(0.0);

	vec2 t = raymarchTerrain( ro, rd, tmima.x, tmima.y );
	if( t.x>0.0 )
	{
		vec3 pos = ro + t.x*rd;
		vec3 nor = terrainNormal( pos.xz );

		// bump map
		nor = normalize( nor + 0.8*(1.0-abs(nor.y))*0.8*fbmd_8( pos*0.3*vec3(1.0,0.2,1.0) ).yzw );

		vec3 col = vec3(0.18,0.11,0.10)*.75;
		col = 1.0*mix( col, vec3(0.1,0.1,0.0)*0.3, smoothstep(0.7,0.9,nor.y) );

		//col *= 1.0 + 2.0*fbm( pos*0.2*vec3(1.0,4.0,1.0) );

		float sha = 0.0;
		float dif =  clamp( dot( nor, kSunDir), 0.0, 1.0 );
		if( dif>0.0001 )
		{
			sha = terrainShadow( pos+nor*0.01, kSunDir, 0.01 );
			//if( sha>0.0001 ) sha *= cloudsShadow( pos+nor*0.01, kSunDir, 0.01, 1000.0 );
			dif *= sha;
		}
		vec3  ref = reflect(rd,nor);
		float bac = clamp( dot(normalize(vec3(-kSunDir.x,0.0,-kSunDir.z)),nor), 0.0, 1.0 )*clamp( (pos.y+100.0)/100.0, 0.0,1.0);
		float dom = clamp( 0.5 + 0.5*nor.y, 0.0, 1.0 );
		vec3  lin  = 1.0*0.2*mix(0.1*vec3(0.1,0.2,0.0),vec3(0.7,0.9,1.0),dom);//pow(vec3(occ),vec3(1.5,0.7,0.5));
		lin += 1.0*5.0*vec3(1.0,0.9,0.8)*dif;
		lin += 1.0*0.35*vec3(1.0)*bac;

		col *= lin;

		col = fog(col,t.x);

		teShadow = sha;
		teDistance = t;
		res = vec4( col, 1.0 );
		resT = t.x;
	}

	return res;
}
