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
			Wrist.wristPID.setSetpoint(Constants.armWristMiddle);
		}
		else if(message.getWristPosition() == 2){
			switchState(State.intake);
			Wrist.wristPID.setSetpoint(Constants.armWristBack);
		}
		else{
			switchState(State.back);
			Wrist.wristPID.setSetpoint(Constants.armWristDown);
		}
	}

	@Override
	public void update() {
				
				System.out.println("Wrist PID setpoint: " + Wrist.wristPID.getSetpoint());
				Wrist.wristPID.calculate(Wrist.getAveWristEncoder(), false);
				Wrist.setWrist(negate * (Wrist.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Wrist.getWristAngleRad(Wrist.getAveWristEncoder()))));
				System.out.println("__________________WristPower: " + Wrist.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Wrist.getWristAngleRad(Wrist.getAveWristEncoder())));
				
				if(Wrist.wristPID.isDone()){
					done = true;
					System.out.println("done middle");
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