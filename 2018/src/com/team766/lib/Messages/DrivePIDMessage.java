package com.team766.lib.Messages;

import lib.Message;

public class DrivePIDMessage implements Message{
	private double distance, angle;
	private boolean resetGyro;

	//angle: pos --> left; neg --> right
	//resetGyro = dont reset gyro
	public DrivePIDMessage(double distance, double angle, boolean resetGyro){
		this.distance = distance;
		this.angle = angle;
		this.resetGyro = resetGyro;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getAngle(){
		return angle;
	}
	
	public boolean resetGyro(){
		return resetGyro;
	}
}
