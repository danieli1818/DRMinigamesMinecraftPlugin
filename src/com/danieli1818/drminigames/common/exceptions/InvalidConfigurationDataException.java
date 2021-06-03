package com.danieli1818.drminigames.common.exceptions;

public class InvalidConfigurationDataException extends RuntimeException {

	private String message;
	
	public InvalidConfigurationDataException() {
		this.message = "An Invalid Configuration Data Has Been Found!";
	}
	
	public InvalidConfigurationDataException(String message) {
		if (message == null) {
			this.message = "An Invalid Configuration Data Has Been Found!";
		} else {
			this.message = message;
		}
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
}
