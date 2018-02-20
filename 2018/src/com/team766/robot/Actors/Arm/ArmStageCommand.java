package com.team766.robot.Actors.Arm;

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
		Arm.setShoulderEncoders(0);
		
		this.message = (ArmStageMessage)command;
		this.targetHeight = message.getHeight();
		
		targetAngle = Arm.getAngleFromHeight(targetHeight);
		Arm.setShoulderSetPoint(targetAngle);
		Arm.shoulderUpPID.setSetpoint(Arm.getShoulderSetPoint());
	}

	@Override
	public void update() {
		Arm.shoulderUpPID.calculate(targetAngle, false);
		System.out.println("this Angle: " + Arm.getShoulderAngle());
		System.out.println(Arm.shoulderUpPID.isDone());
		if(!done){
			System.out.println("pid not done output: " + Arm.shoulderUpPID.getOutput());
			Arm.setShoulder(Arm.shoulderUpPID.getOutput());
			if(Arm.shoulderUpPID.isDone()){
				done = true;
			}
		}
	}

	@Override
	public void stop() {
		Arm.setShoulder(0.0);
	}

	@Override
	public boolean isDone() {
		stop();
		return done;
	}

}
