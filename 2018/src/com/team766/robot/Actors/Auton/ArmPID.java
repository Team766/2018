package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.ArmStageMessage;

import interfaces.AutonMode;

public class ArmPID implements AutonMode{
	
	private State currState;
	private AutonSelector parent;
	private boolean commandDone;
	
	private enum State{
		Start,
		ArmMovement,
		Done
	}

	public ArmPID(AutonSelector parent){
		this.parent = parent;
		currState = State.Start;
		commandDone = false;
	}
	
	@Override
	public void iterate() {
		switch (currState){
			case Start:
				setState(State.ArmMovement);
				parent.sendMessage(new ArmStageMessage(3.0)); //3 feet
				break;
			case ArmMovement:
				if(commandDone){
					setState(State.Done);
				}
				break;
			case Done:
				break;
		}
	}
	
	private void setState(State s){
		currState = s;
	}

	@Override
	public void commandDone(boolean done) {
		commandDone = done;
	}

}
