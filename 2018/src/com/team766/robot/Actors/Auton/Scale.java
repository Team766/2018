package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;

import interfaces.AutonMode;

public class Scale implements AutonMode{
	private boolean commandDone, lock;
	private AutonSelector parent;
	private State currentState;
	private double startTime;
	
	private enum State{
		Start,
		DriveRaiseArm,
		LowerArm,
		Done
	}
	
	public Scale(AutonSelector parent){
		this.parent = parent;
		commandDone = false;
		currentState = State.Start;
		lock = false;
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				switchState(State.DriveRaiseArm);
				parent.sendMessage(new DrivePIDMessage(4.0, 0.0));
				parent.sendMessage(new ShoulderPIDMessage(true));
				break;
			case DriveRaiseArm:
				System.out.println("driving and raising the arm");
				if(commandDone){
					if(!lock){
						startTime = System.currentTimeMillis();
						lock = true;
						System.out.println("set start time");
					}
					if((System.currentTimeMillis() - startTime) > 2000){
						System.out.println("sent lower arm message");
						switchState(State.LowerArm);
						parent.sendMessage(new ShoulderPIDMessage(false));
					}
				}
				break;
			case LowerArm:
				System.out.println("lowering the arm");
				if(commandDone){
					switchState(State.Done);
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
	
	private void switchState(State state){
		currentState = state;
		commandDone(false);
	}

}