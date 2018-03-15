package com.team766.lib.Messages;

import lib.Message;

public class ShoulderPIDMessage implements Message{
	private int position;
	
	//0,1,2,3 --> Down, Middle, Vertical, Hold
	public ShoulderPIDMessage(int position){
		this.position = position;
		System.out.println("inside shoulder message");
	}
	
	public int getDesiredPos(){
		return position;
	}
}
