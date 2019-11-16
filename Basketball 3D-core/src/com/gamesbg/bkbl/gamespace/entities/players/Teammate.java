package com.gamesbg.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
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
		
		//if(this.equals(map.getMainPlayer()))
			//map.getCamera().getBodies().get(0).setWorldTransform(modelInstance.transform.cpy().mul(new Matrix4().setToTranslation(0, 5, 3)));
			//map.getCamera().setWorldTransform(new Matrix4(modelInstance.transform).mul(getCamNode().globalTransform).mul(new Matrix4().setToTranslation(0, getHeight(), -10)));
	}

}
