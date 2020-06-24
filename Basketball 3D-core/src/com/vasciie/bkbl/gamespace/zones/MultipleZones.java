package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.math.Vector2;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class MultipleZones extends Zone {

	Zone[] zones;
	

	public MultipleZones(String id, GameMap map, Zone[] zones) {
		super(id, map);
		
		this.zones = zones;
	}

	@Override
	public boolean checkZone(Vector2 pos) {
		for(Zone zone : zones) {
			if(zone.checkZone(pos))
				return true;
		}
		
		return false;
	}

}
