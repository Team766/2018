package com.team766.lib.Messages;

import lib.Message;

public class WristTestMessage implements Message{
	
	double left, right;
	
	public WristTestMessage(double left, double right){
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
