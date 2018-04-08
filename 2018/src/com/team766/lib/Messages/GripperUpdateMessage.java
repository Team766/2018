package com.team766.lib.Messages;

import lib.Message;

public class GripperUpdateMessage implements Message{

	private boolean open;

	//true = open, false = close
	public GripperUpdateMessage(boolean open) {
//		System.out.println("inside gripper message!");
		this.open = open;
		System.out.println("gripper open: " + open);
//		for(StackTraceElement ste: Thread.currentThread().getStackTrace()){
//			System.out.println(ste);
//		}
	}
	
	//open or close the gripper
	public boolean getOpen(){
		return open;
	}
	
	public String toString(){
		return "Message:\tGripper Update Message";
	}

}
