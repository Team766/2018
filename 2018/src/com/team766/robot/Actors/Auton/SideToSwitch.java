package com.team766.robot.Actors.Auton;


import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;
import lib.Message;

public class SideToSwitch implements AutonMode{
	private boolean driveCommandDone, shoulderCommandDone;
	private AutonSelector parent;
	private State currentState;
	private double[] straightDist;
	private double[] turnAngle;
	private int count;
	private boolean isOppositeSide;
	private int negateAngle;
	
	private enum State{
		Start,
		DriveStraight,
		Turn,
		DriveRaiseArm,
		DropCube,
		Done
	}
	public SideToSwitch(AutonSelector parent, boolean isStartingRight){
		this.parent = parent;
		negateAngle = isStartingRight? -1 : 1;
		driveCommandDone = false;
		shoulderCommandDone = false;
		currentState = State.Start;
		count = 0;
		
		isOppositeSide = (Constants.switch_side == negateAngle) ;
		if(isOppositeSide){
			straightDist = new double[]{Constants.side_switch_forward, Constants.side_switch_forward_side, Constants.side_switch_forward_side_forward};
			turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, negateAngle * Constants.switchSecondTurnAngle, 0.0};
		} else{
			straightDist = new double[]{Constants.side_switch_straight, Constants.side_switch_straight_side};
			turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, 0.0};
		}
		
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				double dist = isOppositeSide? straightDist[count] : Constants.side_switch_straight;
				switchState(State.DriveStraight, new DrivePIDMessage(dist, 0, true));
				break;
			case DriveStraight:
				System.out.println("driving for " + straightDist[count] + " feet");
				if(driveCommandDone){
					switchState(State.Turn, new DrivePIDMessage(0.0, turnAngle[count], false));
				} 
				break;
			case Turn:
				System.out.println("turning for " + turnAngle[count] + " degrees");
				if(driveCommandDone){
					if((count < 2 && isOppositeSide) || (count < 1 && !isOppositeSide)){
						count += 1;
						switchState(State.DriveStraight, new DrivePIDMessage(straightDist[count], turnAngle[count - 1], false));
					} else{
						setState(State.DriveRaiseArm);
						parent.sendMessage(new DrivePIDMessage(Constants.switch_final_forward, 0.0, false));
						parent.sendMessage(new ShoulderPIDMessage(1)); 
					}	
				}
				break;
			case DriveRaiseArm:
				System.out.println("driving");
				if(driveCommandDone)
					switchState(State.DropCube, new GripperUpdateMessage(true));
					setState(State.Done);
				break;
			case DropCube:
				System.out.println("dropping the cube");
				setState(State.Done);
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
	
	private void switchState(State state, Message message){
		currentState = state;
		parent.sendMessage(message);
		driveCommandDone(false);
		shoulderCommandDone(false);
	}
	
	private void setState(State state){
		currentState = state;
		driveCommandDone(false);
		shoulderCommandDone(false);
	}
	
	public String getTarget(){
		return "Switch";
	}

	@Override
	public void shoulderCommandDone(boolean done) {
		shoulderCommandDone = done;
	}

}
