package com.team766.lib.Messages;

import lib.Message;

public class DriveEncoderMessage implements Message {

	private double distance, angle;
	
	public DriveEncoderMessage(double distance, double angle){
		this.distance = distance;
		this.angle = angle;
	}
	
	//feet
	public double getDistance(){
		return distance;
	}
	
	//degrees
	public double getAngle(){
		return angle;
	}
	
	public String toString(){
		return "Message: DriveEncoderMessage";
	}

}
