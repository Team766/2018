package com.team766.robot.Actors.Arm;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import lib.ConstantsFileReader;
import lib.Message;



public class WristPIDCommand extends CommandBase {
	/*
	 * This command moves the wrist between the lowest resting point and back position
	 */
	private boolean done;
	private State currentState;
	private WristPIDMessage message;
	private int negate = 1;
	
	private enum State{
		back,
		middle,		
		intake,
		stop
	}
	
	public WristPIDCommand(Message m) {
		this.message = (WristPIDMessage)m;
		done = false;
		currentState = State.stop;
		
		if(message.getWristPosition() == 1){
			//System.out.println("to middle__________");
			switchState(State.middle);
			Arm.wristPID.setSetpoint(Constants.armWristMiddle);
		}
		else if(message.getWristPosition() == 2){
			switchState(State.intake);
			Arm.wristPID.setSetpoint(Constants.armWristBack);
		}
		else{
			switchState(State.back);
			Arm.wristPID.setSetpoint(Constants.armWristDown);
		}
	}

	@Override
	public void update() {
		switch(currentState){
			case middle:
				System.out.println("___________________the middle case for wrist");
				System.out.println("Wrist PID setpoint: " + Arm.wristPID.getSetpoint());
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(negate * (Arm.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder()))));
				System.out.println("__________________WristPower: " + Arm.wristPID.getOutput() * Constants.wristMiddlePIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder())));
				//System.out.println("WristPower: " + Arm.leftWrist.get());
				
				
				if(Arm.wristPID.isDone()){
					done = true;
					System.out.println("done middle");
				}
				break;
			case intake:
				//System.out.println("___________________the intake case for wrist");
				System.out.println("Wrist PID setpoint: " + Arm.wristPID.getSetpoint());
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(negate * (Arm.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder()))));
				System.out.println("__________________WristPower: " + Arm.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder())));
				//System.out.println("WristPower: " + Arm.leftWrist.get());
				if(Arm.wristPID.isDone()){
					done = true;
					System.out.println("done intake");
				}
				
				break;
			case back:
				//System.out.println("___________________the back case for wrist");
				System.out.println("Wrist PID setpoint: " + Arm.wristPID.getSetpoint());
				Arm.wristPID.calculate(Arm.getAveWristEncoder(), false);
				Arm.setWrist(negate * (Arm.wristPID.getOutput() * Constants.wristDownPIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder()))));
				System.out.println("__________________WristPower: " + Arm.wristPID.getOutput() * Constants.wristBackPIDScale + Constants.armWrisFeedForward * Math.cos(Arm.getWristAngleRad(Arm.getAveWristEncoder())));
				//System.out.println("WristPower: " + Arm.leftWrist.get());
				if(Arm.wristPID.isDone()){
					done = true;
					System.out.println("done back");
				}
				break;
			case stop:
				System.out.println("stopping");
				break;
		}

	}

	@Override
	public void stop() {
		Arm.setWrist(0);

	}

	@Override
	public boolean isDone() {
		stop();
		return done;
	}
	
	
	private void switchState(State state){
		currentState = state;
	}

}
