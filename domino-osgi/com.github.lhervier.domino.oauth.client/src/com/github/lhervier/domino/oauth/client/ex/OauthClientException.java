package com.github.lhervier.domino.oauth.client.ex;

public class OauthClientException extends RuntimeException {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -6019350761545118817L;

	/**
	 * Constructor
	 * @param message
	 */
	public OauthClientException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause
	 */
	public OauthClientException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor
	 * @param message the message
	 * @param cause the cause
	 */
	public OauthClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
