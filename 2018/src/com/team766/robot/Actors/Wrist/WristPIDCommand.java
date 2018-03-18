package com.team766.robot.Actors.Wrist;

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
	ConstantsFileReader constants_file;
	
	private enum State{
		Back,
		Middle,		
		Intake,
		Stop,
		Hold
	}
	
	public WristPIDCommand(Message m) {
		this.message = (WristPIDMessage)m;
		done = false;
		currentState = State.Stop;
		constants_file = ConstantsFileReader.getInstance();
		
		if(message.getWristPosition() == 1){
			//System.out.println("to middle__________");
			switchState(State.Middle);
			Wrist.wristPID.setSetpoint(constants_file.get("armWristMiddle"));
		}
		else if(message.getWristPosition() == 2){
			switchState(State.Intake);
			Wrist.wristPID.setSetpoint(constants_file.get("armWristIntake"));
		}
		else if(message.getWristPosition() == 0){
			switchState(State.Back);
			Wrist.wristPID.setSetpoint(constants_file.get("armWristBack"));
		}
		else{
			switchState(State.Hold);
			Wrist.wristPID.setSetpoint(Wrist.getAveWristEncoder());
		}
	}

	@Override
	public void update() {	
		System.out.println("Wrist PID setpoint: " + Wrist.wristPID.getSetpoint());
		Wrist.wristPID.calculate(Wrist.getLeftWristEncoder(), false);
		Wrist.setWrist(negate * (Wrist.wristPID.getOutput() * constants_file.get("wristBackPIDScale") + constants_file.get("armWristFeedForward") * Math.cos(Wrist.getWristAngleRad(Wrist.getAveWristEncoder()))));
		System.out.println("__________________WristPower: " + Wrist.wristPID.getOutput() * constants_file.get("wristBackPIDScale") + constants_file.get("armWristFeedForward") * Math.cos(Wrist.getWristAngleRad(Wrist.getAveWristEncoder())));
		
		if(Wrist.wristPID.isDone()){
			done = true;
			System.out.println("done moving");
		}
	}

	@Override
	public void stop() {
		Wrist.setWrist(0);
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	
	private void switchState(State state){
		currentState = state;
	}
	
	

}
