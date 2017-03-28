//------------------------------------------------------------------------------------------
// clouds
//------------------------------------------------------------------------------------------

vec4 cloudsMap( in vec3 pos )
{
	vec4 n = fbmd_8(pos*0.003*vec3(0.6,1.0,0.6)-vec3(0.1,1.9,2.8));
	vec2 h  =  smoothstepd( -60.0, 10.0, pos.y ) -  smoothstepd( 10.0, 500.0, pos.y );
	h.x = 2.0*n.x + h.x - 1.3;
	return vec4( h.x, 2.0*n.yzw*vec3(0.6,1.0,0.6)*0.003 + vec3(0.0,h.y,0.0)  );
}

float cloudsShadow( in vec3 ro, in vec3 rd, float tmin, float tmax )
{
	float sum = 0.0;

	// bounding volume!!
	float tl = ( -10.0-ro.y)/rd.y;
	float th = ( 300.0-ro.y)/rd.y;
	if( tl>0.0 ) tmin = max( tmin, tl );
	if( th>0.0 ) tmax = min( tmax, th );

	float t = tmin;
	for(int i=0; i<64; i++)
	{
		vec3  pos = ro + t*rd;
		vec4  denGra = cloudsMap( pos );
		float den = denGra.x;
		float dt = max(0.2,0.02*t);
		if( den>0.001 )
		{
			float alp = clamp(den*0.3*min(dt,tmax-t-dt),0.0,1.0);
			sum = sum + alp*(1.0-sum);
		}
		else
		{
			dt *= 1.0 + 4.0*abs(den);
		}
		t += dt;
		if( sum>0.995 || t>tmax ) break;
	}

	return clamp( 1.0-sum, 0.0, 1.0 );
}

vec4 renderClouds( in vec3 ro, in vec3 rd, float tmin, float tmax, inout float resT )
{
	vec4 sum = vec4(0.0);

	// bounding volume!!
	float tl = ( -10.0-ro.y)/rd.y;
	float th = ( 300.0-ro.y)/rd.y;
	if( tl>0.0 )   tmin = max( tmin, tl ); else return sum;
	/*if( th>0.0 )*/ tmax = min( tmax, th );


	float t = tmin;
	float lastT = t;
	float thickness = 0.0;
#ifdef LOWQUALITY
	for(int i=0; i<128; i++)
#else
		for(int i=0; i<300; i++)
#endif
		{
			vec3  pos = ro + t*rd;
			vec4  denGra = cloudsMap( pos );
			float den = denGra.x;
#ifdef LOWQUALITY
			float dt = max(0.1,0.011*t);
#else
			float dt = max(0.05,0.005*t);
#endif
			if( den>0.001 )
			{
#ifdef LOWQUALITY
				float sha = 1.0;
#else
				float sha = clamp( 1.0 - max(0.0,cloudsMap( pos + kSunDir*5.0 ).x), 0.0, 1.0 );
				//sha *= clamp( pos.y - terrainMap( (pos + kSunDir*5.0).xz ).x, 0.0, 1.0 );
#endif
				vec3 nor = -normalize(denGra.yzw);
				float dif = clamp( dot(nor,kSunDir), 0.0, 1.0 )*sha;
				float fre = clamp( 1.0+dot(nor,rd), 0.0, 1.0 )*sha;
				// lighting
				vec3 lin  = vec3(0.70,0.80,1.00)*0.9*(0.6+0.4*nor.y);
				lin += vec3(0.20,0.25,0.20)*0.7*(0.5-0.5*nor.y);
				lin += vec3(1.00,0.70,0.40)*4.5*dif*(1.0-den);
				lin += vec3(0.80,0.70,0.50)*1.3*pow(fre,32.0)*(1.0-den);
				// color
				vec3 col = vec3(0.8,0.77,0.72)*clamp(1.0-4.0*den,0.0,1.0);

				col *= lin;

				col = fog( col, t );

				// front to back blending
				float alp = clamp(den*0.25*min(dt,tmax-t-dt),0.0,1.0);
				col.rgb *= alp;
				sum = sum + vec4(col,alp)*(1.0-sum.a);

				thickness += dt*den;
				lastT = t;
			}
			else
			{
#ifdef LOWQUALITY
				dt *= 1.0 + 4.0*abs(den);
#else
				dt *= 0.8 + 2.0*abs(den);
#endif
			}
			t += dt;
			if( sum.a>0.995 || t>tmax ) break;
		}

	resT = mix( resT, lastT, sum.w );

	if( thickness>0.0)
		sum.xyz += vec3(1.00,0.60,0.40)*0.2*pow(clamp(dot(kSunDir,rd),0.0,1.0),32.0)*exp(-0.3*thickness)*clamp(thickness*4.0,0.0,1.0);

	return clamp( sum, 0.0, 1.0 );
}
