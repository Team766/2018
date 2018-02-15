package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.robot.Constants;

import lib.Message;

public class DriveDistance extends CommandBase{
	/*
	 * This command drives the robot ramping power down using encoders
	 */
	DriveEncoderMessage message;
	private boolean leftDone, rightDone;
	private double targetDist, targetAngle;
	
	private double linearPower = 0.5;
	private double angularPower = 0.2;
	private double linearP = 0.2;
	private double angularP = 0.1;
	
	public DriveDistance(Message m){
		message = (DriveEncoderMessage) m;
		leftDone = false;
		rightDone = false;
		
		Drive.resetEncoders();
		Drive.setGyroAngle(0.0);
		
		targetDist = message.getDistance();
		targetAngle = message.getAngle();
	}

	public void update() {
		//assumes gyro angle increases to the right
		double angleError = targetAngle - Drive.getCalculatedAngle();
		double angleDirection = angleError > 0 ? 1 : -1;
		double angleCorrection = angleDirection * angleError * angularPower * angularP;
		
		double leftError = targetDist - Drive.leftDistance();
		double leftDirection = leftError > 0 ? 1 : -1;
		double leftPower = leftDirection * linearPower * linearP * leftError;
		
		if(Math.abs(leftError) > Constants.driveThreshold ){
			Drive.setLeft(leftPower + angleCorrection);
		}
		else{
			Drive.setLeft(0.0);
			leftDone = true;
		}

		double rightError = targetDist - Drive.rightDistance();
		double rightDirection = rightError > 0 ? 1 : -1;
		double rightPower = rightDirection * linearPower * linearP * rightError;
		
		if(Math.abs(rightError) > Constants.driveThreshold){
			Drive.setRight(rightPower - angleCorrection);
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
