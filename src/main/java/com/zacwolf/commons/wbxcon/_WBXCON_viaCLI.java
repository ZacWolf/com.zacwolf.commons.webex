package com.zacwolf.commons.wbxcon;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This provides a command line interface for interacting with a WebEx Connect Managed Org
 *
 */
public class _WBXCON_viaCLI {

final	private	static	List<String>	OPTS_ORDER	=	new ArrayList<String>();

	static{
		OPTS_ORDER.add("D");
		OPTS_ORDER.add("U");
		OPTS_ORDER.add("P");
		OPTS_ORDER.add("A");
		OPTS_ORDER.add("cred");
		OPTS_ORDER.add("p");
		OPTS_ORDER.add("u");
		OPTS_ORDER.add("ug");
		OPTS_ORDER.add("uc");
		OPTS_ORDER.add("u2s");
		OPTS_ORDER.add("u2l");
		OPTS_ORDER.add("u2g");
		OPTS_ORDER.add("u2c");
		OPTS_ORDER.add("u2p");
		OPTS_ORDER.add("cg");
		
		OPTS_ORDER.add("r");
		OPTS_ORDER.add("rx");
		OPTS_ORDER.add("rf");
	}

	private _WBXCON_viaCLI() { }

	@SuppressWarnings({ "static-access", "deprecation" })
	public final static void main(final String[] args){
final	CommandLineParser 			parser				=	new BasicParser();
		_WBXCONfactory				factory				=	null;
		Options						options				=	new Options();
									options.addOption(
										OptionBuilder.withLongOpt("updateTRUSTSTORE")
													 .withDescription("Update the SSL truststore with the latest certificate from webexconnect.com")
													 .hasArgs(2)
													 .withValueSeparator(':')
													 .withArgName("hostname:port")
													 .create("trust")
									);		
		try{
		CommandLine					cmd					=	parser.parse( options, args,true);
									options				=	new Options();
									options.addOption(
										OptionBuilder.withLongOpt("wapiDOMAIN")
													 .withDescription("The ManagedOrg/Domain name")
													 .hasArg()
													 .withArgName( "REQUIRED:domain" )
													 .isRequired()
													 .create("D")
									);
									options.addOption(
										OptionBuilder.withLongOpt("wapiUSER")
													 .withDescription("UserName to use for making REST API calls")
													 .hasArg()
													 .withArgName("REQUIRED:userName")
													 .isRequired()
													 .create("U")
									);
									options.addOption(
										OptionBuilder.withLongOpt("wapiPASS")
													 .withDescription("Password to use for making REST API calls")
													 .hasArg()
													 .withArgName("REQUIRED:userPASS")
													 .isRequired()
													 .create("P")
									);
									options.addOption(
										OptionBuilder.withLongOpt("wapiAUTHURL")
													 .withDescription("The URL of the WebEx auth.do servlet")
													 .hasArg()
													 .withArgName("AUTHURL(optional)")
													 .create("A")
									);
									options.addOption(
										OptionBuilder.withDescription("Dump cred token")
													 .create("cred")
									);
									options.addOption(
										OptionBuilder.withDescription("Verbose")
													 .create("v")
									);
//									options.addOption(
//										OptionBuilder.withLongOpt("test")
//													 .withDescription("Test all of the user methods")
//													 .create("t")
//									);
									options.addOption(
										OptionBuilder.withLongOpt("password")
													 .withDescription("Generate a random password using the rules for the org")
													 .create("p")
									);
									options.addOption(
										OptionBuilder.withLongOpt("getUser")
													 .withDescription("Dump user XML by supplying the userName without the domain")
													 .hasArg()
													 .withArgName("userName")
													 .create("u")
									);
									options.addOption(
										OptionBuilder.withLongOpt("getUserGroups")
													 .withDescription("Retrieve a set of groups that the given user belongs too")
													 .hasArg()
													 .withArgName("userName")
													 .create("ug")
									);
									options.addOption(
										OptionBuilder.withLongOpt("userToStatus")
													 .withDescription("Set active status by userName")
													 .hasArgs(2)
													 .withValueSeparator()
													 .withArgName("userName=true/false")
													 .create("u2s")
									);
									options.addOption(
										OptionBuilder.withLongOpt("userToLocked")
													 .withDescription("Set temporary lock status for account")
													 .hasArgs(2)
													 .withValueSeparator()
													 .withArgName("userName=true/false")
													 .create("u2l")
									);
									options.addOption(
										OptionBuilder.withLongOpt("addToGroup")
													 .withDescription("Add userID to groupID")
													 .hasArgs(2)
													 .withValueSeparator()
													 .withArgName("WBXUID=WBXGROUPID")
													 .create("u2g")
									);
final	OptionGroup					clusterGroup	=	new OptionGroup();
									clusterGroup.addOption(
										OptionBuilder.withLongOpt("getClusters")
													 .withDescription("Get CUCM cluster names configured for domain" )
													 .create("gc")
									);
									clusterGroup.addOption(
										OptionBuilder.withLongOpt("userToCluster")
													 .withDescription("Set CUCM cluster for user(s)")
													 .hasArgs(2)
													 .withValueSeparator()
													 .withArgName("CECID=ClusterName")
													 .create("u2c")
									);
									clusterGroup.addOption(
										OptionBuilder.withLongOpt("getUserCluster")
													 .withDescription("Get CUCM clustername set for user")
													 .hasArg()
													 .withArgName("CECID")
													 .create("uc")
									);
									options.addOptionGroup(clusterGroup);
									options.addOption(
										OptionBuilder.withLongOpt("userToPrivilege")
													 .withDescription("Assert a special priviledge to a user")
													 .hasArgs(2)
													 .withValueSeparator()
													 .withArgName("CECID=privilege")
													 .create("u2p")
									);
									options.addOption(
										OptionBuilder.withLongOpt("reportType")
													 .withDescription("Generate a named report. Requires wapiUser have WBX:RunOrgReport privilege")
													 .hasArg()
													 .create('r')
									);
									options.addOption(
										OptionBuilder.withLongOpt("reportXML")
													 .withDescription("The xml params required for the report")
													 .hasArg()
													 .withArgName("QuotedString")
													 .create("rx")
									);
									options.addOption(
										OptionBuilder.withLongOpt("reportFile")
													 .withDescription("The filepath/name where the report will be stored.")
													 .hasArg()
													 .withArgName("Default:System.out")
													 .create("rf")
									);
									cmd					=	parser.parse( options, args);
final	boolean						verbose				=	cmd.hasOption('v');

				if (verbose) System.out.println("Starting...")	;
				if (cmd.hasOption('A'))
									factory				=	new _WBXCONfactory(cmd.getOptionValue("D"),cmd.getOptionValue("A"),cmd.getOptionValue('U'),cmd.getOptionValue('P'));
				else				factory				=	new _WBXCONfactory(cmd.getOptionValue("D"),cmd.getOptionValue('U'),cmd.getOptionValue('P'));
				
				if (cmd.hasOption("cred")){
					if (verbose) System.out.println("\nCRED:=============================================================================");
					System.out.println(factory.org.getWAPIURL());
					if (verbose) System.out.println("==================================================================================\n");
				}
				
				if (cmd.hasOption("p")){
					if (verbose) System.out.println("\nPASSWORD GENERATED FROM ORG RULES:================================================");
					System.out.println(factory.org.passwordrule.getRandomPassword());
					if (verbose) System.out.println("==================================================================================\n");
				} else {
					if (cmd.hasOption("gc")){
						if (verbose) System.out.println("\nCLUSTERNAME FOR DOMAIN:===========================================================");
						for (final String clustername:factory.domainGetCMCUClusterSet())
						System.out.println(clustername);
						if (verbose) System.out.println("==================================================================================\n");
					}
					if (cmd.hasOption("u")){
						if (verbose) System.out.println("\nUSER:==========================================================================");
						if (cmd.getOptionValue('u').contains("%"))
							WBXCONorg.documentPrettyPrint(factory.org.restapiAccountQuery("userName", "<like><path>userName</path><value>"+cmd.getOptionValue('u')+"@"+cmd.getOptionValue("D")+"</value></like>", "/user/userName,ASC", "/wbxapi/return"),System.out);
						else
							WBXCONorg.documentPrettyPrint(factory.accountGetAsRawDom(cmd.getOptionValue("u")), System.out);
						if (verbose) System.out.println("==================================================================================\n");
					}
					if (cmd.hasOption("ug")){
final	String						uid					=	cmd.getOptionValue("ug");
final	WBXCONuser.WBXCONUID		wbxconuid;
							if (!uid.startsWith("U"))
									wbxconuid			=	factory.accountGet((uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID(uid);
						if (verbose) System.out.println("\nGROUPS:===========================================================================");
						if (verbose) System.out.println("For user:"+uid+" wbxconuid:"+wbxconuid);
						for (final String line:factory.accountGetGroups(wbxconuid))
							System.out.println(line);
						if (verbose) System.out.println("==================================================================================\n");
					}
					if (cmd.hasOption("uc")){
final	String						uid					=	cmd.getOptionValue("uc");
final	WBXCONuser.WBXCONUID		wbxconuid;
							if (!uid.startsWith("U"))
									wbxconuid			=	factory.accountGet((uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID(uid);
						if (verbose) System.out.println("\nCUCM Clustername:=================================================================");
						if (verbose) System.out.println("For user:"+uid+" wbxconuid:"+wbxconuid);
						System.out.println(factory.accountGetCMCUClusterValue(wbxconuid));
						if (verbose) System.out.println("==================================================================================\n");
					}
					if (cmd.hasOption("u2s")){
						for (Object uid: cmd.getOptionProperties("u2s").keySet()){
final	WBXCONuser.WBXCONUID		wbxconuid;						
							if (!((String)uid).startsWith("U"))
									wbxconuid			=	factory.accountGet(((String)uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID((String)uid);
final	boolean						active				=	((String)cmd.getOptionProperties("u2s").get(uid)).equalsIgnoreCase("true");
							factory.accountChangeStatus(wbxconuid, active);
							System.out.println("User:"+uid+" wbxconuid:"+wbxconuid+" set:"+(active?"active":"deactivated"));
						}
					}
					if (cmd.hasOption("u2l")){
						for (Object uid: cmd.getOptionProperties("u2l").keySet()){
final	WBXCONuser.WBXCONUID		wbxconuid;						
							if (!((String)uid).startsWith("U"))
									wbxconuid			=	factory.accountGet(((String)uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID((String)uid);
final	boolean						locked				=	((String)cmd.getOptionProperties("u2l").get(uid)).equalsIgnoreCase("true");
							factory.accountChangeLocked(wbxconuid, locked);
							System.out.println("User:"+uid+" wbxconuid:"+wbxconuid+" set:"+(locked?"locked":"unlocked"));
						}
					}
					if (cmd.hasOption("u2g")){
						for (Object uid: cmd.getOptionProperties("u2g").keySet()){
final	WBXCONuser.WBXCONUID		wbxconuid;						
							if (!((String)uid).startsWith("U"))
									wbxconuid			=	factory.accountGet(((String)uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID((String)uid);
final	String						gid					=	(String)cmd.getOptionProperties("u2g").get(uid);
final	WBXCONuser.WBXCONGROUPID	wbxcongroupid;
							if (!gid.startsWith("G"))
									wbxcongroupid		=	factory.groupGetIDforName(gid);
							else	wbxcongroupid		=	new WBXCONuser.WBXCONGROUPID(gid);
									factory.accountAddToGroup(wbxconuid, wbxcongroupid);
							System.out.println("User:"+uid+" wbxconuid:"+wbxconuid+" added to group:"+wbxcongroupid);
						}
					}
					if (cmd.hasOption("u2c")){
						for (Object uid: cmd.getOptionProperties("u2c").keySet()){
final	WBXCONuser.WBXCONUID		wbxconuid;
							if (!((String)uid).startsWith("U"))
									wbxconuid			=	factory.accountGet(((String)uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID((String)uid);
final	String						clustername			=	(String)cmd.getOptionProperties("u2c").get(uid);
									factory.accountSetCMCUClusterValue(wbxconuid, clustername);
							System.out.println("User:"+uid+" wbxconuid:"+wbxconuid+" set to CUCM clustername:"+clustername);
						}
					}
					if (cmd.hasOption("u2p")){
						for (Object uid: cmd.getOptionProperties("u2p").keySet()){
final	WBXCONuser.WBXCONUID		wbxconuid;
							if (!((String)uid).startsWith("U"))
									wbxconuid			=	factory.accountGet(((String)uid)).getWBXUID();
							else	wbxconuid			=	new WBXCONuser.WBXCONUID((String)uid);
final	String						privilege			=	(String)cmd.getOptionProperties("u2p").get(uid);
									factory.accountAssertSpecialPrivilege(wbxconuid, privilege, "add");
							System.out.println("User:"+uid+" wbxconuid:"+wbxconuid+" added to privilege:"+privilege);
						}
					}
					if (cmd.hasOption("r")){
						if (!cmd.hasOption("rx"))
							throw new ParseException ("Must specify the xml parameters for the report");
							if (verbose) System.out.println("REPORT:===========================================================================\n");
							if (verbose) System.out.println("\nSubmitting reportType:"+cmd.getOptionValue("r")+" with params:\n"+cmd.getOptionValue("rx")+"\n");
final	OutputStream				out;
						if (cmd.hasOption("rf")){
									out					=	new FileOutputStream(cmd.getOptionValue("rf"));
							System.out.println("Report will be saved to file:"+cmd.getOptionValue("rf"));
						} else		out					=	System.out;
						try{		factory.getReport(cmd.getOptionValue("r"),cmd.getOptionValue("rx"), out);
						} finally {
							out.flush();
							out.close();
						}
						if (verbose) System.out.println("==================================================================================\n");
					}
				}
		} catch (ParseException exp ) {
final	HelpFormatter	formatter	=	new HelpFormatter();
						formatter.setOptionComparator(new OptionComparator<Option>());
						formatter.printHelp( "java -jar WBXfactory.jar", 
											 "\nCommand line interface for WebEx Connect\n"+
											 "--------------------------------------------------------------------------\n",
											 options,
											 "\n--------------------------------------------------------------------------\n"+
											 "Zac Morris <zac@zacwolf.com> https://github.com/ZacWolf/com.zacwolf.commons" );
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (factory!=null)
				factory.finalize();
		}
	}

	private static class OptionComparator<T extends Option> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			return OPTS_ORDER.indexOf(o1.getOpt()) - OPTS_ORDER.indexOf(o2.getOpt());
		}
	}
}
