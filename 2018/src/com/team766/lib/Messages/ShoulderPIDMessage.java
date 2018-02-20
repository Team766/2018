package com.team766.lib.Messages;

import lib.Message;

public class ShoulderPIDMessage implements Message{
	private boolean vertical;
	
	//true --> vertical; false --> down
	public ShoulderPIDMessage(boolean vertical){
		this.vertical = vertical;
	}
	
	public boolean toVertical(){
		return vertical;
	}
}
