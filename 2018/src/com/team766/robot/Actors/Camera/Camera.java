package com.team766.robot.Actors.Camera;

import com.team766.robot.HardwareProvider;

import interfaces.CameraInterface;
import lib.Actor;

public class Camera extends Actor{
	
	CameraInterface camera = HardwareProvider.getInstance().getCamera();
	
	public Camera(){
		camera.startAutomaticCapture();
	}
	
	@Override
	public void iterate() {
		
	}
	@Override
	public String toString() {
		return "Actor: \tCamera";
	}

	

}
