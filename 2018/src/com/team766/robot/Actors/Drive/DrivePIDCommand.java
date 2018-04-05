package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DrivePIDMessage;

import lib.Message;

public class DrivePIDCommand extends CommandBase{
	/*
	 * Drives robot to specified distance and angle using distance and angle PID
	 */
	
	private boolean done;
	private DrivePIDMessage message;
	
	public DrivePIDCommand(Message m){
		message = (DrivePIDMessage) m;
		done = false;
		
		Drive.resetEncoders();
		Drive.setGyroAngle(0.0);
		
		Drive.distancePID.setSetpoint(message.getDistance());
		Drive.anglePID.setSetpoint(message.getAngle());
	}

	@Override
	public void update() {
		Drive.distancePID.calculate(Drive.averageDistance(), true);
		Drive.anglePID.calculate(Drive.getGyroAngle(), false); //added debug need to test monday
		
		Drive.setAutonLeft(Drive.distancePID.getOutput() - Drive.anglePID.getOutput()); 
		Drive.setAutonRight(Drive.distancePID.getOutput() + Drive.anglePID.getOutput());
		
		System.out.println("drive left: " + (Drive.distancePID.getOutput() - Drive.anglePID.getOutput() + "\t\t\t\t\t\t\tright: " + (Drive.distancePID.getOutput() + Drive.anglePID.getOutput())));
		
		if(Drive.distancePID.isDone() && Drive.anglePID.isDone()){
			stop();
			done = true;
		}
	}

	@Override
	public void stop() {
		Drive.setAutonLeft(0.0);
		Drive.setAutonRight(0.0);
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
