package com.github.lhervier.domino.oauth.client.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.github.lhervier.domino.oauth.client.ex.AuthorizeException;
import com.github.lhervier.domino.oauth.client.ex.GrantException;
import com.github.lhervier.domino.oauth.client.ex.RefreshTokenException;
import com.github.lhervier.domino.oauth.client.ex.WrongPathException;
import com.github.lhervier.domino.oauth.client.model.GrantError;

@ControllerAdvice
public class ExceptionController {

	/**
	 * View names
	 */
	private static final String VIEW_GRANT_ERROR = "grantError";
	private static final String VIEW_AUTHORIZE_ERROR = "authorizeError";
	private static final String VIEW_ERROR = "error";
	
	/**
	 * Model attributes names
	 */
	private static final String MODEL_ATTR_ERROR = "error";
	
	/**
	 * Error while trying to acces the authorization server
	 */
	@ExceptionHandler(AuthorizeException.class)
	public ModelAndView handleAuthorizeException(AuthorizeException e) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MODEL_ATTR_ERROR, e.getError());
		return new ModelAndView(VIEW_AUTHORIZE_ERROR, model);
	}
	
	/**
	 * Error while exchanging the auth code with the access token
	 */
	@ExceptionHandler(GrantException.class)
	public ModelAndView handelGrantException(GrantException e) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MODEL_ATTR_ERROR, e.getError());
		return new ModelAndView(VIEW_GRANT_ERROR, model);
	}
	
	/**
	 * Refresh token error. Send the error as json.
	 */
	@ExceptionHandler(RefreshTokenException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody GrantError processRefreshTokenError(RefreshTokenException e) {
		return e.getError();
	}
	
	// =========================================================
	
	/**
	 * Handle wrong path exceptions sending a 404 error
	 */
	@ExceptionHandler(WrongPathException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handleWrongPathException(WrongPathException e) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MODEL_ATTR_ERROR, "not_found");
		return new ModelAndView(VIEW_ERROR, model);
	}
	
	/**
	 * Handle other exceptions
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView processServerErrorException(Exception e) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(MODEL_ATTR_ERROR, e.getMessage());
		return new ModelAndView(VIEW_ERROR, model);
	}

}
