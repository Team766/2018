package com.team766.robot.Actors.Shoulder;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.ArmStageMessage;
import com.team766.lib.Messages.DriveTimeMessage;

import lib.Message;

public class ArmStageCommand extends CommandBase{

	private ArmStageMessage message;
	private boolean done;
	private double targetHeight;
	private double targetAngle;

	public ArmStageCommand(Message command) {
		done = false;
		Shoulder.setShoulderEncoders(0);
		
		this.message = (ArmStageMessage)command;
		this.targetHeight = message.getHeight();
		
		targetAngle = Shoulder.getAngleFromHeight(targetHeight);
		Shoulder.setShoulderSetPoint(targetAngle);
		Shoulder.shoulderUpPID.setSetpoint(Shoulder.getShoulderSetPoint());
	}

	@Override
	public void update() {
		Shoulder.shoulderUpPID.calculate(targetAngle, false);
		System.out.println("this Angle: " + Shoulder.getShoulderAngle());
		System.out.println(Shoulder.shoulderUpPID.isDone());
		if(!done){
			System.out.println("pid not done output: " + Shoulder.shoulderUpPID.getOutput());
			Shoulder.setShoulder(Shoulder.shoulderUpPID.getOutput());
			if(Shoulder.shoulderUpPID.isDone()){
				done = true;
			}
		}
	}

	@Override
	public void stop() {
		Shoulder.setShoulder(0.0);
	}

	@Override
	public boolean isDone() {
		stop();
		return done;
	}

}
