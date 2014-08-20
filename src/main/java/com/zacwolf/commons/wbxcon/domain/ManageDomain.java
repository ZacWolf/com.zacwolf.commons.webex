package com.zacwolf.commons.wbxcon.domain;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.zacwolf.commons.wbxcon._WBXCONfactory;
import com.zacwolf.commons.wbxcon.exceptions.WBXCONexception;


public abstract class ManageDomain {

final	public		static		String			KEY_level		=	"level";
final	public		static		String			KEY_wapiUSER	=	"wapiUSER";
final	public		static		String			KEY_wapiPASS	=	"wapiPASS";
final	public		static		String			KEY_wapiAUTHURL	=	"wapiAUTHURL";
final	public		static		String			KEY_ldapURL		=	"ldapURL";
final	public		static		String			KEY_ldapUSER	=	"ldapUSER";
final	public		static		String			KEY_ldapPASS	=	"ldapPASS";

final	public		transient	String					DOMAIN;
final	protected	transient	Logger					LOGGER;
final	protected	transient	_WBXCONfactory			org;
final	protected	transient	LDAPserverConnection	ldap;


	public ManageDomain(final Level level, 
						final String domain, 
						final String wapiUSER, 
						final String wapiPASS,
						final String ldapURL,
						final String ldapUSER,
						final String ldapPASS
					) throws WBXCONexception{
		this(level,domain,null,wapiUSER,wapiPASS,ldapURL,ldapUSER,ldapPASS);
	}

	public ManageDomain(final Level level, 
						final String domain, 
						final String wapiAUTHURL, 
						final String wapiUSER, 
						final String wapiPASS,
						final String ldapURL,
						final String ldapUSER,
						final String ldapPASS
					) throws WBXCONexception{
					this.DOMAIN			=	domain;
					this.LOGGER			=	Logger.getLogger(DOMAIN);
					this.LOGGER.setLevel(level);
					this.LOGGER.setUseParentHandlers(false);
final	Handler		defaultHandler		=	new ConsoleHandler();
final	Formatter	defaultFormatter	=	new DomainLoggingFormatter();
					defaultHandler.setFormatter(defaultFormatter);
					defaultHandler.setLevel(level);
					this.LOGGER.addHandler(defaultHandler);
					this.org			=	new _WBXCONfactory(DOMAIN,wapiAUTHURL, wapiUSER, wapiPASS);
		if (ldapURL!=null)
					this.ldap			=	new LDAPserverConnection(ldapURL,ldapUSER,ldapPASS);
		else		this.ldap			=	null;
	}

	public abstract void manageUsers() throws WBXCONexception;
	
	/**
	 * @param url
	 * @param user
	 * @param pass
	 * @return
	 * @throws SQLException
	 * @throws NotInitializedException
	 */
	protected static final Connection getDBconnection(final String url, final String user, final String password) throws SQLException{
		return getDBconnection(url,user,password,0);
	}

	/**
	 * @param url
	 * @param user
	 * @param pass
	 * @param retry
	 * @return
	 * @throws SQLException
	 * @throws NotInitializedException
	 */
	private static final Connection getDBconnection(final String url, final String user, final String password,int retry) throws SQLException{
		try{
			return DriverManager.getConnection(url,user,password);
		} catch (SQLException sql){
			if (sql.getErrorCode()==12154 && retry<10){
				try {
					Thread.sleep(10000);
					getDBconnection(url,user,password,++retry);
				} catch (InterruptedException e) {}
			} else 
				throw sql;
		}
		return null;
	}

	/**
	 * Convenience class for connecting to an LDAP server, and doing a search
	 *
	 */
	public class LDAPserverConnection{
	
final	private	Hashtable<String,String>	environment		=	new Hashtable<String,String>();
	
		/**
		 * Convenience class for connecting to an LDAP server, and doing a search
		 *
		 */
		public LDAPserverConnection(final String ldapProviderURL,
									final String ldapPrincipal,
									final String ldapCredentials
								   )
		{
					this.environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
					this.environment.put(Context.PROVIDER_URL, ldapProviderURL);
					this.environment.put(Context.SECURITY_PRINCIPAL, ldapPrincipal);
					this.environment.put(Context.SECURITY_CREDENTIALS, ldapCredentials);
					this.environment.put(Context.REFERRAL, "follow");
		}
	
		/**
		 * @return an LDAP Directory server connection context
		 * @throws NamingException
		 */
		public InitialDirContext getNewContext() throws NamingException{
			try {	return new InitialDirContext(this.environment);
			} catch (NamingException e) {// let's try localhost before we error out
final	Hashtable<String,String>	tempenv	=	new Hashtable<String,String>(this.environment);
									tempenv.put(Context.PROVIDER_URL, "ldap://localhost:389");
					return new InitialDirContext(tempenv);
			}
		}
	
		
		/**
		 * Perform an LDAP search, using a new InitialDirContext that is closed after the search.
		 * The default max number of matches returned is 1000
		 * 
		 * @param searchContext	The fully qualified base to start the search
		 * 						Usually in the form <code>CN=,OU=,OU=,DC=,DC=</code>
		 * @param filter		@see <a href="http://docs.oracle.com/cd/E19528-01/819-0997/gdxpd/index.html">Examples</a>
		 * @return				Fully loaded list of search result objects.
		 * @throws NamingException
		 */
		public List<SearchResult> search(final String searchContext, 
													  final String filter
													 ) throws NamingException
		{
			return search(searchContext,filter,1000);
		}

		/**
		 * Perform an LDAP search, using a new InitialDirContext that is closed after the search.
		 * 
		 * @param searchContext	The fully qualified base to start the search
		 * 						Usually in the form <code>CN=,OU=,OU=,DC=,DC=</code>
		 * @param filter		@see <a href="http://docs.oracle.com/cd/E19528-01/819-0997/gdxpd/index.html">Examples</a>
		 * @param countLimit	The max number of matching records to return
		 * @return				Fully loaded list of search result objects.
		 * @throws NamingException
		 */
		public List<SearchResult> search(final String searchContext, 
													  final String filter, 
													  final long countLimit
													 ) throws NamingException
		{
final	List<SearchResult>	search	=	new ArrayList<SearchResult>();
final	InitialDirContext	context	=	getNewContext();
			try{
final	NamingEnumeration<SearchResult>	results	=	search(context,searchContext,filter,1000);
				while(results.hasMore())
					search.add(results.next());
				
			} finally {
				context.close();
			}
			return search;
		}
	
		/**
		 * Perform an LDAP search, utilizing a User provided InitalDirContext object.
		 * The InitialDirContext is not closed after the search, allowing it
		 * to be reused for multiple searches.
		 * 
		 * The default max number of matches returned is 1000
		 * 
		 * @param context		{@link InitalDirContext} 
		 * @param searchContext	The fully qualified base to start the search
		 * 						Usually in the form <code>CN=,OU=,OU=,DC=,DC=</code>
		 * @param filter		@see <a href="http://docs.oracle.com/cd/E19528-01/819-0997/gdxpd/index.html">Examples</a>
		 * @return				An enumeration that doesn't pre-load all results into memory.
		 * @throws NamingException
		 */
		public NamingEnumeration<SearchResult> search(final InitialDirContext context,
													  final String searchContext,
													  final String filter
													 ) throws NamingException
		{
			return context.search(searchContext, filter, new SearchControls(SearchControls.SUBTREE_SCOPE, 1000, (5*60*1000),  null, true, true));
		}
	
		/**
		 * Perform an LDAP search, utilizing a User provided InitalDirContext object.
		 * The InitialDirContext is not closed after the search, allowing it
		 * to be reused for multiple searches.
		 * 
		 * @param context		{@link InitalDirContext} 
		 * @param searchContext	The fully qualified search text. Usually in the form 
		 * 						<code>CN=,OU=,OU=,DC=,DC=</code>
		 * @param filter		@see <a href="http://docs.oracle.com/cd/E19528-01/819-0997/gdxpd/index.html">Examples</a>
		 * @param countLimit	The max number of matching records to return
		 * @return				An enumeration that doesn't pre-load all results into memory.
		 * @throws NamingException
		 */
		public NamingEnumeration<SearchResult> search(final InitialDirContext context,
													  final String searchContext, 
													  final String filter, 
													  final long countLimit
													 ) throws NamingException
		{
			return context.search(searchContext, filter, new SearchControls(SearchControls.SUBTREE_SCOPE, countLimit, (5*60*1000),  null, true, true));
		}
	}

	private class DomainLoggingFormatter extends Formatter {
final	private		ThreadLocal<DateFormat>	dateFormat	=	new ThreadLocal<DateFormat>() {
																@Override
																protected DateFormat initialValue() {
																	return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
																}
															};
		@Override
		final public String format(LogRecord record) {
final		StringBuilder	logEntry	=	new StringBuilder();
							logEntry.append(DOMAIN);
							logEntry.append(" ");
							logEntry.append(dateFormat.get().format(new Date(record.getMillis())));
							logEntry.append(" ");
							logEntry.append(record.getLevel().getName());
							logEntry.append(" ");
							logEntry.append(record.getMessage());
							logEntry.append(System.getProperty("line.separator"));
			return logEntry.toString();
		}
	}
}
