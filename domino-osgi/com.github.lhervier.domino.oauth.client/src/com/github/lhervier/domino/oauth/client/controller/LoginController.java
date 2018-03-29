package com.github.lhervier.domino.oauth.client.controller;

import static com.github.lhervier.domino.oauth.client.utils.Utils.urlEncode;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.github.lhervier.domino.oauth.client.Constants;
import com.github.lhervier.domino.oauth.client.Oauth2ClientProperties;
import com.github.lhervier.domino.oauth.client.ex.AuthorizeException;
import com.github.lhervier.domino.oauth.client.ex.GrantException;
import com.github.lhervier.domino.oauth.client.model.AuthorizeError;
import com.github.lhervier.domino.oauth.client.model.GrantError;
import com.github.lhervier.domino.oauth.client.model.GrantResponse;
import com.github.lhervier.domino.oauth.client.service.TokenService;
import com.github.lhervier.domino.oauth.client.utils.Callback;
import com.github.lhervier.domino.oauth.client.utils.QueryStringUtils;
import com.github.lhervier.domino.oauth.client.utils.Utils;
import com.github.lhervier.domino.oauth.client.utils.ValueHolder;

import lotus.domino.NotesException;

@Controller
public class LoginController {

	/**
	 * The http servlet request
	 */
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * The http session
	 */
	@Autowired
	private HttpSession session;
	
	/**
	 * Spring environment
	 */
	@Autowired
	private Oauth2ClientProperties props;
	
	/**
	 * The token service
	 */
	@Autowired
	private TokenService tokenSvc;
	
	/**
	 * Initialization
	 */
	@RequestMapping(value = "/login")
	public ModelAndView init(
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "redirect_url", required = false) String redirectUrl) throws AuthorizeException, GrantException {
		// If we have an authorization code, we can process it
		if( !StringUtils.isEmpty(code) )
			return this.processAuthorizationCode(code);		// state stores the redirect url
		
		// Throw exception if error in parameters
		if( !StringUtils.isEmpty(error) ) {
			AuthorizeError authError = QueryStringUtils.createBean(
					this.request,
					AuthorizeError.class
			);
			throw new AuthorizeException(authError);
		}
		
		// Otherwise, we redirect to the authorize endpoint
		this.session.setAttribute(Constants.SESSION_REDIRECT_URL, redirectUrl);
		String authorizeEndPoint = this.props.getAuthorizeUrl();
		String redirectUri = this.props.getRedirectUri();
		String clientId = this.props.getClientId();
		StringBuilder fullRedirectUri = new StringBuilder(authorizeEndPoint)
					.append("?response_type=").append(urlEncode(this.props.getResponseType()))
					.append("&redirect_uri=").append(urlEncode(redirectUri))
					.append("&client_id=").append(urlEncode(clientId))
					.append("&scope=").append(urlEncode(this.props.getScope()));
		if( !StringUtils.isEmpty(state) )
			fullRedirectUri.append("&state=").append(urlEncode(state));
		fullRedirectUri.append("&nonce=").append(urlEncode(this.session.getId()));
		if( !StringUtils.isEmpty(this.props.getAuthorizeAccessType()) )
			fullRedirectUri.append("&access_type=").append(urlEncode(this.props.getAuthorizeAccessType()));
		if( !StringUtils.isEmpty(this.props.getAuthorizePrompt()) )
			fullRedirectUri.append("&prompt=").append(urlEncode(this.props.getAuthorizePrompt()));
		
		return new ModelAndView("redirect:" + fullRedirectUri);
	}
	
	/**
	 * Process authorization code, and fill session with tokens.
	 * @param code le code autorisation
	 * @throws IOException 
	 * @throws NotesException 
	 */
	private ModelAndView processAuthorizationCode(final String code) throws GrantException {
		// The response object
		final ValueHolder<GrantError> errHolder = new ValueHolder<GrantError>();
		
		// Create the connection
		this.tokenSvc.createTokenConnection(
				"grant_type=authorization_code" +
				"&code=" + urlEncode(code) +
				"&redirect_uri=" + urlEncode(this.props.getRedirectUri())
		)
		
		// OK => Mémorise les tokens en session et redirige vers l'url initiale
		.onOk(new Callback<GrantResponse>() {
			@Override
			public void run(GrantResponse grant) throws Exception {
				if( !"Bearer".equalsIgnoreCase(grant.getTokenType()) ) {
					GrantError err = new GrantError();
					err.setError("unsupported_token_type");
					err.setErrorDescription("Token type '" + grant.getTokenType() + "' is not supported");
					errHolder.set(err);
					return;
				}
				
				// If we have an id_token in the response, we must check the nonce value
				if( !Utils.checkIdTokenNonce(grant.getIdToken(), LoginController.this.session.getId()) ) {
					GrantError error = new GrantError();
					error.setError("invalid_nonce_in_id_token");
					error.setErrorDescription("The nonce value in the id token is invalid");
					errHolder.set(error);
					return;
				} 
				
				// Update session
				session.setAttribute(Constants.SESSION_GRANT_RESPONSE, grant);
			}
		})
		
		// KO => Affiche l'erreur dans la XPage
		.onError(new Callback<GrantError>() {
			@Override
			public void run(GrantError error) throws IOException {
				errHolder.set(error);
			}
		})
		
		// Send the request
		.execute();
		
		// Propagate error
		if( errHolder.get() != null )
			throw new GrantException(errHolder.get());
		
		// Send redirect to the initial page
		return new ModelAndView("redirect:" + session.getAttribute(Constants.SESSION_REDIRECT_URL));
	}
}
