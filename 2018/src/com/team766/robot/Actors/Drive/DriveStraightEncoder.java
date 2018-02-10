package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.robot.Constants;

import lib.Message;

public class DriveStraightEncoder extends CommandBase{

	private DriveEncoderMessage command;
	private double leftTargetDistance;
	private double rightTargetDistance;
	private boolean done;
	
	private double power = 0.5;

	private double error = 0;
	private double kp = 3; //value not sure
	
	public DriveStraightEncoder(Message message){
		command = (DriveEncoderMessage)message;
		leftTargetDistance = Drive.leftDistance() + command.getDistance();
		rightTargetDistance = Drive.rightDistance() + command.getDistance();
		done = false;
	}
	
	public void update() {
		System.out.println("left distance: " + Drive.leftDistance());
		System.out.println("right distance: " + Drive.rightDistance());
		
		Drive.setDrive(power);
		/*
		error = Drive.leftDistance() - Drive.rightDistance();
		rightPower += error / kp;
		*/
	}

	public void stop() {
		Drive.setDrive(0.0);
	}

	public boolean isDone() {
		if(Math.abs(leftTargetDistance - Drive.leftDistance()) < Constants.driveThreshold 
				&& Math.abs(rightTargetDistance - Drive.rightDistance()) < Constants.driveThreshold){
			Drive.setDrive(0.0);
			return true;
		}
		return false;
	}
}
