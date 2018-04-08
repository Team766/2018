package com.team766.robot.Actors;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.EStop;
import com.team766.lib.Messages.IntakeMotorUpdate;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.lib.Messages.WristSimpleMessage;
import com.team766.lib.Messages.WristTestMessage;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.ShifterUpdate;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.ShoulderSimpleMessage;
import com.team766.lib.Messages.ShoulderManualMessage;
import com.team766.robot.Buttons;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Intake.Intake;
import edu.wpi.first.wpilibj.DriverStation;
import com.team766.robot.Actors.Wrist.WristPIDCommand;
import com.team766.robot.Actors.Drive.Drive;

import interfaces.JoystickReader;
import lib.Actor;
import lib.Message;

public class OperatorControl extends Actor{
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoy();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoy();
	JoystickReader jBox = HardwareProvider.getInstance().getButtonJoy();
	
	private double previousLeftPower, previousRightPower, previousHeading;
	
	private double[] leftJoystick = new double[4];
	private double[] rightJoystick = new double[4];
	private boolean[] prevPress = new boolean[17];
	private boolean shifterStatus = false;
	private boolean state = false;
	
	private void setAxis(JoystickReader joystick, double[] axis, int axisNumber, double deadband){
		axis[axisNumber] = (Math.abs(joystick.getRawAxis(axisNumber)) > deadband)? joystick.getRawAxis(axisNumber) : 0.0;
	}

	@Override
	public void iterate() {	
		
		//System.out.println("POV: " + jBox.getPOV());
		
		
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
		
//		double shoulderPower = 0.0;
//		double wristPower = 0.0;
		
		// normalize the input we are getting from the joysticks so that it is 
		// easy to adapt to new joysticks
		double scaleLR = 1.0;
		double scaleFB = -1.0;
		double steeringPowerScale = 0.8;

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
			leftPower = rightJoystickFB + (leftJoystickLR * steeringPowerScale);
			rightPower = rightJoystickFB - (leftJoystickLR * steeringPowerScale);
		}
		else if(Constants.driveType == Constants.Drives.Arm){
			double leftShoulder = leftJoystickFB;
			double rightShoulder = rightJoystickFB;
			
			if(previousLeftPower != leftShoulder || previousRightPower != rightShoulder){
				sendMessage(new WristTestMessage(leftShoulder, rightShoulder));
				previousLeftPower = leftShoulder;
				previousRightPower = rightShoulder;
			}
		}
		
		/*
		 * shoulderPower = leftJoystickFB;
			wristPower = rightJoystickFB;
			
			if(previousLeftPower != shoulderPower || previousRightPower != wristPower){
				sendMessage(new ArmSimpleMessage(shoulderPower, wristPower));
				previousLeftPower = shoulderPower;
				previousRightPower = wristPower;
			}
			leftPower = leftJoystickFB + leftJoystickLR;
			rightPower = leftJoystickFB - leftJoystickLR;
		 */
		
		
		if(Constants.driveType != Constants.Drives.Arm){
			if(previousLeftPower != leftPower || previousRightPower != rightPower){
				sendMessage(new DriveDoubleSideUpdate(leftPower, rightPower));
				previousLeftPower = leftPower;
				previousRightPower = rightPower;
			}
		}
		
		/*
		//button for close gripper(prevPress[7])
		if(jBox.getRawButton(Buttons.gripper)){
			sendMessage(new GripperUpdateMessage(false));
		} else{
			sendMessage(new GripperUpdateMessage(true));
		}
		System.out.print("gripper: " + jBox.getRawButton(Buttons.gripper) + "------");
		*/
		
		//button for outtake wheels up (prevPress[6])
		if(jBox.getRawButton(Buttons.intakeBlock)) {
//			System.out.println("setting intake wheels to -1.0");
			sendMessage(new IntakeMotorUpdate(-1.0));
		} else if(jBox.getPOV() == Buttons.outtakeBlock){
//			System.out.println("setting intake wheels to 1.0");
			sendMessage(new IntakeMotorUpdate(1.0));
		} else{
//			System.out.println("setting intake wheels to 0.0");
			sendMessage(new IntakeMotorUpdate(0.0));
		}
		prevPress[6] = jBox.getRawButton(Buttons.outtakeBlock);
		//A4 intake wheels
		
		//button for stop everything 9
		if(!prevPress[9] && jBox.getRawButton(Buttons.eStop)){
			sendMessage(new EStop());
		}
		prevPress[9] = jBox.getRawButton(Buttons.eStop);
		
		//button for gripper (prevPress[12])
		if(!prevPress[12] && jBox.getRawButton(Buttons.gripperButton)){
			System.out.println("sent gripper message");
			state = !state;
			sendMessage(new GripperUpdateMessage(state));
		}
		prevPress[12] = jBox.getRawButton(Buttons.gripperButton);
		
		//Shifter button right joystick trigger 
		if (jRight.getTriggerPressed()){
			shifterStatus = !shifterStatus;
			sendMessage(new ShifterUpdate(shifterStatus));
//			System.out.println("left trigger is pressed");
		}
		
		//button for move arm shoulder vertical A1
		if(jBox.getPOV() == Buttons.shoulderVertical){
//			System.out.println("sent message for arm verticial");
			sendMessage(new ShoulderPIDMessage(2));
		}
		 
		//shoulder middle A2
		if(jBox.getPOV() == Buttons.shoulderMiddle){
//			System.out.println("sent message for arm middle");
			sendMessage(new ShoulderPIDMessage(1));
		}
		
		//button for move arm backward A3
		if(jBox.getPOV() == Buttons.shoulderBottom){
//			System.out.println("sent message for arm bottom");
			//sendMessage(new Done());
			sendMessage(new ShoulderPIDMessage(0));
		}
		
		//Button for moving wrist to the intake position(prevPress[10])
		if(!prevPress[10] && jBox.getRawButton(Buttons.wristIntake)){
//			System.out.println("button 10 is pressed");
			sendMessage(new WristPIDMessage(2));
		}
		prevPress[10] = jBox.getRawButton(Buttons.wristIntake);
		
		//button for moving wrist to the middle(prevPress[11])
		if(!prevPress[11] && jBox.getRawButton(Buttons.wristMiddle)){
//			System.out.println("11 is pressed");
			sendMessage(new WristPIDMessage(1));
		}
		prevPress[11] = jBox.getRawButton(Buttons.wristMiddle);
		
		//button for moving wrist all the way to the back(prevPress[8])
		if(!prevPress[8] && jBox.getRawButton(Buttons.wristBack)){
			sendMessage(new WristPIDMessage(0));
		}
		prevPress[8] = jBox.getRawButton(Buttons.wristBack);
		
		//button for moving wrist up manually
		if(!prevPress[2] && jBox.getRawButton(Buttons.manualWristUp)){
//			System.out.println("wrist manual up");
			int setPoint = 0;
			sendMessage(new WristSimpleMessage(setPoint));
		} else if (prevPress[2] && !jBox.getRawButton(Buttons.manualWristUp)){
//			System.out.println("hold wrist manual up");
			int setPoint = 3;
			sendMessage(new WristPIDMessage(setPoint));
		}
		prevPress[2] = jBox.getRawButton(Buttons.manualWristUp);
				
		//wrist down manually
		if(!prevPress[4] && jBox.getRawButton(Buttons.manualWristDown)){
//			System.out.println("wrist manual down");
			sendMessage(new WristSimpleMessage(1));
		} else if(prevPress[4] && !jBox.getRawButton(Buttons.manualWristDown)){
//			System.out.println("wrist manual down");
			sendMessage(new WristPIDMessage(3));
		}
		prevPress[4] = jBox.getRawButton(Buttons.manualWristDown);
		
		//shoulder up manually
		if(!prevPress[3] && jBox.getRawButton(Buttons.manualShoulderUp)){
//			System.out.println("manual shoulder up");
			sendMessage(new ShoulderPIDMessage(4));
			//int setPoint = 0;
			//sendMessage(new ShoulderManualMessage(setPoint));
		} else if (prevPress[3] && !jBox.getRawButton(Buttons.manualShoulderUp)){
//			System.out.println("manual shoulder hold");
			sendMessage(new ShoulderPIDMessage(3));
		}
		prevPress[3] = jBox.getRawButton(Buttons.manualShoulderUp);
	
		//shoulder down manually
		if(!prevPress[5] && jBox.getRawButton(Buttons.manualShoulderDown)){
//			System.out.println("manual shoulder down");
			sendMessage(new ShoulderPIDMessage(5));
			//int setPoint = 1;
			//sendMessage(new ShoulderManualMessage(setPoint));
		} else if(prevPress[5] && !jBox.getRawButton(Buttons.manualShoulderDown)){
//			System.out.println("manual shoulder hold");
			sendMessage(new ShoulderPIDMessage(3));
		}
		prevPress[5] = jBox.getRawButton(Buttons.manualShoulderDown);
	}

	@Override
	public String toString() {
		return "Actor: Operator Control";
	}
}
