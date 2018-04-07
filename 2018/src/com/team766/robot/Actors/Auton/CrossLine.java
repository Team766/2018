package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;

import interfaces.AutonMode;

public class CrossLine implements AutonMode{
	
	private boolean commandDone;
	private AutonSelector parent;
	private State currentState;
	
	private enum State{
		Start, Drive, Done
	}
	
	public CrossLine(AutonSelector parent){
		this.parent = parent;
		commandDone = false;
		currentState = State.Start;
	}

	public void iterate() {
		switch(currentState){
			case Start:
				System.out.println("inside cross line auton start");
				switchState(State.Drive);
				parent.sendMessage(new DrivePIDMessage(15.0, 0.0)); //15.0
				break;
			case Drive:
				if(commandDone){
					switchState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
		}
	}

	public void driveCommandDone(boolean done) {
		commandDone = done;
	}
	
	private void switchState(State s){
		currentState = s;
		commandDone = false;
	}
	
	public String getTarget(){
		return "Cross Line";
	}

	@Override
	public void shoulderCommandDone(boolean done) {
		
	}

}
