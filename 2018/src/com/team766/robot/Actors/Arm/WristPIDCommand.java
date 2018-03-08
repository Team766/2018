package com.team766.robot.Actors.Arm;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import lib.ConstantsFileReader;
import lib.Message;

public class WristPIDCommand extends CommandBase {
	/*
	 * This command moves the wrist between the lowest resting point and back position
	 */
	private boolean done;
	private State currentState;
	private WristPIDMessage message;
	private int negate = 1;
	
	private enum State{
		back,
		middle,		
		intake,
		stop
	}
	
	public WristPIDCommand(Message m) {
		this.message = (WristPIDMessage)m;
		done = false;
		currentState = State.stop;
		
		if(message.getWristPosition() == 1){
			//System.out.println("to middle__________");
			switchState(State.middle);
			Arm.wristPID.setSetpoint(Constants.armWristMiddle);
		}
		else if(message.getWristPosition() == 2){
			switchState(State.intake);
			Arm.wristPID.setSetpoint(Constants.armWristBack);
		}
		else{
			switchState(State.back);
			Arm.wristPID.setSetpoint(Constants.armWristDown);
		}
	}

	@Override
	public void update() {
		Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
		double pidOutput = negate * (Arm.wristPID.getOutput() * Constants.wristBackPIDScale);
		double feedforward = Constants.armWristFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder()));
		System.out.println("Arm wrist encoder value: " + Arm.getAveWristEncoder());
		System.out.println("Wrist PID setpoint: " + Arm.wristPID.getSetpoint());
		System.out.println("Pid outpput: " + pidOutput);
		System.out.println("feed forward: " + feedforward);
		Arm.setWrist(pidOutput + feedforward);
		System.out.println("WristPower: " + Arm.leftWrist.get());
		if(Arm.wristPID.isDone()){
			done = true;
			System.out.println("wrist done");
		}

	}

	@Override
	public void stop() {
		Arm.setWrist(0);
	}

	@Override
	public boolean isDone() {
		stop();
		return done;
	}
	
	
	private void switchState(State state){
		currentState = state;
	}

}
