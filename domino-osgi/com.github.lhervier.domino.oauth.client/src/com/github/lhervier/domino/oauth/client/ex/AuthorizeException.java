package com.github.lhervier.domino.oauth.client.ex;

import com.github.lhervier.domino.oauth.client.model.AuthorizeError;

/**
 * Error while trying to access the authorization server
 * @author Lionel HERVIER
 */
public class AuthorizeException extends Exception {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1176608501185902466L;
	
	/**
	 * The error
	 */
	private final AuthorizeError error;
	
	/**
	 * Constructor
	 */
	public AuthorizeException(AuthorizeError e) {
		this.error = e;
	}

	public AuthorizeError getError() {return error;}
}
