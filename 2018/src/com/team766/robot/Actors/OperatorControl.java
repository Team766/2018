package com.team766.robot.Actors;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.robot.Buttons;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.JoystickReader;
import lib.Actor;

public class OperatorControl extends Actor{
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoy();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoy();
	JoystickReader jBox = HardwareProvider.getInstance().getButtonJoy();
	
	private double previousLeft, previousRight, previousHeading;
	
	private double[] leftAxis = new double[4];
	private double[] rightAxis = new double[4];
	private boolean[] prevPress = new boolean[10];

	@Override
	public void iterate() {		
		
		/*
		 * 0 axis L/R
		 * 1 axis F/B
		 */
		
		leftAxis[1] = (Math.abs(jLeft.getRawAxis(1)) > Constants.leftAxisDeadband)? jLeft.getRawAxis(1) : 0.0;
		//System.out.println("leftJoy: " + jLeft.getRawAxis(1));
		
		rightAxis[1] = (Math.abs(jRight.getRawAxis(1)) > Constants.rightAxisDeadband)? jRight.getRawAxis(1) : 0.0;
		//System.out.println("rightJoy: " + jRight.getRawAxis(1));
		
		if(Constants.driveType == Constants.Drives.Simple){
			if(previousLeft != leftAxis[1]){
				sendMessage(new DriveUpdate(leftAxis[1], DriveUpdate.Motor.leftDrive));
				//System.out.println("left power = " + leftAxis[1]);
			}
			if(previousRight != jRight.getRawAxis(1)){
				sendMessage(new DriveUpdate(rightAxis[1], DriveUpdate.Motor.rightDrive));
				//System.out.println("right power = " + rightAxis[1]);
			}
			previousLeft = leftAxis[1];
			previousRight = rightAxis[1];
			System.out.println("left: " + previousLeft);
			System.out.println("right: " + previousRight);
		}
		
		//button for open gripper(prevPress[0])
		if(!prevPress[0] && jBox.getRawButton(Buttons.openGripper)){
			System.out.println("button 1: " + jBox.getRawButton(Buttons.openGripper));
			sendMessage(new GripperUpdate(false, false));
		}
		prevPress[0] = jBox.getRawButton(Buttons.openGripper);
		
		//button for close gripper + intake (prevPress[1])
		if(!prevPress[1] && jBox.getRawButton(Buttons.intakeBlock)){
			System.out.println("button 2: " + jBox.getRawButton(Buttons.intakeBlock));
			sendMessage(new GripperUpdate(true, false));
		}
		prevPress[1] = jBox.getRawButton(Buttons.intakeBlock);
		
		//button for climb down (prevPress[2]) 
		if(!prevPress[2] && jBox.getRawButton(Buttons.climbDown)) {
			sendMessage(new ClimberUpdate(false));
			prevPress[2] = jBox.getRawButton(Buttons.climbDown);
		}
		
		//button for climb up (prevPress[3])
		if(!prevPress[3] && jBox.getRawButton(Buttons.climbUp)) {
			sendMessage(new ClimberUpdate(true));
			prevPress[3] = jBox.getRawButton(Buttons.climbUp);
		}
	}

	@Override
	public String toString() {
		return "Actor: Operator Control";
	}
}
