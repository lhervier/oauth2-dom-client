This is an implementation for IBM Domino of the endpoints needed by the AngularJS [ngOauth2AuthCodeFlow](https://github.com/lhervier/oauth2-ng-auth-code-flow) module.
The endpoints are implemented as standard Domino Spring controllers, thanks to the [domino-spring](https://github.com/lhervier/dom-spring) project.

Once everything is installed and configure, the endpoints (needed to initialize ngOauth2AuthCodeFlow) will be the followings :

- "/init" endpoint : http://youserver/anydb.nsf/oauth2-client/login
- "/tokens" endpoint : http://youserver/anydb.nsf/oauth2-client/tokens
- "/refresh" endpoint : http://youserver/anydb.nsf/oauth2-client/refresh

The installation require multiple steps :

- Install the dom-spring plugins on your server.
- Install this plugin on your server.

To make the endpoints available on a given Notes database, you will have to create a View named "Oauth2ClientParams" that contains a single document (it can contain multiple 
documents, but only the first one will be used).

This document must have a set of fields whose name correspond to the needed properties :

- oauth2.client.endpoints.authorize.url = *URL of your OAuth2 Authorization Server /authorize endpoint*
- oauth2.client.endpoints.authorize.accessType = *When using Google Clous OAUTH2, set this value to 'offline' if you want a refresh token*
- oauth2.client.endpoints.token.url = *URL of your OAuth2 Authorization Server /token endpoint*
- oauth2.client.endpoints.token.authMode = *One of "basic"/"queryString"/"none". This is the way the secret will be passed to the token endpoint.*
	- "basic" : It will besent to the "Authorization Basic" header. The client_id will NOT be passed in the query string.
	- "queryString" : It will be passed along the "client_id" in the query string, in the "client_secret" parameter.
	- "none" : Only the "client_id" will be passed in the query string.
- oauth2.client.responseType = *The OAUTH2 authorize response type. Must be compatible with the authorization code flow (or openid hybrid flow). In doubt, set it to "code+id_token".*
- oauth2.client.scope = *The OAUTH2 scope value. You can leave it empty, or set it to "openid" if you want to extract an id token.*
- oauth2.client.clientId = *Your oauth2 client application id*
- oauth2.client.secret = *Your oauth2 client application secret*
- oauth2.client.redirectURI = *URL used by the users to access your application. Must be coherent with what's configured in your OAUTH2 application.*



# Installing the domino-spring osgi plugins

Refer to the README of this project for more information.



# Install this project's plugin

## Installation using precompiled update site

The update site is (or will be made) available on the github release page.

Once downloaded, the procedure is the same as installing the dom-spring plugins. 

## Generating the update site yourself

This project is using dom-spring. Refer to the README of this project for more information.



# Create a client database

Such databases are not using XPages, or good old "Forms and Views" to generate the user interface. Here, we are talking about databases that contains :

- html/css/js files, preferably stored in the WebContent folder accessible from the package explorer.
- The client AngularJS code will then use normal $http and $resource to access Rest services exposed into a OAuth2 Resource Server

[oauth2-dom-res-server](https://github.com/lhervier/oauth2-dom-res-server) gives you a mean to implement such services on Domino.

Spring also affer you an easy way to implement such services in regular java webapps. You can find an [example here](https://github.com/lhervier/oauth2-spring-res-server)

Have a look at the sample database present in this repository's code for more details.

## ACL warning

With OAuth2, the browser will load the web interface, detect that it does not have an access token, and then redirect to the authorize endpoint of your authorization 
server (ADFS, or Google Cloud or Domino Authorization Server for example). 
The user will log on this server, after which the authorization server will redirect back again the browser to the original page.

In conclusion : If your database is protected by an ACL, the user will have to authenticate twice :

- The first time when accessing the database : Because of Domino ACL, you will have the Domino login screen.
- And again when opening a session on the OAUTH2 authorization server (Your Microsoft ADFS for example) : You will have the ADFS login screen.

For this reason, it is necessary to set Anonymous to reader in the ACL of the database that host the front end code. 
Then, write Domino hosted Rest Services using [oauth2-dom-res-server](https://github.com/lhervier/oauth2-dom-res-server), and
protect them with the access token.

## The configuration document :

Your database MUST have a view named "Oauth2ClientParams". And this view MUST contain at least one document (only the first one will be used). 
If this is not the case, the endpoints will not be made available at your database context (you will have a 404 error).

The list of fields present in the document must correspond to the awaited Spring properties (as described in the introduction chapter).

For security reason, it is recommanded to protect the configuration document with a Reader field. The only persons who will access this document are :

- The administrator that will create the document
- And the server itself (the osgi code that will read the values will use a notes session opened as the server itself).

## Other design elements

Using the package explorer, you can create a set of HTML/JS/CSS files in the WebContent folder. 
Don't forget to include the [NgOauth2AuthorizationCodeFlow](https://github.com/lhervier/oauth2-ng-auth-code-flow) module. 

## The sample database

Once the plugins are deployed on your Domino Server, the database created from the ondisk project, and the configuration document filled with the right values,
you can open the "index.html" page stored at the root :

	http://domino/db.nsf/index.html

The code of the html page is stored in the WebContent folder, accessible using Domino Designer and the package explorer view.

When opening the page, you should be redirected to the authentication page of your authorization server. And when redirected back to the sample application,
it should display the access token (only for information purpose).

You can then enter the url of a rest service protected by this access token, and call this API.

For testing purpose, if using Google Cloud, you can use the userinfo end point :

	https://www.googleapis.com/oauth2/v1/userinfo?alt=json
