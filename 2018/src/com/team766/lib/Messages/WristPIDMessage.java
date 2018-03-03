package com.team766.lib.Messages;

import lib.Message;

public class WristPIDMessage implements Message {
	private int position;
	
	//assume there are three positions for now
	//0 = down, 1 = middle, 2 = back
	public WristPIDMessage(int position) {
		this.position = position;
		System.out.println("Inside wrist message.");
	}
	
	public int getWristPosition(){
		return position;
	}
	
	

}
