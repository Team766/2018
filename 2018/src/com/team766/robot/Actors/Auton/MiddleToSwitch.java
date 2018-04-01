package com.team766.robot.Actors.Auton;


import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;
import lib.Message;

public class MiddleToSwitch implements AutonMode {
	
	private boolean driveCommandDone, shoulderCommandDone;
	private AutonSelector parent;
	private State currentState;
	private double negateAngle;
	private double[]straightDist;
	private double[]turnAngle;
	private int count;
	
	private enum State{
		Start,
		DriveStraight,
		Turn,
		DriveRaiseArm,
		DropCube,
		Done
	}
	
	public MiddleToSwitch(AutonSelector parent) {
		this.parent = parent;
		driveCommandDone = false;
		shoulderCommandDone = false;
		currentState = State.Start;
		
		negateAngle = Constants.switch_side;
		if(negateAngle == 1){ //Constants.switch_side == 1){
			straightDist = new double[]{Constants.middle_switch_forward, Constants.middle_switch_forward_rightSide, Constants.middle_switch_forward_side_forward};
		} else{
			straightDist = new double[]{Constants.middle_switch_forward, Constants.middle_switch_forward_leftSide, Constants.middle_switch_forward_side_forward};
		}
		turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, negateAngle * Constants.switchSecondTurnAngle, 0.0};
		count = 0;
	}

	@Override
	public void iterate() {
		switch(currentState){
		case Start:
			switchState(State.DriveStraight, new DrivePIDMessage(straightDist[count], 0.0, true));
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
				if(count < 2){
					count += 1;
					switchState(State.DriveStraight, new DrivePIDMessage(straightDist[count], 0.0, false));
				} else{
					setState(State.DriveRaiseArm);
					parent.sendMessage(new DrivePIDMessage(Constants.switch_final_forward, 0.0, false));
					parent.sendMessage(new ShoulderPIDMessage(1));
				}	
			}
			break;
		case DriveRaiseArm:
			System.out.println("raising arms to the middle");
			if(driveCommandDone){
				switchState(State.DropCube, new GripperUpdateMessage(true));
				setState(State.Done);
			}
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
