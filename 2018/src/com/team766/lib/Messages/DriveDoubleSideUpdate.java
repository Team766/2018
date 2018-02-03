package com.team766.lib.Messages;

import lib.Message;

public class DriveDoubleSideUpdate implements Message {

	private double leftPower;
	private double rightPower;
	
	public DriveDoubleSideUpdate(double leftPower, double rightPower) {
		this.leftPower = leftPower;
		this.rightPower = rightPower;
	}
	
	public double getLeftPower(){
		return leftPower;
	}
	
	public double getRightPower(){
		return rightPower;
	}
	
	public String toString(){
		return "Message: Drive Double Side Update";
	}

}
