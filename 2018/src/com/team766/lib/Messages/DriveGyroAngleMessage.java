package com.team766.lib.Messages;

import lib.Message;

public class DriveGyroAngleMessage implements Message{
	
	private double distance, angle;

	public DriveGyroAngleMessage(double distance, double angle){
		this.distance = distance;
		this.angle = angle;
	}
	
	//degrees
	public double getAngle(){
		return angle;
	}
	
	//feet
	public double getDistance(){
		return distance;
	}
}
