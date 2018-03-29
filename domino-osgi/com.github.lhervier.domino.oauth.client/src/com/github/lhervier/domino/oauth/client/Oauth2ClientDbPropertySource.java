package com.github.lhervier.domino.oauth.client;

import lotus.domino.Database;
import lotus.domino.NotesException;

import com.github.lhervier.domino.spring.servlet.BaseParamViewPropertySource;

/**
 * This property source will extract spring properties from
 * the first document of a view named "Oauth2ClientParams" in any database.
 * @author Lionel HERVIER
 */
public class Oauth2ClientDbPropertySource extends BaseParamViewPropertySource {

	/**
	 * Constructor
	 */
	public Oauth2ClientDbPropertySource() {
		super("oauth2-client-property-source");
	}

	/**
	 * Return the name of the view that contains the parameters
	 */
	@Override
	protected String getViewName() {
		return "Oauth2ClientParams";
	}
	
	/**
	 * @see com.github.lhervier.domino.spring.servlet.BaseParamViewPropertySource#getPrefix()
	 */
	@Override
	protected String getPrefix() {
		return "oauth2.client.";
	}

	/**
	 * @see com.github.lhervier.domino.spring.servlet.BaseParamViewPropertySource#checkDb(lotus.domino.Database)
	 */
	@Override
	public boolean checkDb(Database database) throws NotesException {
		return true;
	}
}
