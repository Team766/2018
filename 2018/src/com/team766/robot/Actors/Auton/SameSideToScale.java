package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;

public class SameSideToScale implements AutonMode{
	
	private boolean driveCommandDone, shoulderCommandDone;
	private int right;
	private AutonSelector parent;
	private State currentState;
	
	private enum State{
		Start, 
		Drive, 
		Turn, 
		LiftArm, 
		DropCube, 
		Done
	}
	
	public SameSideToScale(AutonSelector parent, boolean startingRight){
		System.out.println("-------------------running same side to scale-------------------------");
		this.parent = parent;
		right = startingRight ? 1 : -1;
		driveCommandDone = false;
		shoulderCommandDone = false;
		currentState = State.Start;
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				System.out.println("===============sent message to drive at angle========================");
				switchState(State.Drive);
				parent.sendMessage(new DrivePIDMessage(Constants.side_scale_same_forward, right * Constants.side_scale_same_angle, true));
				break;
			case Drive:
				System.out.println("driving and turning " + right * Constants.side_scale_same_angle + " degrees");
				if(driveCommandDone){
					parent.sendMessage(new DrivePIDMessage(0.0, right * Constants.side_scale_same_forward_angle, false));
					System.out.println("_________________sending mesage to lift arm__________________");
					switchState(State.Turn);
				}
				break;
			case Turn:
				System.out.println("turning in place");
				if(driveCommandDone){
					parent.sendMessage(new ShoulderPIDMessage(2));
					parent.sendMessage(new WristPIDMessage(2));
					switchState(State.LiftArm);
				}
				break;
			case LiftArm:
				if(driveCommandDone){
					parent.sendMessage(new DrivePIDMessage(Constants.side_scale_same_forward_side, 0.0, false));
					switchState(State.DropCube);
				}
				break;
			case DropCube:
				System.out.println("completeting last drive forward");
				if(driveCommandDone){
					System.out.println("========================sent message to drop cube=====================");
					parent.sendMessage(new GripperUpdateMessage(true));
					switchState(State.Done);
				}
				break;
			case Done:
				parent.setDone(true);
				break;
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
		return "Scale";
	}

	@Override
	public void shoulderCommandDone(boolean done) {
		shoulderCommandDone = done;
	}

}
