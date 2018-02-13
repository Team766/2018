package com.team766.lib.Messages;

import lib.Message;

public class DriveEncoderMessage implements Message {

	private double distance;
	
	public DriveEncoderMessage(double distance){
		this.distance = distance;
	}
	
	//feet
	public double getDistance(){
		return distance;
	}
	
	public String toString(){
		return "Message: DriveStraightEncoderMessage";
	}

}
