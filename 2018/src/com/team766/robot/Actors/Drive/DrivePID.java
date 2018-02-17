package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;

import lib.Message;

public class DrivePID extends CommandBase{
	/*
	 * Drives robot to specified distance and angle using distance and angle PID
	 */
	
	private boolean done;
	private DriveEncoderMessage message;
	
	public DrivePID(Message m){
		message = (DriveEncoderMessage) m;
		
		Drive.distancePID.setSetpoint(message.getDistance());
		Drive.anglePID.setSetpoint(message.getAngle());
	}

	@Override
	public void update() {
		Drive.distancePID.calculate(Drive.averageDistance(), false);
		Drive.anglePID.calculate(Drive.getGyroAngle(),false);
		
		Drive.setLeft(Drive.distancePID.getOutput() - Drive.anglePID.getOutput()); //could change + and - not tested
		Drive.setRight(Drive.distancePID.getOutput() + Drive.anglePID.getOutput());
		
		if(Drive.distancePID.isDone() && Drive.anglePID.isDone()){
			done = true;
		}
	}

	@Override
	public void stop() {
		Drive.setDrive(0.0);
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
