package com.github.lhervier.domino.oauth.client.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class TokensResponse implements Serializable {
	private static final long serialVersionUID = -4845956195251059509L;
	
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("expires_in")
	private long expiresIn;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("id_token")
	private String idToken;
	private String scope;
	public String getAccessToken() {return accessToken;}
	public void setAccessToken(String accessToken) {this.accessToken = accessToken;}
	public String getIdToken() {return idToken;}
	public void setIdToken(String idToken) {this.idToken = idToken;}
	public String getTokenType() { return tokenType; }
	public void setTokenType(String tokenType) { this.tokenType = tokenType; }
	public String getScope() { return scope; }
	public void setScope(String scope) { this.scope = scope; }
	public long getExpiresIn() { return expiresIn; }
	public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
