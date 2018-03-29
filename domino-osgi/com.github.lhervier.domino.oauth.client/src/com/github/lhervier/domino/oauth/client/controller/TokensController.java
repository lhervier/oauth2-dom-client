package com.github.lhervier.domino.oauth.client.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.lhervier.domino.oauth.client.Constants;
import com.github.lhervier.domino.oauth.client.model.GrantResponse;
import com.github.lhervier.domino.oauth.client.model.TokensResponse;

@Controller
public class TokensController {

	/**
	 * The http session
	 */
	@Autowired
	private HttpSession httpSession;
	
	/**
	 * Send the access token
	 */
	@RequestMapping(value = "/tokens", method = RequestMethod.GET)
	public @ResponseBody TokensResponse tokens() {
		TokensResponse resp = new TokensResponse();
		
		GrantResponse response = (GrantResponse) this.httpSession.getAttribute(Constants.SESSION_GRANT_RESPONSE);
		if( response != null ) {
			resp.setAccessToken(response.getAccessToken());
			resp.setExpiresIn(response.getExpiresIn());
			resp.setIdToken(response.getIdToken());
			resp.setTokenType(response.getTokenType());
			
			if( !StringUtils.isEmpty(response.getScope()) ) {
				resp.setScope(response.getScope());
			} else if( response.getScopes() != null && !response.getScopes().isEmpty() ) {
				resp.setScope(StringUtils.collectionToDelimitedString(response.getScopes(), " "));
			}
		}
		
		return resp;
	}
}
