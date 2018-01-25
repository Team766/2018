package com.team766.lib.Messages;

import lib.Message;

public class GripperUpdate implements Message{
	
	private boolean intake;
	private boolean grab;
	
	public GripperUpdate(boolean intake, boolean grab) {
		this.intake = intake;
		this.grab = grab;
	}
	
	//intaking block by spinning the wheels
	public boolean getIntake(){
		return intake;
	}
	
	//open or close the gripper
	public boolean getGrab(){
		return grab;
	}
	
	public String toString(){
		return "Message:\tUpdate Gripper";
	}

}
