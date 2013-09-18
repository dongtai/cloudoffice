package apps.transmanager.weboffice.constants.server;

import apps.transmanager.weboffice.constants.both.MainConstants;



public interface PropsConsts
{
	String COFING_FILE = "/conf/loginConfig.properties";
	String LOGIN_TYPE = "loginType";
	int SYSTEM_LOGIN = MainConstants.SYSTEM_LOGIN;
	int LDAP_LOGIN = MainConstants.LDAP_LOGIN;
	int SSO_LOGIN = MainConstants.SSO_LOGIN;
	String AUTO_LOGIN_CLASS = "auto.login.class";
	String SSO_LOGIN_ENABLE = "sso.login.enable";
	String SSO_LOGOUT_URL = "sso.logout.url";
	
	String DEFAULT_COMPANY = "public";
	String USER_NAME = "userName";
	String USER_MAIL = "email";
	String USER_PASS = "passW";
	String USER_DUTY = "duty";
	String USER_DEP = "department";
	String USER_REAL_NAME = "realName";
	String USER_COMPANY = "companyID";
	String LDAP_USER_MAPPING = "ldap.user.mappings";
	String LDAP_GROUP_MAPPING = "ldap.group.mappings";
	String LDAP_CONTEXT_FACTORY = "ldap.context.factory";
	String LDAP_PROVIDER_URL = "ldap.provider.url";
	String LDAP_SECURITY_PRINCIPAL = "ldap.security.principal";
	String LDAP_SECURITY_CREDENTIALS = "ldap.security.credentials";
	String LDAP_REFERRAL = "ldap.referral";
	String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";
	String LDAP_CONNECT_POOL_ENABLED = "ldap.connect.pool.enabled";
	String LDAP_CONNECT_POOL_MAXSIZE = "ldap.connect.pool.maxsize";
	String LDAP_CONNECT_POOL_TIMEOUT = "ldap.connect.pool.timeout";
	String LDAP_BASE_USER_DN = "ldap.base.user.dn";
	String LDAP_USER_GROUP_NAME = "ldap.user.group.name";
	String LDAP_AUTH_METHOD = "ldap.auth.method";
	String LDAP_AUTH_PASS_NAME = "ldap.auth.pass.name";
	String LDAP_AUTH_PASS_EN_ALG = "ldap.auth.password.encryption.algorithm";
	String LDAP_AUTH_SEARCH_SCOPE = "ldap.auth.search.scope";
	String LDAP_AUTH_SEARCH_USER_FILTER = "ldap.auth.search.user.filter";
	String LDAP_USER_FILTER = "@loginName@";
	
	String CAS_USER_MAPPING = "cas.user.mapping";
	String CAS_AUTH_ENABLED = "cas.auth.enabled";
	String CAS_SERVER_NAME = "cas.server.name";
	String CAS_SERVICE_URL = "cas.service.url";
	String CAS_LOGIN_URL = "cas.login.url";
	String CAS_VALIDATE_URL = "cas.validate.url";
	String CAS_WRAP_REQUEST = "cas.wrap.request";
	String CAS_LOGOUT_URL = "cas.logout.url";
	
	String OPENSSO_ENABLED = "open.auth.enabled";
	String OPENSSO_LOGIN_URL = "open.login.url";
	String OPENSSO_LOGOUT_URL = "open.logout.url";
	String OPENSSO_SERVICE = "open.service.url";
	String OPENSSO_USER_MAPPING = "open.user.mapping";
	String OPENSSO_SUBJECT_ID_KEY = "open.sso.subject.id";
	String OPENSSO_GET_ATTRIBUTES = "open.sso.get.attribute";
	String OPENSSO_GET_COOKIE_NAME_TOKEN = "open.sso.get.cookie.name.token";
	String OPENSSO_GET_COOKIE_NAMES_FORWARD = "open.sso.get.cookie.names.forward";
	String OPENSSO_VALIDATE_TOKEN = "open.sso.validate.token";
	
	String JINDIE_SSO_ENABLE = "jindie.sso.enabled";
	String JINDIE_SSO_LOGOUT_URL = "jindie.sso.logout.url";
	
}
