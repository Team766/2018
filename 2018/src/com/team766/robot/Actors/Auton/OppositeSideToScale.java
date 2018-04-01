package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;

public class OppositeSideToScale implements AutonMode{
	
	private boolean driveCommandDone, shoulderCommandDone;
	private AutonSelector parent;
	private State currentState;
	private int count, right;
	private double driveDist[];
	
	private enum State {
		Start, 
		Drive, 
		Turn,
		LiftArm,
		DropCube, 
		Done
	}
	
	public OppositeSideToScale(AutonSelector parent, boolean startingRight){
		System.out.println("-------------------running opposite to scale-------------------");
		right = startingRight ? 1 : -1;
		this.parent = parent;
		count = 0;
		driveCommandDone = false;
		shoulderCommandDone = false;
		currentState = State.Start;
		driveDist = new double[] {Constants.side_scale_opposite_forward_side, Constants.side_scale_opposite_forward_side_forward};
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				parent.sendMessage(new DrivePIDMessage(Constants.side_scale_opposite_forward, 0.0, true));
				switchState(State.Drive);
				break;
			case Drive:
				if(driveCommandDone){
					if(count < 2){
						int turnDirection = (((count % 2) == 0) ? 1 : -1) * right;
						parent.sendMessage(new DrivePIDMessage(0.0, turnDirection * 90.0, false));
						switchState(State.Turn);
					} else{
						switchState(State.LiftArm);
					}
				}
				break;
			case Turn:
				if(driveCommandDone){
					count ++;
					if(count < 3){
						System.out.println("count: " + count);
						double direction = (count == 1) ? right * 90.0 : 0.0;
						parent.sendMessage(new DrivePIDMessage(driveDist[count - 1], direction, false));
						switchState(State.Drive);
					}
				}
				break;
			case LiftArm:
				if(driveCommandDone){
					parent.sendMessage(new DrivePIDMessage(Constants.side_scale_same_forward_side, 0.0, false));
					switchState(State.DropCube);
				}
				break;
			case DropCube:
				System.out.println("inside drop cube case \t driveCommandDone: " + driveCommandDone);
				if(driveCommandDone){
					System.out.println("sent message to drop cube");
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
