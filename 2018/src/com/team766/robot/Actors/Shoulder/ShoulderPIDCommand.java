package com.team766.robot.Actors.Shoulder;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;

import lib.ConstantsFileReader;
import lib.Message;

public class ShoulderPIDCommand extends CommandBase{
	/*
	 * This command moves the shoulder to one of three setpoints
	 */
	private boolean done;
	private State currentState;
	private ShoulderPIDMessage message;
	ConstantsFileReader constants_file;
	private double startTime;
	private boolean reachTimeLimit;
	
	private enum State{
		Down,
		Middle,
		Up,
		StayVertical,
		Hold
	}
	
	private State[] positions;
	private String[] encoderPos;
	
	public ShoulderPIDCommand(Message m){
		message = (ShoulderPIDMessage) m;
		done = false;
		constants_file = ConstantsFileReader.getInstance();
		positions = new State[]{State.Down, State.Middle, State.Up, State.Hold};
		encoderPos = new String[]{"armShoulderBottom", "armShoulderMiddle", "armShoulderVertical"};
		
		switchState(positions[message.getDesiredPos()]);
		
		if(message.getDesiredPos() == 3){
			Shoulder.shoulderUpPID.setSetpoint(Shoulder.getAveShoulderEncoder());
		} else{
			Shoulder.shoulderUpPID.setSetpoint(constants_file.get(encoderPos[message.getDesiredPos()]));
		}
		startTime = System.currentTimeMillis();
	}

	@Override
	public void update() {
		double currPos = Shoulder.getAveShoulderEncoder();
		Shoulder.shoulderUpPID.calculate(currPos, false);
		double power = Constants.shoulderUpPIDScale * Shoulder.shoulderUpPID.getOutput() + constants_file.get("shoulderUpFeedForward") * Math.cos(Shoulder.getShoulderAngleRad(currPos));
		System.out.println("pid setpoint: " + Shoulder.shoulderUpPID.getSetpoint());
		//System.out.println("Shoulder power: " + Constants.shoulderUpPIDScale * output + ff);
		reachTimeLimit = (System.currentTimeMillis() - startTime) >= constants_file.get("shoulderTimeLimit");
		switch (currentState){
			case Up:
				System.out.println("shoulder going up to vertical");
				if(!reachTimeLimit){
					Shoulder.setShoulder(power); //radians
				} else{
					Shoulder.setShoulderBalance(power);
				}
				if(currPos > constants_file.get("armShoulderVertical") - constants_file.get("k_shoulderUpThresh")){
					switchState(State.StayVertical);
				}
				break;
			case StayVertical:
				Shoulder.setShoulderBalance(power);
				System.out.println("shoulder staying vertical");
				if(currPos < constants_file.get("armShoulderVertical") - constants_file.get("shoulderSwitchClamp")){
					switchState(State.Up);
				}
				if(Shoulder.shoulderUpPID.isDone()){
					done = true;
				}
				break;
			case Middle:
				if(!reachTimeLimit){
					Shoulder.setShoulder(power);
				} else{
					Shoulder.setShoulderBalance(power);
				}
				System.out.println("shoulder going to middle");
				if(Shoulder.shoulderUpPID.isDone()){
					done = true;
				}
				break;
			case Down:
				Shoulder.setShoulder(power);
				System.out.println("Case Down");
				if(Shoulder.shoulderUpPID.isDone()){
					System.out.println("done going down");
					done = true;
				}
				break;
			case Hold:
				System.out.println("shoulder holding position");
				if(!reachTimeLimit){
					Shoulder.setShoulder(power);
				} else{
					Shoulder.setShoulderBalance(power);
				}
				
				if(Shoulder.shoulderUpPID.isDone()){
					done = true;
				}
				break;
		}
	}

	@Override
	public void stop() {
		Shoulder.setShoulder(0.0);
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	private void switchState(State state){
		currentState = state;
		System.out.println("Switching to state " + state.name());
	}

}
