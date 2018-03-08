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
	
	private enum State{
		Down,
		Middle,
		Up,
		StayVertical
	}
	
	private State[] positions;
	private String[] encoderPos;
	
	public ShoulderPIDCommand(Message m){
		message = (ShoulderPIDMessage) m;
		done = false;
		constants_file = ConstantsFileReader.getInstance();
		
		positions = new State[]{State.Down, State.Middle, State.Up};
		encoderPos = new String[]{"armShoulderBottom", "armShoulderMiddle", "armShoulderVertical"};
		
		switchState(positions[message.getDesiredPos()]);
		Shoulder.shoulderUpPID.setSetpoint(constants_file.get(encoderPos[message.getDesiredPos()]));
	}

	@Override
	public void update() {
		double currPos = Shoulder.getAveShoulderEncoder();
		Shoulder.shoulderUpPID.calculate(currPos, false);
		double output = Shoulder.shoulderUpPID.getOutput();
		double ff = constants_file.get("shoulderUpFeedForward") * Math.cos(Shoulder.getShoulderAngleRad(currPos));
		System.out.println("pid setpoint: " + Shoulder.shoulderUpPID.getSetpoint());
		switch (currentState){
			case Up:
				Shoulder.setShoulder(Constants.shoulderUpPIDScale * output + ff); //radians
				if(currPos > constants_file.get("armShoulderVertical") - constants_file.get("k_shoulderUpThresh")){
					switchState(State.StayVertical);
					System.out.println("Shoulder case up command");
				}
				break;
			case StayVertical:
				Shoulder.setShoulderBalance(Constants.shoulderUpPIDScale * output + ff);
				System.out.println("stay vertical");
				if(currPos < constants_file.get("armShoulderVertical") - constants_file.get("shoulderSwitchClamp")){
					switchState(State.Up);
				}
				if(Shoulder.shoulderUpPID.isDone()){
					done = true;
				}
				break;
			case Middle:
				Shoulder.setShoulder(Constants.shoulderUpPIDScale * output + ff);
				System.out.println(" Case Middle");
				if(Shoulder.shoulderUpPID.isDone()){
					done = true;
				}
				break;
			case Down:
				Shoulder.setShoulder(Constants.shoulderUpPIDScale * output + ff);
				System.out.println("Case Down");
				if(Shoulder.shoulderUpPID.isDone()){
					System.out.println("done going down");
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
