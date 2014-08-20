/* @(#)WBXCONuser.java - zac@zacwolf.com
 *
 * Object that represents a WebexConnect user object.
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

/**
 * @author zmorris
 * 
 * This acts as the Java Object representation of a WBX User record, aka Account.
 *
 */
public class WBXCONuser implements Serializable, Comparable<WBXCONuser> {
final	private	static	long 			serialVersionUID 	=	-2598622323573829785L;
final	private	static	String			SPECIAL_CHAR_REMOVE	=	"[!%#$^&*\"';{}|<>]";
final	private	static	String			EMPTY_STRING		=	"";	

final	public			String			id;
final	public			String			domain;
final	public			String			userName;
final	public			String			email;
final	public			String			displayName;

final	private			EXT				ext					=	new EXT();
		private			String			isActive			=	null;
		private			String			isLocked			=	null;
		private			WBXCONUID		wbxconuid			=	null;

	/**
	 * Used to create a new user object when unmarshalling
	 * @param id
	 * @param domain
	 * @param displayname
	 * @param isactive
	 * @param islocked
	 */
	private WBXCONuser(final String id, final String domain, final String displayname, boolean isactive, boolean islocked){
		this.id				=	id;
		this.domain			=	domain;
		this.userName		=	id+"@"+domain;
		this.email			=	this.userName;
		this.displayName	=	displayname;
		this.isActive		=	isactive?"true":"false";
		this.isLocked		=	islocked?"true":"false";
	}

	/**
	 * This is the minimum amount of information necessary to create a new WexExConnect account.
	 * 
	 * @param id
	 * @param domain
	 * @param displayname
	 * @param firstName
	 * @param lastName
	 */
	public WBXCONuser(final String id, final String domain, final String displayname, String firstname, String lastname){
		this.id				=	id;
		this.domain			=	domain;
		this.userName		=	id+"@"+domain;
		this.email			=	this.userName;
		this.displayName	=	displayname;
		this.setWBX(firstname, lastname);
	}

	public boolean isProvisioned(){
		return wbxconuid!=null;
	}

	public boolean isActive(){
		return this.isActive.equals("true");
	}

	public boolean isLocked(){
		return this.isLocked.equals("true");
	}

	public void setWBXUID(String wbxuid){
		this.wbxconuid		=	new WBXCONUID(wbxuid.toUpperCase());
	}

	public WBXCONUID getWBXUID(){
		return this.wbxconuid;
	}

	public void setWBXUID(WBXCONuser.WBXCONUID wbxuid){
		this.wbxconuid		=	wbxuid;
	}

	public EXT.WBX setWBX(String firstname, String lastname){
		return this.ext.setWBX(firstname, lastname);
	}

	public EXT.WBX getWBX() throws NullPointerException{
		return this.ext.wbx;
	}

	@Override
	public int compareTo(WBXCONuser o) {
		if (o.getWBX().getLastName().equalsIgnoreCase(this.getWBX().getLastName())){
			if (o.getWBX().getFirstName().equalsIgnoreCase(this.getWBX().getFirstName()))
				return o.id.compareTo(this.id);
			return o.getWBX().getFirstName().compareTo(this.getWBX().getFirstName());
		}	
		return o.getWBX().getLastName().compareTo(this.getWBX().getLastName());
	}

	public Document marshallXML() throws NullPointerException, ParserConfigurationException{
final	DocumentBuilderFactory	factory 		=	DocumentBuilderFactory.newInstance();
final	DocumentBuilder			parser 			=	factory.newDocumentBuilder();
final	Document				doc				=	parser.newDocument();
final	Element					user			=	doc.createElement("user");
		
		WBXCONorg.documentSetTextContentOfNode("userName",this.userName,user);
		WBXCONorg.documentSetTextContentOfNode("email",this.email,user);
		
		if (this.isActive!=null)
			WBXCONorg.documentSetTextContentOfNode("isActive",this.isActive,user);
							
		if (this.isLocked!=null)
			WBXCONorg.documentSetTextContentOfNode("isLocked",this.isLocked,user);
		
		user.appendChild(this.ext.marshalXML(doc));
		doc.appendChild(user);
		return doc;
	}

	public static WBXCONuser unmarshallXML(Document dom){
final	String				email		=	WBXCONorg.documentGetTextContentByTagName(dom,"email");
final	String				id			=	email.substring(0,email.indexOf("@"));
final	String				domain		=	email.substring(email.indexOf("@")+1);
final	String				displayName	=	WBXCONorg.documentGetTextContentByTagName(dom,"displayName");
final	String				isActive	=	WBXCONorg.documentGetTextContentByTagName(dom,"isActive");
final	String				isLocked	=	WBXCONorg.documentGetTextContentByTagName(dom,"isLocked");
final	WBXCONuser			account		=	new WBXCONuser(id,domain, displayName, isActive!=null && isActive.equalsIgnoreCase("true"),isLocked!=null && isLocked.equalsIgnoreCase("true"));
							account.setWBXUID(WBXCONorg.documentGetTextContentByTagName(dom,"userID"));
							account.setWBX(	WBXCONorg.documentGetTextContentByTagName(dom,"firstName"),
											WBXCONorg.documentGetTextContentByTagName(dom,"lastName")
										  )
									.setCUCM(WBXCONorg.documentGetTextContentByTagName(dom,"clusterName"))
									.setPROFILE(WBXCONorg.documentGetTextContentByTagName(dom,"businessUnit"),
												WBXCONorg.documentGetTextContentByTagName(dom,"jobTitle"),
												WBXCONorg.documentGetTextContentByTagName(dom,"websiteURL"),
												WBXCONorg.documentGetTextContentByTagName(dom,"companyName")
											  );
final	NodeList			addrs		=	dom.getElementsByTagName("address");
		if(addrs != null){
			for(int a=0; a<addrs.getLength(); a++){
final	Node				_addr		=	addrs.item(a);
				if (_addr.hasAttributes()){
		String				type		=	null;
		String				street1		=	null;
		String				street2		=	null;
		String				city		=	null;
		String				state		=	null;
		String				zipcode		=	null;
		String				country		=	null;
final	NodeList			children	=	_addr.getChildNodes();
					if(children != null){
						for(int aa=0;aa<children.getLength();aa++){
							if(children.item(aa).getNodeName() != null){
								if (children.item(aa).getNodeName().equalsIgnoreCase("addressType")){
									type		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("streetLine1")){
									street1		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("streetLine2")){
									street2		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("city")){
									city		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("state")){
									state		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("zipcode")){
									zipcode		=	children.item(aa).getTextContent();
								} else if (children.item(aa).getNodeName().equalsIgnoreCase("country")){
									country		=	children.item(aa).getTextContent();
								}
							}
						}
					}
					account.getWBX().getPROFILE().addAddress(type, street1, street2, city, state, zipcode, country);
				}
			}
		}
		//WBXCONorg.documentPrettyPrint(dom, System.out);
final	NodeList			phones		=	dom.getElementsByTagName("phoneNumber");
		if(phones !=null){
			for(int p=0; p<phones.getLength(); p++){
final	Node				_phone			=	phones.item(p);
				if (_phone.hasAttributes()){
	String				type		=	null;
	String				number		=	null;
	String				displaynum	=	null;
//	String				country		=	null;
final	NodeList			children	=	_phone.getChildNodes();
					if(children != null){
						for(int pp=0;pp<children.getLength();pp++){
							if(children.item(pp).getNodeName()!= null){
								if (children.item(pp).getNodeName().equalsIgnoreCase("phoneType"))
									type		=	children.item(pp).getTextContent();
								else if (children.item(pp).getNodeName().equalsIgnoreCase("number"))
									number		=	children.item(pp).getTextContent();
								else if (children.item(pp).getNodeName().equalsIgnoreCase("displayNumber"))
									displaynum	=	children.item(pp).getTextContent();
//								else if (children.item(pp).getNodeName().equalsIgnoreCase("countryCode"))
//									country		=	children.item(pp).getTextContent();
							}
						}
					}
final	PhoneNumber			phone		=	new PhoneNumber(number, null);//CountryCode is not the same code used by libphonenumber
							phone.setDisplayName(displaynum);
							account.getWBX().getPROFILE().addPhone(type, phone);
				}
			}
		}

/* emails managed in the profile are now depreciated

final	NodeList			emailNumbers	=	dom.getElementsByTagName("emailNumber");
		if(emailNumbers != null){
			for(int p=0; p<emailNumbers.getLength(); p++){
final	Node				_email			=	emailNumbers.item(p);
				if (_email.hasAttributes()){
	String				type				=	null;
	String				emailadd			=	null;
final	NodeList			children		=	_email.getChildNodes();						
					for(int pp=0;pp<children.getLength();pp++){
						if (children.item(pp).getNodeName() != null && children.item(pp).getNodeName().equalsIgnoreCase("emailType"))
							type			=	children.item(pp).getTextContent();
						else if (children.item(pp).getNodeName() != null && children.item(pp).getNodeName().equalsIgnoreCase("email"))
							emailadd		=	children.item(pp).getTextContent();
					}
					account.getWBX().getPROFILE().addEmail(type, emailadd);
				}
			}
		}	
*/
		return account;
	}

	public String getJSON(){
		String				phone	=	"";
		String				mobile	=	"";
		String				fax		=	"";
		String				address	=	"";

		for (EXT.WBX.PROFILE.PHONE _phone:getWBX().getPROFILE().getPhoneNumbers()){
			if(_phone.type != null){
				if (_phone.type.equals("BusinessPhone")){
					phone	=	_phone.getNumber();
				} else if (_phone.type.equals("MobilePhone")){
					mobile	=	_phone.getNumber();
				} else if (_phone.type.equals("BusinessFax")){
					fax		=	_phone.getNumber();
				}
			}
		}//end if	
		for (EXT.WBX.PROFILE.ADDRESS _addr:getWBX().getPROFILE().getAddresses()){
			if(_addr.type != null && _addr.type.equalsIgnoreCase("CompanyAddr")){
				if(_addr.streetLine1 != null)
					address	+=	_addr.streetLine1.replaceAll(SPECIAL_CHAR_REMOVE, EMPTY_STRING)+"<br />";
				
				if(_addr.streetLine2 != null)
					address	+=	_addr.streetLine2.replaceAll(SPECIAL_CHAR_REMOVE, EMPTY_STRING)+"<br />";
				
				if(_addr.city != null)
					address	+= _addr.city.replaceAll(SPECIAL_CHAR_REMOVE, EMPTY_STRING)+", ";
				
				if(_addr.state != null)
					address	+= _addr.state.replaceAll(SPECIAL_CHAR_REMOVE, EMPTY_STRING)+" "+_addr.zipcode+"<br />";
				
				if(_addr.country != null)
					_addr.country.replaceAll(SPECIAL_CHAR_REMOVE, EMPTY_STRING);
								
			}
		}

		return "\""+id+"\":"+
				"{"+
				"displayName:\""+displayName+"\","+
				(this.isActive!=null?"isActive:\""+isActive+"\",":"")+
				(this.isLocked!=null?"isLocked:\""+isLocked+"\",":"")+
				"company:\""+ext.wbx.prof.companyName+"\","+
				"department:\""+ext.wbx.prof.businessUnit+"\","+
				"title:\""+ext.wbx.prof.jobTitle+"\","+
				"email:\""+id+"@"+domain+"\","+
				"phone:\""+phone+"\","+
				"mobile:\""+mobile+"\","+
				"fax:\""+fax+"\","+
				"address:\""+address+"\""+
				"}";
	}

		/**
		 * Object representation of the WebEx Connect unique userID
		 *
		 */
		public static class WBXCONUID implements Serializable{
final	private static	long 	serialVersionUID	=	-4972050208417890378L;
final	private			String	wbxuid;
			
			WBXCONUID(final String id){
				this.wbxuid	=	id;
			}
			
			/* 
			 * The userID as a String value
			 * 
			 * (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString(){
				return this.wbxuid;
			}
		}
		
		public static class WBXCONGROUPID implements Serializable{
final	private static  long 	serialVersionUID = -3123712663871463104L;
final	private			String	wbxgroupid;
			
			WBXCONGROUPID(final String id){
				this.wbxgroupid	=	id;
			}
			
			@Override
			public String toString(){
				return this.wbxgroupid;
			}
		}
	
		public class EXT implements Serializable{
final	private static	long 	serialVersionUID	=	-7326365659380042024L;
		private			WBX		wbx					=	null;

			EXT(){
				
			}
		
			public EXT.WBX setWBX(String firstName, String lastName){
				this.wbx	=	new WBX(firstName,lastName);
				return this.wbx;
			}

			public WBX getWBX(){
				return this.wbx;
			}
			
			public Element marshalXML(Document doc){
final	Element	_ext		=	doc.createElement("ext");
				if (this.wbx!=null)
					_ext.appendChild(this.wbx.marshalXML(doc));
				return _ext;
			}

			public class WBX implements Serializable{
final	private	static	long 		serialVersionUID	=	3427661461960111679L;

final	private			String		firstName;
final	private			String		lastName;

		private			PROFILE		prof				=	null;
		private			CUCM		cucm				=	null;

				WBX(final String firstname, final String lastname){
					this.firstName			=	firstname;
					this.lastName			=	lastname;
				}

				public EXT.WBX.PROFILE setPROFILE(final String department, final String jobTitle, final String websiteurl, final String companyName){
					this.prof	=	new PROFILE(department,jobTitle,websiteurl,companyName);
					return this.prof;
				}

				public EXT.WBX setCUCM(final String clustername){
					this.cucm	=	new CUCM(clustername);
					return this;
				}

				public String getFirstName(){
					return this.firstName;
				}

				public String getLastName(){
					return this.lastName;
				}

				public PROFILE getPROFILE(){
					return this.prof;
				}

				public CUCM getCUCM(){
					return this.cucm;
				}

				public Element marshalXML(Document doc){
final	Element					_wbx			=	doc.createElement("WBX");				
					if(this.firstName!=null)
						WBXCONorg.documentSetTextContentOfNode("firstName",this.firstName,_wbx);
					if(this.lastName!=null)
						WBXCONorg.documentSetTextContentOfNode("lastName",this.lastName,_wbx);
					if (this.prof!=null)
						_wbx.appendChild(this.prof.marshallXML(doc));
					if (this.cucm!=null)
						_wbx.appendChild(this.cucm.marshalXML(doc));						
					return _wbx;
				}

				public class PROFILE implements Serializable{
final	private	static		long			serialVersionUID	=	-3730369640285193796L;
final	private				String			businessUnit;
final	private				String			jobTitle;				
final	private				String			websiteurl;
final	private				String			companyName;
final	private				Set<ADDRESS>	addresses			=	new HashSet<ADDRESS>();
final	private				Set<PHONE>		phoneNumbers		=	new HashSet<PHONE>();
final	private				Set<EMAIL>		emails				=	new HashSet<EMAIL>();	

					PROFILE(final String businessunit, final String jobtitle, final String websiteurl, final String companyname) {
						this.businessUnit 	=	businessunit;
						this.jobTitle 		=	jobtitle;
						this.websiteurl 	=	websiteurl;
						this.companyName 	=	companyname;
					}

					public void addAddress(final String type, final String streetLine1, final String streetLine2, final String city, final String state, final String zipcode, final String country){
						this.addresses.add(new ADDRESS(type,streetLine1, streetLine2, city, state, zipcode, country));
					}

					public void addPhone(final String type, final PhoneNumber phone){
						this.phoneNumbers.add(new PHONE(type,phone));
					}

					@Deprecated
					public void addEmail(final String type, final String email){
						this.emails.add(new EMAIL(type,email));
					}

					public Set<ADDRESS> getAddresses(){
						return this.addresses;
					}

					@Deprecated
					public Set<EMAIL> getEmails(){
						return this.emails;
					}

					public Set<PHONE> getPhoneNumbers(){
						return this.phoneNumbers;
					}

					public String getBusinessUnit(){
						return this.businessUnit;
					}

					public String getJobTitle(){
						return this.jobTitle;
					}

					public String getWebsiteURL(){
						return this.websiteurl;
					}

					public String getCompanyName(){
						return this.companyName;
					}

					public Element marshallXML(final Document doc){
final	Element					_profile		=	doc.createElement("profile");

						if (getCompanyName()!=null)
							WBXCONorg.documentSetTextContentOfNode("companyName",getCompanyName(),_profile);
						
						if (getWebsiteURL()!=null)
							WBXCONorg.documentSetTextContentOfNode("websiteURL",getWebsiteURL(),_profile);
						
						if (getBusinessUnit()!=null)
							WBXCONorg.documentSetTextContentOfNode("businessUnit",getBusinessUnit(),_profile);
						
						if (getJobTitle()!=null)
							WBXCONorg.documentSetTextContentOfNode("jobTitle",getJobTitle(),_profile);

						if (getAddresses().size()>0){
final	Element					_addresses		=	doc.createElement("addresses");
							for (EXT.WBX.PROFILE.ADDRESS addr:getAddresses())
								_addresses.appendChild(addr.marshalXML(doc));
							_profile.appendChild(_addresses);
						}
						
						if (getPhoneNumbers().size()>0){
final	Element					_phonenumbers	=	doc.createElement("phoneNumbers");
							for (EXT.WBX.PROFILE.PHONE _phone:getPhoneNumbers())
								_phonenumbers.appendChild(_phone.marshallXML(doc));
							_profile.appendChild(_phonenumbers);
						}
/*Emails stored in the profile are now deprecated
						if (getEmails().size()>0){
final	Element					_emails			=	doc.createElement("emails");
							for (EXT.WBX.PROFILE.EMAIL _email:getEmails())
								_emails.appendChild(_email.marshallXML(doc));
							_profile.appendChild(_emails);
						}
*/
						return _profile;
					}

					public class ADDRESS  implements Serializable{
final	private	static	long	serialVersionUID	=	6698799738943799518L;
final	private			String	type;
final	private			String	streetLine1;
final	private			String	streetLine2;
final	private			String	city;
final	private			String	state;
final	private			String	zipcode;
final	private			String	country;

						ADDRESS(final String type, final String streetLine1, final String streetLine2, final String city, final String state, final String zipcode, final String country) {
							this.type 			= type;
							this.streetLine1 	= streetLine1;
							this.streetLine2 	= streetLine2;
							this.city 			= city;
							this.state 			= state;
							this.zipcode 		= zipcode;
							this.country 		= country;
						}

						public String getType() {
							return type;
						}

						public String getStreetLine1() {
							return streetLine1;
						}

						public String getStreetLine2() {
							return streetLine2;
						}

						public String getCity() {
							return city;
						}

						public String getState() {
							return state;
						}

						public String getZipcode() {
							return zipcode;
						}

						public String getCountry() {
							return country;
						}
						
						public Element marshalXML(final Document doc){
final	Element					_addressfind	=	doc.createElement("address");
								_addressfind.setAttribute("find", "addressType");
								WBXCONorg.documentSetTextContentOfNode("addressType",this.type,_addressfind);
							if (this.getStreetLine1()!=null)
								WBXCONorg.documentSetTextContentOfNode("streetLine1",this.streetLine1,_addressfind);
							if (this.getStreetLine2()!=null)
								WBXCONorg.documentSetTextContentOfNode("streetLine2",this.streetLine2,_addressfind);
							if (this.getCity()!=null)
								WBXCONorg.documentSetTextContentOfNode("city",this.city,_addressfind);
							if (this.getState()!=null)
								WBXCONorg.documentSetTextContentOfNode("state",this.state,_addressfind);
							if (this.getZipcode()!=null)
								WBXCONorg.documentSetTextContentOfNode("zipcode",this.zipcode,_addressfind);
							if (this.getCountry()!=null)
								WBXCONorg.documentSetTextContentOfNode("country",this.country,_addressfind);
							
							return _addressfind;
						}	
					}
					
					@Deprecated
					public class EMAIL implements Serializable{
final	private static	long	serialVersionUID	=	6054935273695729150L;
final	private			String	type;
final	private			String	email;

						EMAIL(final String type, final String email) {
								this.type 			=	type;
								this.email 			=	email;
						}

						public String getType() {
							return type;
						}

						public String getEmail() {
							return email;
						}
						
						public Element marshallXML(final Document doc){
final	Element					_emailfind		=	doc.createElement("emailNumber");
							_emailfind.setAttribute("find", "emailType");
							WBXCONorg.documentSetTextContentOfNode("emailType",this.type,_emailfind);
							WBXCONorg.documentSetTextContentOfNode("email",this.email,_emailfind);
							return _emailfind;
						}
					}
					
					public class PHONE implements Serializable{
final	private static	long			serialVersionUID	=	1492168818561311166L;
final					String			type;
final					PhoneNumber		phone;

					PHONE(final String type, final PhoneNumber phone) {
						this.type 	=	type;
						this.phone	=	phone;
					}

					public String getType() {
						return type;
					}

					public String getNumber() {
						return this.phone.toString();
					}

					public String getDisplayNumber() {
						return this.phone.getDisplayName();
					}

					public String getCountryCode() {
						return this.phone.getCountryCode()+"";
					}
					
					public Element marshallXML(final Document doc){
final	Element				_phonefind		=	doc.createElement("phoneNumber");
							_phonefind.setAttribute("find", "phoneType");
							WBXCONorg.documentSetTextContentOfNode("phoneType",this.type,_phonefind);
							WBXCONorg.documentSetTextContentOfNode("number",getNumber(),_phonefind);
						if (getDisplayNumber()!=null)
							WBXCONorg.documentSetTextContentOfNode("displayNumber",getDisplayNumber(),_phonefind);
						if (getCountryCode()!=null)
							WBXCONorg.documentSetTextContentOfNode("countryCode", getCountryCode(),_phonefind);
						return _phonefind;
					}
				}
			}
			public class CUCM implements Serializable{
final	private static	long 	serialVersionUID	=	-1136753577338687837L;
final 	private	 		String	clusterName;

				CUCM(String clustername){
					this.clusterName			=	clustername;
				}

				public String getClusterName() {
					return clusterName;
				}

				public Element marshalXML(final Document doc){
final	Element			_cucm		=	doc.createElement("CUCM");
					WBXCONorg.documentSetTextContentOfNode("clusterName",this.clusterName,_cucm);
					return _cucm;
				}

				@Override
				public String toString(){
					return this.clusterName;
				}
			}
		}
	}
		
	public static class PhoneNumber implements Serializable{
final	private static long 	serialVersionUID	=	3307338077759977614L;
final	private 		String	number;
		private 		String	regioncode			=	"";
		private			int		countrycode			=	0;
		private			String	displayName			=	"";
		
		public PhoneNumber(String num, String cc){
//			if (num!=null && num.length()>=7){//seems the default is a 2char blank field@
//							this.number			=	num.replaceAll( "[^\\d]", "" );	
//			} else {		this.number			=	"";
//			}
							this.number			=	num;
final	PhoneNumberUtil		phoneUtil			=	PhoneNumberUtil.getInstance();
com.google.i18n.phonenumbers.Phonenumber.PhoneNumber number;
			try {
							number 				=	phoneUtil.parse(num, cc!=null?cc:null);
							this.countrycode	=	number.getCountryCode();
							this.regioncode		=	phoneUtil.getRegionCodeForCountryCode(this.countrycode);
			} catch (NumberParseException e) {
			}
		}
		
		public String getRegionCode(){
			return this.regioncode;
		}
		
		public int getCountryCode(){
			return this.countrycode;
		}
		
		public String getDisplayName(){
			return this.displayName;
		}
		
		public void setDisplayName(final String displayName){
			this.displayName	=	displayName;
		}
		
		@Override
		public String toString(){
			try {
final	PhoneNumberUtil	phoneUtil	=	PhoneNumberUtil.getInstance();				
				return phoneUtil.format(phoneUtil.parse(this.number, this.regioncode), PhoneNumberFormat.E164);
			} catch (NumberParseException e) {
				return "";
			}
		}
	}
}