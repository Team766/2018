package com.team766.robot.Actors.Auton;


import com.team766.lib.Messages.WristPIDMessage;
import interfaces.AutonMode;

public class WristPID implements AutonMode {

	private State currState;
	private AutonSelector parent;
	private boolean commandDone;
	private double startTime;
	
	private enum State{
		Start,
		Intake,
		RaiseUp,
		Done
	}
	
	public WristPID(AutonSelector parent) {
		this.parent = parent;
		currState = State.Start;
		commandDone = false;
	}

	@Override
	public void iterate() {
		switch(currState){
			case Start:
				startTime = System.currentTimeMillis();
				setState(State.Intake);
				parent.sendMessage(new WristPIDMessage(2)); //move the wrist to the intake position
				System.out.println("Getting to the starting!!!!!!");
				break;
			case Intake:
				System.out.println("-----------Case intake");
				if((System.currentTimeMillis() - startTime) > 5000 && commandDone){
					setState(State.RaiseUp);
					parent.sendMessage(new WristPIDMessage(1)); //move the wrist to the middle
				}
				break;
			case RaiseUp:
				System.out.println("------------Case RaiseUp");
				if((System.currentTimeMillis() - (startTime + 5000)) > 5000){
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
		return "Wrist";
	}

}
