package com.team766.robot.Actors;

import com.team766.lib.Messages.ArmSimpleMessage;
import com.team766.lib.Messages.ClimberUpdate;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.IntakeMotorUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.ShifterUpdate;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Buttons;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Intake.Intake;
import com.team766.robot.Actors.Drive.Drive;

import interfaces.JoystickReader;
import lib.Actor;

public class OperatorControl extends Actor{
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoy();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoy();
	JoystickReader jBox = HardwareProvider.getInstance().getButtonJoy();
	
	private double previousLeftPower, previousRightPower, previousHeading;
	
	private double[] leftJoystick = new double[4];
	private double[] rightJoystick = new double[4];
	private boolean[] prevPress = new boolean[14];
	private boolean shifterStatus = false;
	
	private void setAxis(JoystickReader joystick, double[] axis, int axisNumber, double deadband){
		axis[axisNumber] = (Math.abs(joystick.getRawAxis(axisNumber)) > deadband)? joystick.getRawAxis(axisNumber) : 0.0;
	}

	@Override
	public void iterate() {		
		
		/*
		 * 0 axis L/R
		 * 1 axis F/B
		 */
		
		setAxis(jLeft, leftJoystick, 0, Constants.leftAxisDeadband);
		setAxis(jLeft, leftJoystick, 1, Constants.leftAxisDeadband);
		
		setAxis(jRight, rightJoystick, 0, Constants.rightAxisDeadband);
		setAxis(jRight, rightJoystick, 1, Constants.rightAxisDeadband);
		
		double leftPower = 0.0;
		double rightPower = 0.0;
		
		double shoulderPower = 0.0;
		double wristPower = 0.0;
		
		// normalize the input we are getting from the joysticks so that it is 
		// easy to adapt to new joysticks
		double scaleLR = 1.0;
		double scaleFB = -1.0;

		double leftJoystickLR = leftJoystick[0] * scaleLR;
		double leftJoystickFB = leftJoystick[1] * scaleFB;
		
		double rightJoystickLR = rightJoystick[0] * scaleLR;
		double rightJoystickFB = rightJoystick[1] * scaleFB;
		
		// Now that we have the normalized inputs, let's
		// calculate motor power based on the drive mode
		
		if(Constants.driveType == Constants.Drives.TankDrive){			
			leftPower = leftJoystickFB;
			rightPower = rightJoystickFB;
		}
		else if(Constants.driveType == Constants.Drives.SingleStick){
			leftPower = leftJoystickFB + leftJoystickLR;
			rightPower = leftJoystickFB - leftJoystickLR;
		}
		else if(Constants.driveType == Constants.Drives.CheesyDrive){
			leftPower = rightJoystickFB - leftJoystickLR;
			rightPower = rightJoystickFB + leftJoystickLR;
		}
		else if(Constants.driveType == Constants.Drives.Arm){
			shoulderPower = leftJoystickFB;
			wristPower = rightJoystickFB;
			
			if(previousLeftPower != shoulderPower || previousRightPower != wristPower){
				sendMessage(new ArmSimpleMessage(shoulderPower, wristPower));
				previousLeftPower = shoulderPower;
				previousRightPower = wristPower;
			}
			leftPower = leftJoystickFB + leftJoystickLR;
			rightPower = leftJoystickFB - leftJoystickLR;
		}
		
		
		if(Constants.driveType != Constants.Drives.Arm){
			if(previousLeftPower != leftPower || previousRightPower != rightPower){
				sendMessage(new DriveDoubleSideUpdate(leftPower, rightPower));
				previousLeftPower = leftPower;
				previousRightPower = rightPower;
			}
		}
		
		
		//button for open gripper(prevPress[0])
		if(!prevPress[0] && jBox.getRawButton(Buttons.openGripper)){
			System.out.println("button 1: " + jBox.getRawButton(Buttons.openGripper));
			sendMessage(new GripperUpdate(false));
		}
		prevPress[0] = jBox.getRawButton(Buttons.openGripper);
		
		//button for close gripper(prevPress[1])
		if(!prevPress[1] && jBox.getRawButton(Buttons.closeGripper)){
			System.out.println("button 2: " + jBox.getRawButton(Buttons.closeGripper));
			sendMessage(new GripperUpdate(true));
		}
		prevPress[1] = jBox.getRawButton(Buttons.closeGripper);
		
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
		
		//button for stop gripper motors (prevPress[4])
		if(!prevPress[4] && jBox.getRawButton(Buttons.stopGripperMotor)){
			System.out.println("button 4(stopping): " + jBox.getRawButton(Buttons.stopGripperMotor));
			sendMessage(new IntakeMotorUpdate(0.0));
		}
		prevPress[4] = jBox.getRawButton(Buttons.stopGripperMotor);
		
		//button for intake block (prevPress[5])
		if(!prevPress[5] && jBox.getRawButton(Buttons.intakeBlock)){
			System.out.println("button 5(intaking): " + jBox.getRawButton(Buttons.intakeBlock));
			sendMessage(new IntakeMotorUpdate(Constants.intakeMotorSpeed));
		}
		prevPress[5] = jBox.getRawButton(Buttons.intakeBlock);
		
		//Shifter button
		if (!prevPress[7] && jBox.getRawButton(Buttons.shiftGear)){
			shifterStatus = !shifterStatus;
			sendMessage(new ShifterUpdate(shifterStatus));
			System.out.println("Button 7 is pressed");
		}
		prevPress[7] = jBox.getRawButton(Buttons.shiftGear);
		
		//button for move arm shoulder forward(prevPress[8])
		if(!prevPress[8] && jBox.getRawButton(Buttons.shoulderTop)){
			sendMessage(new Done());
			sendMessage(new ShoulderPIDMessage(2));
		}
		prevPress[8] = jBox.getRawButton(Buttons.shoulderTop);
		 
		if(!prevPress[6] && jBox.getRawButton(Buttons.shoulderMiddle)){
			sendMessage(new Done());
			sendMessage(new ShoulderPIDMessage(1));
		}
		prevPress[6] = jBox.getRawButton(Buttons.shoulderMiddle);
		
		//button for move arm backward(prevPress[9])
		if(!prevPress[9] && jBox.getRawButton(Buttons.shoulderBottom)){
			sendMessage(new Done());
			sendMessage(new ShoulderPIDMessage(0));
		}
		prevPress[9] = jBox.getRawButton(Buttons.shoulderBottom);
		
		//Button for moving wrist to the intake position(prevPress[10])
		if(!prevPress[10] && jBox.getRawButton(Buttons.wristIntake)){
			System.out.println("button 10 is pressed");
			sendMessage(new WristPIDMessage(2));
			prevPress[10] = jBox.getRawButton(Buttons.wristIntake);
		}
		prevPress[10] = jBox.getRawButton(Buttons.wristIntake);
		
		//button for moving wrist to the middle(prevPress[11])
		if(!prevPress[11] && jBox.getRawButton(Buttons.wristMiddle)){
			System.out.println("11 is pressed");
			sendMessage(new WristPIDMessage(1));
			prevPress[11] = jBox.getRawButton(Buttons.wristMiddle);
		}
		prevPress[11] = jBox.getRawButton(Buttons.wristMiddle);
		
		//button for moving wrist all the way to the back(prevPress[12])
		if(!prevPress[12] && jBox.getRawButton(Buttons.wristBack)){
			sendMessage(new WristPIDMessage(0));
			prevPress[12] = jBox.getRawButton(Buttons.wristBack);
		}
		prevPress[12] = jBox.getRawButton(Buttons.wristBack);
	}

	@Override
	public String toString() {
		return "Actor: Operator Control";
	}
}
