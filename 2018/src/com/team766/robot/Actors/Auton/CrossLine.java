package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;

import interfaces.AutonMode;

public class CrossLine implements AutonMode{
	
	private boolean driveCommandDone, shoulderCommandDone;
	private AutonSelector parent;
	private State currentState;
	
	private enum State{
		Start, Drive, Done
	}
	
	public CrossLine(AutonSelector parent){
		this.parent = parent;
		driveCommandDone = false;
		shoulderCommandDone = false;
		currentState = State.Start;
	}

	public void iterate() {
		switch(currentState){
			case Start:
				switchState(State.Drive);
				parent.sendMessage(new DrivePIDMessage(7.0, 0.0, true));
				break;
			case Drive:
				if(driveCommandDone){
					switchState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
		}
	}

	@Override
	public void driveCommandDone(boolean done) {
		driveCommandDone = done;
	}
	
	private void switchState(State s){
		currentState = s;
		driveCommandDone = false;
		shoulderCommandDone = false;
	}
	
	public String getTarget(){
		return "Cross Line";
	}

	@Override
	public void shoulderCommandDone(boolean done) {
		shoulderCommandDone = false;
	}

}
