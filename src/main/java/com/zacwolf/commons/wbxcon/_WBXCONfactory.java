/* @(#)WBXCONexception.java - zac@zacwolf.com
 *
 * Object that represents a WebEx Connect org object
 * 
	Licensed under the MIT License (MIT)
	
	Copyright (c) 2014 Zac Morris
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */
package com.zacwolf.commons.wbxcon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;

import com.zacwolf.commons.wbxcon.exceptions.WBXCONexception;


/**
 * This is the "public" face for creating the methods for managing a WebEx Connect Managed Org
 *
 */
public class _WBXCONfactory {

final	public	String			domainname;
final			WBXCONorg		org;

	public _WBXCONfactory(final String domainname, final String wapiUSER, final String wapiPASS) throws WBXCONexception{
		this.domainname	=	domainname;
		this.org		=	new WBXCONorg(domainname, wapiUSER, wapiPASS);
	}

	public _WBXCONfactory(final String domainname, final String wapiAUTHURL, final String wapiUSER, final String wapiPASS) throws WBXCONexception{
		this.domainname	=	domainname;
		this.org		=	new WBXCONorg(domainname, wapiAUTHURL, wapiUSER, wapiPASS);
	}

	@Override
	public void finalize(){
		this.org.finalize();
	}
	
	/**
	 * Return the list of all CMCU clusters configured for the domain
	 * @return list of CMCU cluster names
	 * @throws WBXCONexception
	 */
	public Set<String> domainGetCMCUClusterSet() throws WBXCONexception{
		return this.org.restapiDomainPullCMCUClusterSet();
	}

	/**
	 * Query the WebEx Connect groupID for the given groupName
	 * 
	 * @param groupname
	 * @return	{@link WBXCONuser.WBXCONGROUPID} representation of the groupID
	 * @throws WBXCONexception
	 */
	public WBXCONuser.WBXCONGROUPID groupGetIDforName(String groupname) throws WBXCONexception{
		return this.org.restapiGroupGetIDForName(groupname);
	}
	
	/**
	 * Create a WBX User account based on given WBXaccount Object with a random password
	 * 
	 * @param account			a <code>WBXaccount</code> Object to be used to create a matching WBX User account.
	 * * @return WBXCONuser.WBXCONUID of the account that is created
	 * @throws WBXCONexception
	 */
	public WBXCONuser.WBXCONUID accountCreate(final WBXCONuser account) throws WBXCONexception{
		return accountCreate(account,this.org.passwordrule.getRandomPassword(),true);
	}

	/**
	 * Create a WBX User account based on given WBXaccount Object with a random password
	 * 
	 * @param account			a <code>WBXaccount</code> Object to be used to create a matching WBX User account.
	 * @param sendWelcomeEmail 	a <code>boolean</code> indicating whether to send the Welcome email to the User.
	 * @return WBXCONuser.WBXCONUID of the account that is created
	 * @throws WBXCONexception
	 */
	public WBXCONuser.WBXCONUID accountCreate(final WBXCONuser account, final boolean sendWelcomeEmail) throws WBXCONexception {
		return this.org.restapiAccountCreate(account,this.org.passwordrule.getRandomPassword(),sendWelcomeEmail);
	}

	/**
	 * Create a WBX User account based on given WBXaccount Object
	 * 
	 * @param account			a <code>WBXaccount</code> Object to be used to create a matching WBX User account.
	 * @param password			a String containing the password to be used as the default password for the account.
	 * 							if the password does not meet the rules for the org, then the account will not be 
	 * 							created an an error will be thrown.
	 * @return WBXCONuser.WBXCONUID of the account that is created
	 * @throws WBXCONexception
	 */
	public WBXCONuser.WBXCONUID accountCreate(final WBXCONuser account, final String password) throws WBXCONexception{
		return accountCreate(account,password,true);
	}

	/**
	 * Create a WBX User account based on given WBXaccount Object
	 * 
	 * @param account			a <code>WBXaccount</code> Object to be used to create a matching WBX User account.
	 * @param sendWelcomeEmail 	a <code>boolean</code> indicating whether to send the Welcome email to the User.
	 * @param password			a String containing the password to be used as the default password for the account.
	 * @return WBXCONuser.WBXCONUID of the account that is created
	 * @throws WBXCONexception
	 */
	public WBXCONuser.WBXCONUID accountCreate(final WBXCONuser account, String password, final boolean sendWelcomeEmail) throws WBXCONexception {
		return this.org.restapiAccountCreate(account,password,sendWelcomeEmail);
	}
	
	/**
	 * Retrieve a WBXCONuser object for the given userName
	 * 
	 * @param id
	 * @return WBXCONuser representation of the user account
	 * @throws WBXCONexception
	 */
	public WBXCONuser accountGet(String id) throws WBXCONexception{
		return this.org.restapiAccountGet(id);
	}

	/**
	 * Returns the raw Document(DOM) object for the given userName.
	 * @param id
	 * @return
	 * @throws WBXCONexception
	 */
	public Document accountGetAsRawDom(String id) throws WBXCONexception{
		return this.org.restapiAccountGetAsDom(id);
	}

	/**
	 * Returns an xml string representation of a user object 
	 * @param id
	 * @return
	 * @throws WBXCONexception
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public String accountGetAsPretttyPrint(String id) throws WBXCONexception, NullPointerException, IOException, TransformerException{
final	ByteArrayOutputStream	baos	=	new ByteArrayOutputStream();	
		WBXCONorg.documentPrettyPrint(this.org.restapiAccountGetAsDom(id), baos);
		return baos.toString("utf-8");
	}

	/**
	 * Set the CMCU cluster value for a given user 
	 * @param userName		The userName of the user
	 * @param clustername	The clustername to set
	 * @throws WBXCONexception
	 */
	public void accountSetCMCUClusterValue(final String userName, final String clustername) throws WBXCONexception{
		if (clustername==null || clustername.equalsIgnoreCase("null"))
			this.org.restapiAccountRemoveEXTchildNode(this.org.restapiAccountGet(userName).getWBXUID(),"WBX/CUCM");
		else
			this.org.restapiAccountModify(userName,"<WBX><CUCM><clusterName>"+clustername+"</clusterName></CUCM></WBX>");
	}

	/**
	 * Get the CMCU cluster value for a given user
	 * @param userName The userName (with or without domain) of the user to retrieve the value for
	 * @throws WBXCONexception
	 */
	public String accountGetCMCUClusterValue(final String userName) throws WBXCONexception{
		return this.org.restapiAccountGet(userName).getWBX().getCUCM().getClusterName();
	}
	
	/**
	 * Sets Active status of a WBXCON User object
	 * @param id		a <code>WBXaccount.WBXUID</code> object
	 * @param status	a boolean indicating the Active status to be set for the given WBX User object
	 * @throws WBXCONexception
	 */
	public void accountChangeStatus(final WBXCONuser.WBXCONUID id, final boolean status) throws WBXCONexception{
		this.org.restapiAccountModify(id,"<isActive>"+status+"</isActive>");
	}
	
	public void accountChangeStatus(final String userName, final boolean status) throws WBXCONexception{
		this.org.restapiAccountModify(userName,"<isActive>"+status+"</isActive>");
	}
	
	/**
	 * Sets Locked status of a WBXCON User object
	 * @param id		a <code>WBXaccount.WBXUID</code> object
	 * @param locked	a boolean indicating the Locked status to be set for the given WBX User object
	 * @throws WBXCONexception
	 */
	public void accountChangeLocked(final WBXCONuser.WBXCONUID id, final boolean locked) throws WBXCONexception{
		this.org.restapiAccountModify(id,"<user><isLocked>"+locked+"</isLocked></user>");
	}
	
	public void accountChangeLocked(final String userName, final boolean locked) throws WBXCONexception{
		this.org.restapiAccountModify(userName,"<user><isLocked>"+locked+"</isLocked></user>");
	}

	/**
	 * Add a user to a WBXCON Group specified by the groupName
	 * @param id		a <code>WBXaccount.WBXUID</code> object
	 * @param groupName	the groupName for the WBX Group object
	 * @throws WBXCONexception
	 */
	public void accountAddToGroup(final WBXCONuser.WBXCONUID id, final String groupName) throws WBXCONexception{
		this.org.restapiAccountAddToGroup(id,this.org.restapiGroupGetIDForName(groupName));
	}
	
	/**
	 * Add a user to a WBXCON Group specified by the groupName
	 * @param id		a <code>WBXaccount.WBXUID</code> object
	 * @param groupid	a <code>WBXaccount.WBXGROUPID</code> object
	 * @throws WBXCONexception
	 */
	public void accountAddToGroup(final WBXCONuser.WBXCONUID id, final WBXCONuser.WBXCONGROUPID groupid) throws WBXCONexception{
		this.org.restapiAccountAddToGroup(id,groupid);
	}

	/**
	 * Queries the set of WBXCON group objects that the given user belongs to.
	 * @param id			a <code>WBXaccount.WBXUID</code> object
	 * @return Set<String>	in the format: groupID|groupName
	 * @throws WBXCONexception
	 */
	public Set<String> accountGetGroups(final WBXCONuser.WBXCONUID id) throws WBXCONexception{
		return this.org.restapiAccountGetGroups(id);
	}

	/**
	 * Synchronizes the WBXCON User profile data with the <code>WBXCONuser</code> object
	 * @param account		a <code>WBXaccount</code> object
	 * @throws WBXCONexception
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 * @throws NullPointerException 
	 */
	public void accountSyncProfile(final WBXCONuser account,final boolean clearFirst) throws WBXCONexception, ParserConfigurationException, NullPointerException, TransformerException{
		if (clearFirst)
			this.org.restapiAccountRemoveEXTchildNode(account.getWBXUID(),"WBX/profile");
		
final	DocumentBuilderFactory	factory 	=	DocumentBuilderFactory.newInstance();
final	DocumentBuilder			parser 		=	factory.newDocumentBuilder();
final	Document				doc			=	parser.newDocument();
		this.org.restapiAccountModify(account.getWBXUID(),"<user><ext><WBX>"+WBXCONorg.documentToXMLstring(account.getWBX().getPROFILE().marshallXML(doc))+"</WBX></ext></user>");
	}
	
	/**
	 * Grants a special privilege to an account.
	 * 
	 * Special privileges include:
	 * 	WBX:RunOrgReport
	 * 	WBX:ManageDomain
	 * 	WBX:ManageUsers
	 * 	WBX:ManageRoles
	 * 
	 * 
	 * @param uid
	 * @param privilege
	 * @throws WBXCONexception
	 */
	public void accountAssertSpecialPrivilege(final WBXCONuser.WBXCONUID uid, final String privilege, final String op) throws WBXCONexception{
		if (privilege==null || !privilege.startsWith("WBX:"))
			throw new WBXCONexception("Not a valid Special Privilege.  Special Privileges start with \"WBX:\"");
		
		this.org.restapiAccountAssertSpecialPrivilege(uid,privilege,op);
	}
	
	public synchronized void getReport(final String reportType, final String reportXML, final OutputStream out) throws InterruptedException, WBXCONexception{
final	WBXCONorg.REPORTJOB	report	=	this.org.new REPORTJOB(reportType,reportXML);
							report.getReport(out);
	}
	
	/**
	 * Populates two sorted sets with email address for all the currently 
	 * activated and deactivated accounts for the domain
	 * 
	 * @param activated
	 * @param deactivated
	 * @throws WBXCONexception
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ClientProtocolException 
	 */
	public synchronized void populateAccountUserNameSets(final SortedSet<String> activated, final SortedSet<String> deactivated) throws WBXCONexception{
		try{
			this.org.new REPORTJOB("org_connect_user_realtime_report","<bucketSize>total</bucketSize>").populateActivatedDeactivedSets(activated, deactivated);
		}catch (Exception e){
			throw new WBXCONexception(e);
		}
	}
}
