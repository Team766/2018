package com.team766.lib.Messages;

import lib.Message;

public class GripperUpdate implements Message{

	private boolean grab;
	
	//true = close, false = open
	public GripperUpdate(boolean grab) {
		System.out.println("inside gripper message!");
		this.grab = grab;
	}
	
	//open or close the gripper
	public boolean getGrab(){
		return grab;
	}
	
	public String toString(){
		return "Message:\tUpdate Gripper";
	}

}
