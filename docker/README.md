# hbpmip/portal-backend

[![License](https://img.shields.io/badge/license-AGPL--3.0-blue.svg)](https://www.gnu.org/licenses/agpl-3.0.html) [![](https://images.microbadger.com/badges/version/hbpmip/portal-backend.svg)](https://hub.docker.com/r/hbpmip/portal-backend/tags/ "hbpmip/portal-backend image tags") [![](https://images.microbadger.com/badges/image/hbpmip/portal-backend.svg)](https://microbadger.com/#/images/hbpmip/portal-backend "hbpmip/portal-backend on microbadger")

## Docker image for the MIP portal backend.

To use this image, you need a running PostgreSQL database.
You need to configure a few things using some environment variables.

Environment variables:

* PORTAL_DB_URL: JDBC URL to connect to the portal database, for example "jdbc:postgresql://db:5432/portal".
* PORTAL_DB_SCHEMA: Database schema, default value is "public".
* PORTAL_DB_SERVER: optional, address of the portal database server, for example DB_SERVER="db:5432". Used to wait for the database to be up and running.
* PORTAL_DB_USER: User to use when connecting to the portal database.
* PORTAL_DB_PASSWORD: Password to use when connecting to the portal database.

* META_DB_URL: JDBC URL to connect to the metadata database.
* META_DB_SCHEMA: Database schema, default value is "public".
* META_DB_SERVER: optional, address of the metadata database server. Used to wait for the database to be up and running.
* META_DB_USER: User to use when connecting to the metadata database.
* META_DB_PASSWORD: Password to use when connecting to the metadata database.

* FEATURES_DB_URL: JDBC URL to connect to the features database.
* FEATURES_DB_SCHEMA: Database schema, default value is "public".
* FEATURES_DB_SERVER: optional, address of the features database server. Used to wait for the database to be up and running.
* FEATURES_DB_USER: User to use when connecting to the features database.
* FEATURES_DB_PASSWORD: Password to use when connecting to the features database.
* FEATURES_DB_MAIN_TABLE: Table that contains the scientific data to use, default value is "features".

* CONTEXT_PATH:  context path appended to all services running in this container. Default to "/services".

* AUTHENTICATION: 0 to disable authentication or 1 to enable authentication.
* CLIENT_ID: required when authentication is turned on, client ID for the [OpenID server of HBP](https://services.humanbrainproject.eu/oidc/).
* CLIENT_SECRET: required when authentication is turned on, client secret for the [OpenID server of HBP](https://services.humanbrainproject.eu/oidc/).
* TOKEN_URI: default to "https://services.humanbrainproject.eu/oidc/token".
* AUTH_URI: default to "https://services.humanbrainproject.eu/oidc/authorize".
* USER_INFO_URI: default to "https://services.humanbrainproject.eu/oidc/userinfo".

* FRONTEND_LOGIN_URL: URL to redirect to when login is required. Default to "http://frontend/services/login/hbp".
* FRONTEND_AFTER_LOGIN_URL: URL to redirect after login. Default to "http://frontend/home".
* FRONTEND_AFTER_LOGOUT_URL: URL to redirect to after logout. Default to "http://frontend/services/login/hbp".

* WOKEN_URL: URL to woken machine learning server.
* EXAREME_URL: URL to Exareme server.

* LOGGING_LEVEL_WEB: log level for the web layer of the application. Default to "DEBUG".
* LOGGING_LEVEL_HIBERNATE: log level for the Hibernate layer of the application. Default to "DEBUG".

* SESSION_TIMEOUT: Timeout in milliseconds for session expiration. Default to 2592000.