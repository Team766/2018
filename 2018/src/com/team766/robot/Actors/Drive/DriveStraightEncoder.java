package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;

import lib.Message;

public class DriveStraightEncoder extends CommandBase{

	private DriveEncoderMessage command;
	private double leftTargetDistance;
	private double rightTargetDistance;
	private boolean done;
	
	private double leftPower = 1.0;
	private double rightPower = 1.0;
	private double error = 0;
	private double kp = 3; //value not sure
	
	public DriveStraightEncoder(Message message){
		command = (DriveEncoderMessage)message;
		leftTargetDistance = Drive.leftDistance() + command.getDistance();
		rightTargetDistance = Drive.rightDistance() + command.getDistance();
		done = false;
	}
	
	public void update() {
		if(!done){ 
			System.out.println("left distance: " + Drive.leftDistance());
			System.out.println("right distance: " + Drive.rightDistance());
			System.out.println("Error " + error);
			
			Drive.setLeft(leftPower);
			Drive.setRight(-rightPower);
			
			error = Drive.leftDistance() - Drive.rightDistance();
			rightPower += error / kp;
			
			
			if((Drive.leftDistance() == leftTargetDistance) && (Drive.rightDistance() == rightTargetDistance)){
				done = true;
			}
		}
		else
			stop();
	}

	public void stop() {
		Drive.setDrive(0.0);
	}

	public boolean isDone() {
		return done;
	}

	
}
