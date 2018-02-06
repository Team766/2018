package com.team766.lib.Messages;

import lib.Message;

public class ArmSimpleMessage implements Message {

	private double shoulderSpeed;
	private double wristSpeed;
	
	public ArmSimpleMessage(double shoulderSpeed, double wristSpeed) {
		this.shoulderSpeed = shoulderSpeed;
		this.wristSpeed = wristSpeed;
	}
	
	public double getShoulderSpeed(){
		return shoulderSpeed;
	}
	
	public double getWristSpeed(){
		return wristSpeed;
	}
	
	public String toString(){
		return "Message:\tUpdate Gripper Motor";
	}

}
