package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import interfaces.AutonMode;

public class DrivePID implements AutonMode{
	private boolean commandDone;
	private AutonSelector parent;
	private State currState;
	
	private enum State {
		Start,
		Drive,
		Done
	}
	
	public DrivePID(AutonSelector parent){
		this.parent = parent;
		currState = State.Start;
		commandDone = false;
	}

	@Override
	public void iterate() {
		switch(currState){
			case Start:
				setState(State.Drive);
				parent.sendMessage(new DrivePIDMessage(0.0, 90.0));
				break;
			case Drive:
				if(commandDone){
					setState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
				break;
		}
	}

	public State getState(){
		return currState;
	}
	
	private void setState(State s){
		currState = s;
	}
	
	@Override
	public void commandDone(boolean done){
		commandDone = done;
	}
	
	public String getTarget(){
		return "Drive";
	}

}
