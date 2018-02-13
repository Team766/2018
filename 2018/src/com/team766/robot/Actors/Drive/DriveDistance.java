package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.robot.Constants;

import lib.Message;

public class DriveDistance extends CommandBase{
	/*
	 * This command drives the robot straight ramping power down using encoders
	 */
	DriveEncoderMessage message;
	private boolean leftDone, rightDone;
	private double targetDist, targetAngle;
	
	private double linearPower = 0.5;
	private double linearP = 0.2;
	
	public DriveDistance(Message m){
		message = (DriveEncoderMessage) m;
		leftDone = false;
		rightDone = false;
		
		Drive.resetEncoders();
		
		targetDist = message.getDistance();
	}

	public void update() {
		double leftError = targetDist - Drive.leftDistance();
		double leftDirection = leftError > 0 ? 1 : -1;
		if(Math.abs(leftError) > Constants.driveThreshold ){
			Drive.setLeft(leftDirection * linearPower * linearP * leftError);
		}
		else{
			Drive.setLeft(0.0);
			leftDone = true;
		}

		double rightError = targetDist - Drive.rightDistance();
		double rightDirection = rightError > 0 ? 1 : -1;
		if(Math.abs(rightError) > Constants.driveThreshold){
			Drive.setRight(rightDirection * linearPower * linearP * rightError);
		}
		else{
			Drive.setRight(0.0);
			rightDone = true;
		}
		
		//System.out.println("DBG: \t\tError: left = " + leftError + " \t\tright = " + rightError);
	}

	public void stop() {
		Drive.setDrive(0.0);
	}

	public boolean isDone() {
		if(leftDone && rightDone){
			stop();
			return true;
		}
		return false;
	}	

}
