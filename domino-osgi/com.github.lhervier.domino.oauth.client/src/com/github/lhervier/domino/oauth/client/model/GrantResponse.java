package com.github.lhervier.domino.oauth.client.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * R�ponse pour les types de grant "authorization code"
 * @author Lionel HERVIER
 */
public class GrantResponse extends TokensResponse {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -1108128610582221150L;

	/**
	 * Le refresh token
	 */
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	/**
	 * Les scopes (s'il sont diff�rents de ceux demand�s par le client)
	 * Google Cloud les envoi en utilisant ce param�tre, m�me s'il n'est pas 
	 * r�f�renc� dans la RFC...
	 */
	@JsonIgnore
	private List<String> scopes;

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scopes the scopes to set
	 */
	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}
}
