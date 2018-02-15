package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveEncoderMessage;

import lib.Message;

public class DrivePID extends CommandBase{
	
	private boolean done;
	private DriveEncoderMessage message;
	
	public DrivePID(Message m){
		message = (DriveEncoderMessage) m;
		
		Drive.distancePID.setSetpoint(message.getDistance());
		Drive.anglePID.setSetpoint(message.getAngle());
	}

	@Override
	public void update() {
		
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
