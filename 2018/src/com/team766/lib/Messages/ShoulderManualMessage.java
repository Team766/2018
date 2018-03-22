package com.team766.lib.Messages;

import lib.Message;

public class ShoulderManualMessage implements Message {
	
	private int direction;
	
	//0 = going up, 1 = going down
	public ShoulderManualMessage(int direction) {
		this.direction = direction;
	}
	
	public int getShoulderDirection(){
		return direction;
	}
	

}
