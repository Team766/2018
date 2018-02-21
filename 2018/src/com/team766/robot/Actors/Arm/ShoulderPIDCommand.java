package com.team766.robot.Actors.Arm;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;

import lib.ConstantsFileReader;
import lib.Message;

public class ShoulderPIDCommand extends CommandBase{
	/*
	 * This command moves the shoulder between the lowest resting point and vertical position
	 */
	private boolean done;
	private State currentState;
	private ShoulderPIDMessage message;
	
	private enum State{
		StayVertical,
		Up,
		Down,
		Stop
	}
	
	public ShoulderPIDCommand(Message m){
		message = (ShoulderPIDMessage) m;
		done = false;
		currentState = State.Stop;
		
		if(message.toVertical()){
			switchState(State.Up);
			Arm.shoulderUpPID.setSetpoint(ConstantsFileReader.getInstance().get("armShoulderVertical"));
		} else{
			switchState(State.Down);
			Arm.shoulderUpPID.setSetpoint(ConstantsFileReader.getInstance().get("armShoulderBottom"));
		}
	}

	@Override
	public void update() {
		switch (currentState){
			case Up:
				Arm.shoulderUpPID.calculate(Arm.getAveShoulderEncoder(), false);
				Arm.setShoulder(Constants.shoulderUpPIDScale * Arm.shoulderUpPID.getOutput() + ConstantsFileReader.getInstance().get("shoulderUpFeedForward") * Math.cos(Arm.getShoulderAngleRad(Arm.getAveShoulderEncoder()))); //radians
				if(Arm.getAveShoulderEncoder() > ConstantsFileReader.getInstance().get("armShoulderVertical") - ConstantsFileReader.getInstance().get("k_shoulderUpThresh")){
					switchState(State.StayVertical);
				}
				break;
			case StayVertical:
				Arm.shoulderUpPID.calculate(Arm.getAveShoulderEncoder(), false);
				Arm.setShoulderBalance(Constants.shoulderUpPIDScale * Arm.shoulderUpPID.getOutput() + ConstantsFileReader.getInstance().get("shoulderUpFeedForward") * Math.cos(Arm.getShoulderAngleRad(Arm.getAveShoulderEncoder())));
				if(Arm.getAveShoulderEncoder() < ConstantsFileReader.getInstance().get("armShoulderVertical") - ConstantsFileReader.getInstance().get("shoulderSwitchClamp")){
					switchState(State.Up);
				}
				break;
			case Down:
				double ff = ConstantsFileReader.getInstance().get("shoulderUpFeedForward") * Math.cos(Arm.getShoulderAngleRad(Arm.getAveShoulderEncoder()));
				Arm.shoulderUpPID.calculate(Arm.getAveShoulderEncoder(), false);
				Arm.setShoulder(Constants.shoulderUpPIDScale * Arm.shoulderUpPID.getOutput() + ff);
				
				break;
			case Stop:
				
				break;
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
	
	private void switchState(State state){
		currentState = state;
	}

}
