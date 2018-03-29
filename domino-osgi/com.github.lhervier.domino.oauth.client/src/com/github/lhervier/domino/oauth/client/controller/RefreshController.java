package com.github.lhervier.domino.oauth.client.controller;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.lhervier.domino.oauth.client.Constants;
import com.github.lhervier.domino.oauth.client.ex.RefreshTokenException;
import com.github.lhervier.domino.oauth.client.model.GrantError;
import com.github.lhervier.domino.oauth.client.model.GrantResponse;
import com.github.lhervier.domino.oauth.client.model.TokensResponse;
import com.github.lhervier.domino.oauth.client.service.TokenService;
import com.github.lhervier.domino.oauth.client.utils.Callback;
import com.github.lhervier.domino.oauth.client.utils.ValueHolder;

@Controller
public class RefreshController {

	/**
	 * The http session
	 */
	@Autowired
	private HttpSession httpSession;
	
	/**
	 * The token controller
	 */
	@Autowired
	private TokensController tokenCtrl;
	
	/**
	 * The token service
	 */
	@Autowired
	private TokenService tokenSvc;
	
	/**
	 * Refresh the token
	 */
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public @ResponseBody TokensResponse refreshToken() throws RefreshTokenException {
		GrantResponse resp = (GrantResponse) this.httpSession.getAttribute(Constants.SESSION_GRANT_RESPONSE);
		String refreshToken = resp.getRefreshToken();
		
		// No refresh token => Unable to ask for a new one
		if( refreshToken == null ) {
			this.httpSession.removeAttribute(Constants.SESSION_GRANT_RESPONSE);
			return this.tokenCtrl.tokens();
		}
		
		final ValueHolder<RefreshTokenException> ex = new ValueHolder<RefreshTokenException>();
		this.tokenSvc.createTokenConnection(
				"grant_type=refresh_token&" +
				"refresh_token=" + refreshToken
		)
		
		// OK => Met � jour la session et retourne le token
		.onOk(new Callback<GrantResponse>() {
			@Override
			public void run(GrantResponse grant) throws IOException, ParseException {
				RefreshController.this.httpSession.setAttribute(Constants.SESSION_GRANT_RESPONSE, grant);
			}
		})
		
		// Erreur => Non autoris�
		.onError(new Callback<GrantError>() {
			@Override
			public void run(GrantError error) throws IOException {
				RefreshController.this.httpSession.setAttribute(Constants.SESSION_GRANT_RESPONSE, null);
				ex.set(new RefreshTokenException(error));
			}
		})
		.execute();
		
		if( ex.get() != null )
			throw ex.get();
		
		return this.tokenCtrl.tokens();
	}
}
