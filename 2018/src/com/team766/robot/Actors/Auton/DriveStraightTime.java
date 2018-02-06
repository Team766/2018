package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DriveTimeMessage;

import interfaces.AutonMode;
import lib.Message;

public class DriveStraightTime implements AutonMode{
	
	private boolean commandDone;
	private AutonSelector parent;
	
	private State currentState = State.Start;
	
	private enum State {
		Start,
		DriveStraight,
		Done
	}
	
	public DriveStraightTime(AutonSelector parent){
		this.parent = parent;
		commandDone = false;
	}

	@Override
	public void iterate() {
		switch (currentState) {
			case Start:
				switchState(State.DriveStraight, new DriveTimeMessage(2.0, 0.5, false));
				break;
			case DriveStraight:
				if(commandDone){
					setState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
				break;
		}
	}

	@Override
	public void commandDone(boolean done) {
		commandDone = done;
	}

	public State getState(){
		return currentState;
	}
	
	private void setState(State s){
		currentState = s;
	}
	
	private void switchState(State state, Message message){
		setState(state);
		parent.sendMessage(message);
		commandDone = false;
	}
}
