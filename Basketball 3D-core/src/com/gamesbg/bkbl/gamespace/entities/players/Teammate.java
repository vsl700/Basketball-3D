package com.gamesbg.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.gamesbg.bkbl.gamespace.entities.Player;

public class Teammate extends Player {

	@Override
	protected Color getPlayerColor() {
		// TODO Auto-generated method stub
		return Color.BLUE;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(this.equals(map.getMainPlayer()))
			map.getCamera().getBodies().get(0).proceedToTransform(modelInstance.transform.cpy().mul(new Matrix4().setTranslation(0, 0, -10)));
	}

}
