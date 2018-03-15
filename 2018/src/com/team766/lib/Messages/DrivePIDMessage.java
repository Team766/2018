package com.team766.lib.Messages;

import lib.Message;

public class DrivePIDMessage implements Message{
	private double distance, angle;

	//angle: pos --> left; neg --> right
	public DrivePIDMessage(double distance, double angle){
		this.distance = distance;
		this.angle = angle;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getAngle(){
		return angle;
	}
}
