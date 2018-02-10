package com.team766.lib.Messages;

import lib.Message;

public class ArmStageMessage implements Message {
	
	private double height;
	
	public ArmStageMessage(double height) {
		this.height = height;
	}
	
	public double getHeight(){
		return height;
	}
	
	public String toString(){
		return "Message:\tArm Stage Message";
	}

}
