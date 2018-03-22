package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;

public class SameSideToScale implements AutonMode{
	
	private boolean commandDone;
	private int right;
	private AutonSelector parent;
	private State currentState;
	
	private enum State{
		Start, Drive, Turn, LiftArm, DropCube, Done
	}
	
	public SameSideToScale(AutonSelector parent, boolean startingRight){
		System.out.println("-------------------running same side to scale-------------------------");
		this.parent = parent;
		right = startingRight ? 1 : -1;
		commandDone = false;
		currentState = State.Start;
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				System.out.println("===============sent message to drive at angle========================");
				switchState(State.Drive);
				parent.sendMessage(new DrivePIDMessage(Constants.side_scale_same_forward, right * Constants.side_scale_same_angle));
				break;
			case Drive:
				System.out.println("driving");
				if(commandDone){
					parent.sendMessage(new DrivePIDMessage(0.0, right * Constants.side_scale_same_forward_angle));
					System.out.println("_________________sending mesage to lift arm__________________");
					//parent.sendMessage(new ShoulderPIDMessage(2));
					//parent.sendMessage(new WristPIDMessage(2));
					switchState(State.Turn);
				}
				break;
			case Turn:
				System.out.println("turning in place");
				if(commandDone){
					parent.sendMessage(new DrivePIDMessage(Constants.side_scale_same_forward_side, 0.0));
					switchState(State.DropCube);
				}
				break;
			case DropCube:
				System.out.println("completeting last drive forward");
				if(commandDone){
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
	public void commandDone(boolean done) {
		commandDone = done;
	}
	
	private void switchState(State s){
		currentState = s;
		commandDone = false;
	}
	
	public String getTarget(){
		return "Scale";
	}

}
