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
//		this.message = (ArmStageMessage)command;
//		this.targetHeight = message.getHeight();
//		done = false;
//		targetAngle = Arm.getAngleFromHeight(message.getHeight());
//		Arm.setShoulderSetPoint(targetAngle);
//		Arm.intakePID.setSetpoint(Arm.getShoulderSetPoint());
	}
//
//	@Override
	public void update() {
//		Arm.intakePID.calculate(targetAngle, false);
//		System.out.println("Angle: " + Arm.getShoulderAngle());
//		if(!done){
//			Arm.setArmShoulder(Arm.intakePID.getOutput());
//			
//			if(Arm.intakePID.isDone())
//				done = true;
//		}
	}

	@Override
	public void stop() {
//		Arm.setArmShoulder(0.0);
	}

	@Override
	public boolean isDone() {
		return isDone();
	}

}
