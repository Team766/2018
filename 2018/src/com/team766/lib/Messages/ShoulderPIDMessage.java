package com.team766.lib.Messages;

import lib.Message;

public class ShoulderPIDMessage implements Message{
	private int position;
	
	//0,1,2 --> Down, Middle, Vertical
	public ShoulderPIDMessage(int position){
		this.position = position;
	}
	
	public int getDesiredPos(){
		return position;
	}
}
