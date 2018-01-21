package com.team766.lib.Messages;

import com.team766.lib.Messages.DriveUpdate.Motor;

import lib.Message;

public class DriveUpdate implements Message{
	
	private double power;
	private Motor motor;
	
	public enum Motor{
		rightDrive,
		leftDrive,
	}
	
	public DriveUpdate(double in, Motor mot){
		power = in;
		motor = mot;
	}
	
	public double getPower(){
		return power;
	}
	
	public Motor getMotor(){
		return motor;
	}
	
	public String toString() {
		return "Message: Drive Update";
	}
}
