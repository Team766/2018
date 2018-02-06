package com.team766.robot.Actors;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveUpdate;
//import com.team766.lib.Messages.GripperMotorUpdate;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.robot.Buttons;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Gripper.Gripper;

import interfaces.JoystickReader;
import lib.Actor;

public class OperatorControl extends Actor{
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoy();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoy();
	JoystickReader jBox = HardwareProvider.getInstance().getButtonJoy();
	
	private double previousLeftPower, previousRightPower, previousHeading;
	
	private double[] leftAxis = new double[4];
	private double[] rightAxis = new double[4];
	private boolean[] prevPress = new boolean[10];
	
	private void setAxis(JoystickReader joystick, double[] axis, int axisNumber, double deadband){
		axis[axisNumber] = (Math.abs(joystick.getRawAxis(axisNumber)) > deadband)? joystick.getRawAxis(axisNumber) : 0.0;
	}

	@Override
	public void iterate() {		
		
		/*
		 * 0 axis L/R
		 * 1 axis F/B
		 */
		
		setAxis(jLeft, leftAxis, 0, Constants.leftAxisDeadband);
		setAxis(jLeft, leftAxis, 1, Constants.leftAxisDeadband);
		
		setAxis(jRight, rightAxis, 0, Constants.rightAxisDeadband);
		setAxis(jRight, rightAxis, 1, Constants.rightAxisDeadband);
		
		//System.out.println("leftJoy: " + jLeft.getRawAxis(1));
		//System.out.println("rightJoy: " + jRight.getRawAxis(1));
		
		double leftPower = 0.0;
		double rightPower = 0.0;
		
		//calculating motor power based on the drive mode
		if(Constants.driveType == Constants.Drives.TankDrive){			
			leftPower = leftAxis[1];
			rightPower = rightAxis[1];
		}
		else if(Constants.driveType == Constants.Drives.SingleStick){
			leftPower = leftAxis[1] + leftAxis[0];
			rightPower = leftAxis[1] - leftAxis[0];
		}
		
		
		//sending calculated motor power (DriveDoubleSideUpdate)
		if(previousLeftPower != leftPower || previousRightPower != rightPower){
			sendMessage(new DriveDoubleSideUpdate(leftPower, rightPower));
			previousLeftPower = leftPower;
			previousRightPower = rightPower;
			System.out.println("left power = " + leftPower);
			System.out.println("right power = " + rightPower);
		}
		
		
		//System.out.println("left = " + leftPower + "\t\tright = " + rightPower);
//		
//		//button for open gripper(prevPress[0])
//		if(!prevPress[0] && jBox.getRawButton(Buttons.openGripper)){
//			System.out.println("button 1: " + jBox.getRawButton(Buttons.openGripper));
//			sendMessage(new GripperUpdate(false));
//		}
//		prevPress[0] = jBox.getRawButton(Buttons.openGripper);
//		
//		//button for close gripper(prevPress[1])
//		if(!prevPress[1] && jBox.getRawButton(Buttons.closeGripper)){
//			System.out.println("button 2: " + jBox.getRawButton(Buttons.closeGripper));
//			sendMessage(new GripperUpdate(true));
//		}
//		prevPress[1] = jBox.getRawButton(Buttons.closeGripper);
//		
//		//button for intake block (prevPress[5])
//		if(!prevPress[5] && jBox.getRawButton(Buttons.intakeBlock)){
//			System.out.println("button 5(intaking): " + jBox.getRawButton(Buttons.intakeBlock));
//			sendMessage(new GripperMotorUpdate(Constants.gripperMotorSpeed));
//		}
//		prevPress[5] = jBox.getRawButton(Buttons.intakeBlock);
//		
//		//button for stop gripper motors (prevPress[4])
//		if(!prevPress[4] && jBox.getRawButton(Buttons.stopGripperMotor)){
//			System.out.println("button 4(stopping): " + jBox.getRawButton(Buttons.stopGripperMotor));
//			sendMessage(new GripperMotorUpdate(0.0));
//		}
//		prevPress[4] = jBox.getRawButton(Buttons.stopGripperMotor);
//		
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
