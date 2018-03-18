package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.ArmStageMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;

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
		System.out.println("inside arm PID auton");
		switch (currState){
			case Start:
				setState(State.ArmMovement);
				parent.sendMessage(new ShoulderPIDMessage(2)); //moves shoulder up to vertical
				break;
			case ArmMovement:
				if(commandDone){
					setState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
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
	
	public String getTarget(){
		return "Arm";
	}

}
