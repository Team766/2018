package com.team766.robot;

import interfaces.MyRobot;
import lib.HTTPServer;
import lib.LogFactory;
import lib.Logger.Level;
import lib.Scheduler;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants.Autons;
import com.team766.robot.Actors.OperatorControl;
import com.team766.robot.Actors.Auton.AutonSelector;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * 2018 Robot Code
 * 
 * @author Margaret Chan
 */
public class Robot implements MyRobot {
	private HTTPServer httpServer;
	
	public enum GameState{
		Teleop,
		Disabled,
		Test,
		Auton
	}

	public static GameState gameState = GameState.Disabled;

	public static GameState getState() {
		return gameState;
	}

	public static void setState(GameState state) {
		gameState = state;
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		CommandBase.init();
		
		Scheduler.getInstance().add(CommandBase.Drive);
		Scheduler.getInstance().add(CommandBase.Gripper);
		//Scheduler.getInstance().add(CommandBase.Arm);
		
		System.out.println("It works!!!");
		log(Level.INFO, "Robot Starting");

		httpServer = new HTTPServer(Constants.Autons.class);
		httpServer.start();
	}

	
	public void disabledInit() {
		setState(GameState.Disabled);
	}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	public void autonomousInit() {
		log(Level.INFO, "Auton Init / Match Starting");
		setState(GameState.Auton);
		Scheduler.getInstance().remove(OperatorControl.class);
		Scheduler.getInstance().remove(AutonSelector.class);

		sendStopMessage();

		log(Level.INFO, "Starting AutonSelector");
		Constants.Autons selected_auton = httpServer.getSelectedAutonMode(Constants.Autons.class);
		
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		for(int i = 0; i < 2; i++){
			boolean right = true;
			if(gameData.charAt(i) == 'L'){
				right = false;
			}
			if(i == 0){
				Constants.switch_side = right ? 1 : -1;
				break;
			}
			Constants.scale_side = right ? 1 : -1;
		}
			
		
		Scheduler.getInstance().add(new AutonSelector(selected_auton));
	}

	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}
	
	@Override
	public void teleopInit() {
		log(Level.INFO, "Teleop Init");
		setState(GameState.Teleop);

		Scheduler.getInstance().remove(AutonSelector.class);

		sendStopMessage();

		Scheduler.getInstance().add(new OperatorControl());
	}

	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
	
	public void testInit() {
		setState(GameState.Test);
	}
	
	public void testPeriodic() {
		Scheduler.getInstance().run();
	}
	
	public String toString(){
		return "2018 Robot";
	}
	
	private void sendStopMessage(){
		try {
			Scheduler.getInstance().sendMessage(new Stop());
		} catch (InterruptedException e) {
			e.printStackTrace();
			log(Level.ERROR, "Failed to send Stop message");
		}
	}
	
	private void log(Level lvl, String message){
		LogFactory.getInstance("Robot").log(lvl, getCallingClass() + ": " + message);
	}
	
	private String getCallingClass(){
		Object[] out = Thread.currentThread().getStackTrace();
		return out[2].toString();
	}
}

