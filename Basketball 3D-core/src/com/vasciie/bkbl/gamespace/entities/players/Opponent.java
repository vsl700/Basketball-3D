package com.vasciie.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.objects.Basket;

public class Opponent extends Player {

	@Override
	protected Color getPlayerColor() {
		// TODO Auto-generated method stub
		return Color.RED;
	}

	@Override
	public Basket getTargetBasket() {
		// TODO Auto-generated method stub
		return map.getHomeBasket();
	}
	
	

}
