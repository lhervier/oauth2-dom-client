package com.github.lhervier.domino.oauth.client.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.lhervier.domino.oauth.client.ex.OauthClientException;

public class HttpConnection<T, E> {

	/**
	 * L'URL � appeler
	 */
	private String url;
	
	/**
	 * Le type de retour si OK
	 */
	private Class<T> okType;
	
	/**
	 * Le type de retour si KO
	 */
	private Class<E> errorType;
	
	/**
	 * La callback si c'est OK
	 */
	private Callback<T> okCallback;
	
	/**
	 * La callback si c'est KO
	 */
	private Callback<E> errorCallback;
	
	/**
	 * Les headers � ajouter
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * Un �ventuel hostname verifier (si connection https)
	 */
	private HostnameVerifier verifier = null;
	
	/**
	 * Un �ventuel SSLFactory (si connection https)
	 */
	private SSLSocketFactory factory = null;
	
	/**
	 * Le contenu � envoyer
	 */
	private InputStream content;
	
	/**
	 * Jackson mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Initialise une connection 
	 * @param <T> le type de retour si OK (200)
	 * @param <E> le type de retour si KO
	 * @param url l'url � appeler
	 * @param okType le type de retour si OK
	 * @param errorType le type de retour si KO
	 * @return 
	 */
	public static final <T, E> HttpConnection<T, E> createConnection(String url, Class<T> okType, Class<E> errorType) {
		HttpConnection<T, E> ret = new HttpConnection<T, E>();
		ret.url = url;
		ret.okType = okType;
		ret.errorType = errorType;
		return ret;
	}
	public static final <T, E> HttpConnection<T, E> createConnection(String url) {
		return HttpConnection.createConnection(url, null, null);
	}
	
	/**
	 * D�fini la callback quand on re�oit une r�ponse correct
	 * @param callback la callback
	 * @return
	 */
	public HttpConnection<T, E> onOk(Callback<T> callback) {
		this.okCallback = callback;
		return this;
	}
	
	/**
	 * D�fini la callback quand on re�oit une erreur
	 * @param callback la callback
	 * @return
	 */
	public HttpConnection<T, E> onError(Callback<E> callback) {
		this.errorCallback = callback;
		return this;
	}
	
	/**
	 * Ajoute un header
	 * @param name le nom du header
	 * @param value la valeur
	 * @return
	 */
	public HttpConnection<T, E> addHeader(String name, String value) {
		this.headers.put(name, value);
		return this;
	}
	
	/**
	 * Pour ajouter un verifier
	 * @param verifier le verifier
	 */
	public HttpConnection<T, E> withVerifier(HostnameVerifier verifier) {
		this.verifier = verifier;
		return this;
	}
	
	/**
	 * Pour ajouter un SSLFactory
	 * @param factory la factory
	 */
	public HttpConnection<T, E> withFactory(SSLSocketFactory factory) {
		this.factory = factory;
		return this;
	}
	
	/**
	 * Pour d�finir un contenu texte
	 * @param content le contenu
	 * @param encoding l'encodage � utiliser
	 * @throws UnsupportedEncodingException 
	 */
	public HttpConnection<T, E> setTextContent(String content, String encoding) {
		try {
			this.content = new ByteArrayInputStream(content.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new OauthClientException(e);
		}
		return this;
	}
	
	/**
	 * Pour d�finir un contenu objet � serialiser en Json
	 * @param content le contenu
	 * @param encoding l'encodage
	 */
	public HttpConnection<T, E> setJsonContent(Object content, String encoding) {
		try {
			this.content = new ByteArrayInputStream(this.mapper.writeValueAsString(content).getBytes(encoding));
		} catch (JsonGenerationException e) {
			throw new OauthClientException(e);
		} catch (JsonMappingException e) {
			throw new OauthClientException(e);
		} catch (UnsupportedEncodingException e) {
			throw new OauthClientException(e);
		} catch (IOException e) {
			throw new OauthClientException(e);
		}
		return this;
	}
	
	/**
	 * Pour d�finir un contenu
	 * @param content le contenu
	 */
	public HttpConnection<T, E> setContent(byte[] content) {
		this.content = new ByteArrayInputStream(content);
		return this;
	}
	
	/**
	 * Pour d�finir un contenu
	 * @param in stream vers le contenu
	 */
	public HttpConnection<T, E> setContent(InputStream in) {
		this.content = in;
		return this;
	}
	
	/**
	 * Emet la requ�te
	 * @throws IOException 
	 */
	public void execute() {
		InputStream in = null;
		Reader reader = null;
		HttpURLConnection conn = null;
		try {
			URL uUrl = new URL(this.url);
			
			conn = (HttpURLConnection) uUrl.openConnection();
			if( "https".equals(uUrl.getProtocol()) ) {
				HttpsURLConnection conns = (HttpsURLConnection) conn;
				if( this.verifier != null )
					conns.setHostnameVerifier(this.verifier);
				
				if( this.factory != null )
					conns.setSSLSocketFactory(this.factory);
			}
			
			// Input et output. Va �mettre un GET ou un POST
			conn.setDoInput(this.okCallback != null || this.errorCallback != null);
			conn.setDoOutput(this.content != null);
			
			// Ajoute les en t�tes http
			for( Entry<String, String> entry : this.headers.entrySet() )
				conn.addRequestProperty(entry.getKey(), entry.getValue());
			
			// Envoi l'objet
			if( this.content != null ) {
				OutputStream out = null;
				try {
					out = conn.getOutputStream();
					IOUtils.copy(this.content, out);
				} finally {
					IOUtils.closeQuietly(out);
					IOUtils.closeQuietly(this.content);
				}
			}
			
			// Charge la r�ponse (du JSON)
			StringBuilder sb = new StringBuilder();
			
			// Stream pour acc�der au contenu (en fonction d'une erreur)
			if( conn.getResponseCode() == 200 )
				in = conn.getInputStream();
			else
				in = conn.getErrorStream();
			
			// Lit le contenu de la r�ponse
			reader = new InputStreamReader(in, "UTF-8");
			char[] buff = new char[4 * 1024];
			int read = reader.read(buff);
			while( read != -1 ) {
				sb.append(buff, 0, read);
				read = reader.read(buff);
			}
			
			// Code 200 => OK
			if( conn.getResponseCode() == 200 && this.okCallback != null ) {
				T resp = this.mapper.readValue(sb.toString(), this.okType);
				this.okCallback.run(resp);
				
			// Code autre => Erreur
			} else if( conn.getResponseCode() != 200 && this.errorCallback != null ) {
				E resp = this.mapper.readValue(sb.toString(), this.errorType);
				this.errorCallback.run(resp);
			}
		} catch(Exception e) {
			throw new OauthClientException(e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(in);
			if( conn != null ) conn.disconnect();
		}
	}
}
