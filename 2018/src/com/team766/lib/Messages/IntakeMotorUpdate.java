package com.team766.lib.Messages;

import lib.Message;

public class IntakeMotorUpdate implements Message {

	private double speed;
	
	public IntakeMotorUpdate(double speed) {
		this.speed = speed;
	}
	
	public double getSpeed(){
		return speed;
	}
	
	public String toString(){
		return "Message:\tUpdate Gripper Motor";
	}

}
