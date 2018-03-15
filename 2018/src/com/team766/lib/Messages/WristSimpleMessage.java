package com.team766.lib.Messages;

import lib.Message;

public class WristSimpleMessage implements Message {

	private int direction;
	
	public WristSimpleMessage(int direction) {
		this.direction = direction;
	}
	
	public int getWristDirection(){
		return direction;
	}

}
