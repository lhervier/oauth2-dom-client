package com.github.lhervier.domino.oauth.client.ex;

import com.github.lhervier.domino.oauth.client.model.GrantError;

/**
 * Error while trying to access the token endpoint
 * @author Lionel HERVIER
 */
public class GrantException extends Exception {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -378642073728141057L;
	
	/**
	 * The error
	 */
	private final GrantError error;
	
	/**
	 * Constructor
	 */
	public GrantException(GrantError e) {
		this.error = e;
	}

	public GrantError getError() {return error;}
}
