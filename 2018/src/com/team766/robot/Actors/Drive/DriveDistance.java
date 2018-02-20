package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.robot.Constants;

import lib.Message;

public class DriveDistance extends CommandBase{
	/*
	 * This command drives the robot ramping power down using encoders and gyro
	 */
	DriveEncoderMessage message;
	private boolean done;
	private double targetDist, targetAngle;
	
	private double linearP = 0.2;
	private double angularP = 0.1;
	
	public DriveDistance(Message m){
		message = (DriveEncoderMessage) m;
		done = false;
		
		Drive.resetEncoders();
		Drive.setGyroAngle(0.0);
		
		targetDist = message.getDistance();
		targetAngle = message.getAngle();
	}

	public void update() {
		boolean angleDone = false;
		boolean linearDone = false;
		
		double angleError = targetAngle - Drive.getCalculatedAngle();
		double yaw_v = angleError * angularP;
		
		double linearError = targetDist - Drive.averageDistance();
		double surge_v = linearError * linearP;
		
		if(Math.abs(angleError) < Constants.driveAngleThreshold){
			yaw_v = 0.0;
			angleDone = true;
		}
		
		if(Math.abs(linearError) < Constants.driveEncoderThreshold){
			surge_v = 0.0;
			linearDone = true;
		}
		
		Drive.twist(surge_v, yaw_v);

		done = done || (angleDone && linearDone);
	}

	public void stop() {
		Drive.setDrive(0.0);
	}

	public boolean isDone() {
		if(done){
			stop();
			return true;
		}
		return false;
	}	
}
