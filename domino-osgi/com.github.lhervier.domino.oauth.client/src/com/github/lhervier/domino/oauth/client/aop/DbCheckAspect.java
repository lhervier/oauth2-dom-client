package com.github.lhervier.domino.oauth.client.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.lhervier.domino.oauth.client.Oauth2ClientProperties;
import com.github.lhervier.domino.oauth.client.ex.WrongPathException;

@Component
@Aspect
public class DbCheckAspect {

	/**
	 * The oauth properties
	 */
	@Autowired
	private Oauth2ClientProperties props;
	
	/**
	 * Pointcut to detect controller methods
	 */
	@Pointcut("within(com.github.lhervier.domino.oauth.client.controller.*)")
	private void controller() {
		// As a pointcut, this method is empty
	}
	
	/**
	 * Join point
	 * @param joinPoint the join point
	 * @throws WrongPathException if the context is incorrect
	 */
	@Before("controller()")
	public void checkDb(JoinPoint joinPoint) throws WrongPathException {
		// Only accept connections to a database from which we are able to extract a client_id
		if( StringUtils.isEmpty(this.props.getClientId()) )
			throw new WrongPathException();
	}
}
