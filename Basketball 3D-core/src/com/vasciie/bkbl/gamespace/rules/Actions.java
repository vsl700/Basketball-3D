package com.vasciie.bkbl.gamespace.rules;

import com.vasciie.bkbl.gamespace.GameMap;

public class Actions{
	Action firstAction, currentAction;
	GameMap map;
	
	public Actions(GameMap map) {
		this.map = map;
	}
	
	public void addAction(Action action) {
		if(currentAction == null) {
			firstAction = currentAction = action;
			return;
		}
		
		Action temp = currentAction;
		while(temp.next != null) {
			temp = temp.next;
		}
		
		temp.next = action;
	}
	
	public void copyActions(Actions actions) {
		while(actions.getCurrentAction() != null) {
			addAction(actions.getCurrentAction());
			
			actions.nextAction();
		}
	}
	
	public Action getCurrentAction() {
		return currentAction;
	}
	
	public void nextAction() {
		currentAction = currentAction.next;
	}
	
	public void firstAction() {
		currentAction = firstAction;
	}
	
	public boolean isEmpty() {
		return firstAction == null;
	}
	
	public boolean isLastAction() {
		return currentAction.next == null;
	}
	
	/**
	 * 
	 * @return true if all the actions are completed
	 */
	public boolean act() {
		if(isEmpty())
			return true;
		
		if(((currentAction.isGameDependent() && map.isGameRunning()) || !currentAction.isGameDependent()) && currentAction.act()) {
			if(isLastAction()) {
				firstAction();
				return true;
			}
			
			nextAction();
		}
		
		return false;
	}
	
	public static abstract class Action implements Cloneable{
		public Action next;
		
		/**
		 * 
		 * @return true if the action is completed
		 */
		public abstract boolean act();
		
		/**
		 * 
		 * @return true if the action is dependent on the game and the game should start (it doesn't clear the broken rule or rule breaker)
		 */
		public abstract boolean isGameDependent();
		
		public Action copyAction() {
			try {
				Action temp = (Action) clone();
				temp.next = null;
				
				return temp;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
}
