package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.math.Vector2;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class PolygonZone extends Zone {

	Vector2[] positions;
	
	public PolygonZone(String id, GameMap map, Vector2[] positions) {
		super(id, map);
		this.positions = positions;
	}
	
	private final Vector2 temp = new Vector2();
	@Override
	public boolean checkZone(Vector2 pos) {
		return checkZone(pos, temp);
	}
	
	@Override
	public boolean checkZone(Vector2 pos, Vector2 dimensions) {
		Vector2 comb1 = pos.cpy().add(dimensions.x, 0);
		Vector2 comb2 = pos.cpy().sub(dimensions.x, 0);
		Vector2 comb3 = pos.cpy().add(0, dimensions.y);
		Vector2 comb4 = pos.cpy().sub(0, dimensions.y);/*
		Vector2 comb5 = pos.cpy().add(dimensions.x, -dimensions.y);
		Vector2 comb6 = pos.cpy().sub(dimensions.x, -dimensions.y);
		Vector2 comb7 = pos.cpy().add(-dimensions.x, dimensions.y);
		Vector2 comb8 = pos.cpy().sub(-dimensions.x, dimensions.y);
		Vector2 comb9 = pos.cpy().add(dimensions.x, dimensions.y);
		Vector2 comb10 = pos.cpy().sub(dimensions.x, dimensions.y);*/
		
		for(int i = 0; i < positions.length; i++) {
			int indexStart, indexEnd;
			if(i == 0) {
				indexStart = positions.length - 1;
				indexEnd = i + 1;
			}else if(i == positions.length - 1) {
				indexStart = i - 1;
				indexEnd = 0;
			}else {
				indexStart = i - 1;
				indexEnd = i + 1;
			}
			
			if(!checkPoint(comb1, positions[i], positions[indexStart], positions[indexEnd]) || 
					!checkPoint(comb2, positions[i], positions[indexStart], positions[indexEnd]) || 
					!checkPoint(comb3, positions[i], positions[indexStart], positions[indexEnd]) || 
					!checkPoint(comb4, positions[i], positions[indexStart], positions[indexEnd]))
				return false;
		}
		
		return true;
	}
	
	private boolean checkPoint(Vector2 entityPos, Vector2 mainPos, Vector2 posCheck1, Vector2 posCheck2) {
		Vector2 mainToCheck1 = posCheck1.cpy().sub(mainPos).nor();
		Vector2 mainToCheck2 = posCheck2.cpy().sub(mainPos).nor();
		Vector2 mainToEntity = entityPos.cpy().sub(mainPos).nor();
		
		float tempDst = mainToCheck1.dst2(mainToCheck2);
		
		return mainToCheck1.dst2(mainToEntity) <= tempDst && mainToCheck2.dst2(mainToEntity) <= tempDst;
	}
	
	@Override
	public Vector2[] getPositions() {
		return positions;
	}

}
