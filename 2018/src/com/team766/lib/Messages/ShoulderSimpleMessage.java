package com.team766.lib.Messages;

import lib.Message;

public class ShoulderSimpleMessage implements Message{
	
	private double left, right;

	public ShoulderSimpleMessage(double left, double right){
		this.left = left;
		this.right = right;
	}
	
	public double getLeft(){
		return left;
	}
	
	public double getRight(){
		return right;
	}
}
