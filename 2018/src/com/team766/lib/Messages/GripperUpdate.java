package com.team766.lib.Messages;

import lib.Message;

public class GripperUpdate implements Message{

	private boolean open;
	
	//true = open, false = close
	public GripperUpdate(boolean open) {
		System.out.println("inside gripper message!");
		this.open = open;
	}
	
	//open or close the gripper
	public boolean getOpen(){
		return open;
	}
	
	public String toString(){
		return "Message:\tUpdate Gripper";
	}

}
