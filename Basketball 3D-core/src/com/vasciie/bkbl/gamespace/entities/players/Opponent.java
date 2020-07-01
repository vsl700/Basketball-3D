package com.vasciie.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.objects.Basket;

public class Opponent extends Player {

	@Override
	protected Color getPlayerColor() {
		return Color.RED;
	}

	@Override
	public Basket getTargetBasket() {
		return map.getHomeBasket();
	}

	@Override
	public boolean isInAwayBasketZone() {
		return map.getZones().isInZone("free-throw-team", getPosition());
	}

	@Override
	public boolean isInAwayThreePointZone() {
		return map.getZones().isInZone("three-point-team", getPosition());
	}

	@Override
	public boolean isInAwayZone() {
		
		return getPosition().z > 0;
	}
	
	@Override
	public boolean isInHomeBasketZone() {
		
		return map.getZones().isInZone("free-throw-opp", getPosition());
	}

	@Override
	public boolean isInHomeThreePointZone() {
		
		return map.getZones().isInZone("three-point-opp", getPosition());
	}
	

}
