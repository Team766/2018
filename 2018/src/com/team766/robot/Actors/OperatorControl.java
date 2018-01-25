package com.team766.robot.Actors;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.lib.Messages.DriveUpdate;
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
		System.out.println("leftJoy: " + jLeft.getRawAxis(1));
		
		rightAxis[1] = (Math.abs(jRight.getRawAxis(1)) > Constants.rightAxisDeadband)? jRight.getRawAxis(1) : 0.0;
		//System.out.println("rightJoy: " + jRight.getRawAxis(1));
		
		if(Constants.driveType == Constants.Drives.Simple){
			if(previousLeft != leftAxis[1]){
				sendMessage(new DriveUpdate(leftAxis[1], DriveUpdate.Motor.leftDrive));
				System.out.println("left power = " + leftAxis[1]);
			}
			if(previousRight != jRight.getRawAxis(1)){
				sendMessage(new DriveUpdate(rightAxis[1], DriveUpdate.Motor.rightDrive));
			}
			previousLeft = leftAxis[1];
			previousRight = rightAxis[1];
		} 
		
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
