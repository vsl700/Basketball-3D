package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.math.Vector2;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class CircleZone extends Zone {

	float radius;
	
	public CircleZone(String id, float radius) {
		super(id);
		this.radius = radius;
	}

	@Override
	public boolean checkZone(Vector2 pos) {
		// TODO Auto-generated method stub
		return false;
	}

}
