package com.team766.lib.Messages;

import lib.Message;

public class DriveEncoderMessage implements Message {

	private double distance;
	//private double angle;
	
	public DriveEncoderMessage(double distance) {
		this.distance = distance;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public String toString(){
		return "Message: DriveEncoderMessage";
	}

}
