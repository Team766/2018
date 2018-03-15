package com.team766.lib.Messages;

import lib.Message;

public class ShoulderSimpleMessage implements Message {
	
	private int direction;
	
	//0 = going up, 1 = going down
	public ShoulderSimpleMessage(int direction) {
		this.direction = direction;
	}
	
	public int getShoulderDirection(){
		return direction;
	}
	

}
