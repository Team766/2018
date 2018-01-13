package com.team766.lib.Messages;

import lib.Message;

public class DriveTimeMessage implements Message{
	
	private double time;
	
	public DriveTimeMessage (double time){
		this.time = time;
	}

	public double getTime(){
		return time;
	}
	
	public String toString(){
		return "Message: DriveTimeMessage";
	}
}
