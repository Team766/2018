package com.team766.lib.Messages;

import lib.Message;

public class DriveTimeMessage implements Message{
	
	private double time, power;
	private boolean turn;
	
	public DriveTimeMessage (double time, double power, boolean turn){
		this.time = time;
		this.turn = turn;
		this.power = power;
	}

	public double getTime(){
		return time;
	}
	
	public boolean getTurn(){
		return turn;
	}
	
	public double getPower(){
		return power;
	}
	
	public String toString(){
		return "Message: DriveTimeMessage";
	}
}
