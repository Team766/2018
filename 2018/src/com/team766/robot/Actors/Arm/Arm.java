package com.team766.robot.Actors.Arm;

import com.sun.prism.paint.Stop;
import com.team766.lib.Messages.ArmSimpleMessage;
import com.team766.lib.Messages.ArmStageMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.CANSpeedController;
import interfaces.CANSpeedController.ControlMode;
import interfaces.EncoderReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;
import lib.PIDController;

public class Arm extends Actor {
	
	private boolean commandFinished;
	
	private final double startingAngle = 0;
	private double shoulderSetPoint;
	
	Message currentMessage;
	SubActor currentCommand;
	

	//SpeedController leftArmShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightArmShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	//SpeedController leftArmWrist = HardwareProvider.getInstance().getLeftArmWrist();
	CANSpeedController rightArmWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	EncoderReader shoulderAngle = HardwareProvider.getInstance().getShoulderEncoder();
	EncoderReader wristEncoder = HardwareProvider.getInstance().getWristEncoder();
	
	PIDController intakePID = new PIDController(Constants.k_intakeP, Constants.k_intakeI, Constants.k_intakeP, Constants.k_intakeThresh);
	
	
	public Arm() {		
	}
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class, ArmStageMessage.class, Stop.class};
	}
	
	public String toString() {
		return "Actor: Arm";
	}

	public void iterate() {
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			if(currentMessage instanceof ArmSimpleMessage){
				currentCommand = null;
				ArmSimpleMessage armMessage = (ArmSimpleMessage)currentMessage;
				setArmShoulder(armMessage.getShoulderSpeed());
				setArmWrist(armMessage.getWristSpeed());
				printEncoder();
			}
			if(currentMessage instanceof ArmStageMessage){
				currentCommand = new ArmStageCommand(currentMessage);
			}
		}
	}
	
//	public void setLeftArmShoulder(double speed){
//		leftArmShoulder.set(speed);
//	}
	
	//PercentOutput is the mode for setting speed
	public void setRightArmShoulder(double speed){
		rightArmShoulder.set(ControlMode.PercentOutput, -speed);
	}
	
	//setting both motors for arm shoulder 
	public void setArmShoulder(double speed){
		//setLeftArmShoulder(speed);
		setRightArmShoulder(speed);
	}
	
//	public void setLeftArmWrist(double speed){
//		leftArmWrist.set(speed);
//	}
	
	public void setRightArmWrist(double speed){
		rightArmWrist.set(ControlMode.PercentOutput, -speed);
	}
	
	//setting both motors for arm wrist
	public void setArmWrist(double speed){
		//setLeftArmWrist(speed);
		setRightArmWrist(speed);
	}
	
	public void printEncoder(){
		System.out.println("Shoulder encoder value: " + shoulderAngle.get());
		System.out.println("Wrist encoder value: " + wristEncoder.get());
	}
	
	public double getShoulderAngle(){
		//assuming the reduction value is 1 for now...
		return (shoulderAngle.getRaw() * (360d/(1024.0 * 1)) + startingAngle);
	}
	
	public double getShoulderSetPoint(){
		return shoulderSetPoint;
	}
	
	public void setShoulderSetPoint(double setPoint){
		this.shoulderSetPoint = setPoint;
	}
	
	public double getHeight(){
		return 2 * 36 * (Math.sin(getShoulderAngle()));
	}
	
	public double getAngleFromHeight(double height){
		return Math.asin(height / (2 * 36));
	}
	
	public void setShoulderRotation(double speed){
		 if(speed > 0)
			 speed = Math.min(speed, 0.2);
		 else if(speed < 0)
			 speed = Math.max(speed, -0.2);
		 
		 setArmShoulder(speed);
	}
	
	public void resetEncoder(){
		shoulderAngle.reset();
	}
	
	
	
	
	
	

}
