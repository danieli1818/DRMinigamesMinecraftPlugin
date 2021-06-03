package com.danieli1818.drminigames.common.exceptions;

public class ArgumentOutOfBoundsException extends Exception {

	private String message;
	
	public ArgumentOutOfBoundsException() {
		this.message = "An Argument's Value Is Out Of Bounds!";
	}
	
	public ArgumentOutOfBoundsException(String message) {
		if (message == null) {
			this.message = "An Argument's Value Is Out Of Bounds!";
		} else {
			this.message = message;
		}
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
	
}
