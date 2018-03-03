package com.team766.robot.Actors.Arm;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import lib.Message;



public class WristPIDCommand extends CommandBase {
	/*
	 * This command moves the wrist between the lowest resting point and back position
	 */
	private boolean done;
	private State currentState;
	private WristPIDMessage message;
	
	private enum State{
		down,
		middle,		
		back,
		stop
	}
	
	public WristPIDCommand(Message m) {
		this.message = (WristPIDMessage)m;
		done = false;
		currentState = State.stop;
		
		if(message.getWristPosition() == 1){
			System.out.println("to middle__________");
			switchState(State.middle);
			Arm.wristPID.setSetpoint(Constants.armWristMiddle);
		}
		else if(message.getWristPosition() == 2){
			switchState(State.back);
			Arm.wristPID.setSetpoint(Constants.armWristBack);
		}
		else{
			switchState(State.down);
			Arm.wristPID.setSetpoint(Constants.armWristDown);
		}
	}

	@Override
	public void update() {
		switch(currentState){
			case middle:
				System.out.println("___________________the middle case for wrist");
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(-Arm.wristPID.getOutput() * Constants.wristMiddlePIDScale);
				System.out.println("__________________WristPower: " + Arm.wristPID.getOutput() * Constants.wristMiddlePIDScale);
				
				if(Arm.wristPID.isDone()){
					done = true;
				}
				break;
			case back:
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(-Arm.wristPID.getOutput() * Constants.wristBackPIDScale);
				
				if(Arm.wristPID.isDone()){
					done = true;
				}
				
				break;
			case down:
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(-Arm.wristPID.getOutput() * Constants.wristDownPIDScale);
				
				if(Arm.wristPID.isDone()){
					done = true;
				}
				break;
			case stop:
				
				break;
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
