/*
 ADOBE SYSTEMS INCORPORATED
 Copyright 2007 Adobe Systems Incorporated
 All Rights Reserved
 
 NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the 
 terms of the Adobe license agreement accompanying it.  If you have received this file from a 
 source other than Adobe, then your use, modification, or distribution of it requires the prior 
 written permission of Adobe.
 */

using System;
using System.Collections;
using System.ComponentModel;
using System.Web.Services.Protocols;
using System.Net;
using System.Xml;

namespace RegisterPolicy
{
    //  The PDRLTypes namespace contains wrapper classes the the PDRL XML format that is accepted
    //  by LiveCycle ES Rights Management services.  

	namespace PDRLTypes
	{
		/// <summary>
		/// The Principal class: a user or group registered on the Policy Server
		/// </summary>
		class Principal
		{
			public enum SpecialPrincipalTypes
			{
				ALL_AUTHENTICATED_USERS,
				PUBLISHER,
                ALL_PRINCIPALS_GROUP
			}

			public Principal(string name, string domain)
			{				
				_name   = name;
				_domain = domain;
				_type   = "USER";
			}

			public Principal(string name, string domain, string type) : this(name,domain)
			{
				_type   = type;				
			}

			static public Principal createSpecialPrincipal( SpecialPrincipalTypes principal_type )
			{
				string name = "";
				string type = "USER";
                string domain = "EDC_SPECIAL";

				switch(principal_type)
				{
					case SpecialPrincipalTypes.ALL_AUTHENTICATED_USERS:
						name = "all_authenticated_users";
						type = "SYSTEM";
						break;

					case SpecialPrincipalTypes.PUBLISHER:
						name = "publisher";
						break;

                    case SpecialPrincipalTypes.ALL_PRINCIPALS_GROUP:
                        name = "GROUP_ALLPRINCIPALS";
                        domain = "DefaultDom";
                        type = "GROUP";
                        break;

                    default:
						throw new InvalidEnumArgumentException();
				}

				return new Principal(name, domain, type);
			}

			public override string ToString()
			{
				XmlDocument xml_document = new XmlDocument();
				
				XmlElement root_node = xml_document.CreateElement("pdrl", "Principal", "http://www.adobe.com/schema/1.0/pdrl");
				
				XmlElement name = xml_document.CreateElement("pdrl", "PrincipalName", "http://www.adobe.com/schema/1.0/pdrl");
				name.InnerText = _name;

				XmlElement domain = xml_document.CreateElement("pdrl","PrincipalDomain", "http://www.adobe.com/schema/1.0/pdrl");
				domain.InnerText = _domain;

				root_node.SetAttribute("PrincipalNameType", _type);				
				root_node.AppendChild(domain);
				root_node.AppendChild(name);				

				xml_document.AppendChild(root_node);

				return xml_document.OuterXml;				
			}

			private string _domain;
			private string _name;
			private string _type;
		}

		/// <summary>
		/// Permission:  a permission
		/// </summary>
		class Permission
		{
			class IncompletePermissionException : ApplicationException
			{

			}

			public Permission( string name, string access )
			{
				_name   = name;
				_access = access;
			}

			/// <summary>
			/// Serializes the Permission to its PDRL representation
			/// </summary>
			/// <returns>An XML representing the permission object</returns>
			public override string ToString()
			{
				if( _name   == null ||
					_access == null )
				{
					throw new IncompletePermissionException();
				}

				XmlDocument xml_document = new XmlDocument();
				
				XmlElement root_node = xml_document.CreateElement("pdrl", "Permission", "http://www.adobe.com/schema/1.0/pdrl");
				root_node.SetAttribute( "xmlns:pdrl-ex", "http://www.adobe.com/schema/1.0/pdrl-ex" );
				root_node.SetAttribute( "PermissionName", _name );
				root_node.SetAttribute( "Access", _access );

				xml_document.AppendChild(root_node);

				return xml_document.OuterXml;
			}

			public static string allowAccessString()
			{
				return "ALLOW";
			}

			private string _name;
			private string _access;
		}

		/// <summary>
		/// PolicyEntry:  an entry for a principal in a policy that specifies access rights
		/// </summary>
		class PolicyEntry
		{
			class IncompletePolicyEntryException : ApplicationException
			{
				
			}

			public void setPrincipal(Principal principal)
			{
				_principal = principal;
			}

			public void addPermissions(ArrayList permissions_granted)
			{
				_permissions_granted = permissions_granted;
			}

			/// <summary>
			/// Serializes the PolicyEntry to its PDRL XML representation
			/// </summary>
			/// <returns>An XML representation of this PolicyEntry</returns>
			public override string ToString()
			{
				if( _principal == null ||
					_permissions_granted == null )
				{
					throw new IncompletePolicyEntryException();
				}

				XmlDocument xml_document = new XmlDocument();
				
				XmlElement root_node = xml_document.CreateElement("pdrl", "PolicyEntry", "http://www.adobe.com/schema/1.0/pdrl");
				xml_document.AppendChild(root_node);

				XmlDocument inner_document = new XmlDocument();

				foreach( Permission current_permission in _permissions_granted )
				{
					inner_document.LoadXml(current_permission.ToString());
					root_node.AppendChild(xml_document.ImportNode(inner_document.DocumentElement,true));
				}

				inner_document = new XmlDocument();				
				inner_document.LoadXml(_principal.ToString());
				root_node.AppendChild(xml_document.ImportNode(inner_document.DocumentElement, true));				

				return xml_document.OuterXml;
			}

			private Principal _principal = null;
			private ArrayList _permissions_granted = null;
		}

		/// <summary>
		/// Policy:  aggregates principals and access rights
		/// </summary>
		class Policy
		{
			public Policy(string name, string description)
			{
				_name = name;
				_description = description;
			}

			public void addEntry(PolicyEntry new_entry)
			{
				_policy_entries.Add(new_entry);
			}

			/// <summary>
			/// Serializes the policy to a PDRL XML representation that can be passed to the Policy Server SOAP API
			/// </summary>
			/// <returns>An XML representation of this Policy</returns>
			public override string ToString()
			{
				//  Return the Policy as an XML representation

				XmlDocument xml_document = new XmlDocument();
				
				XmlDeclaration declaration = xml_document.CreateXmlDeclaration("1.0","utf-8",null);

				XmlElement root_node = xml_document.CreateElement("pdrl", "Policy", "http://www.adobe.com/schema/1.0/pdrl");
				root_node.SetAttribute("PolicyName", _name);
				root_node.SetAttribute("PolicyDescription", _description);

				xml_document.InsertBefore(declaration,xml_document.DocumentElement);
				xml_document.AppendChild(root_node);				

				foreach( PolicyEntry policy_entry in _policy_entries )
				{
					XmlDocument inner_document = new XmlDocument();				
					inner_document.LoadXml(policy_entry.ToString());

					root_node.AppendChild(xml_document.ImportNode(inner_document.DocumentElement, true));
				}

				return xml_document.OuterXml;
			}

			private string _name;
			private string _description;
			private ArrayList _policy_entries = new ArrayList();
		}

	}

	/// <summary>
	/// A sample of registering a Policy with the Policy Server SOAP API
	/// </summary>
	class RegisterPolicy
	{
		public const string SAMPLE_POLICY_NAME = "Sample .NET Client Policy";
		public const string SAMPLE_POLICY_DESCRIPTION = "Sample";

		private static string _user_id   = "";
		private static string _password  = "";

		static bool consumeArguments( string[] args )
		{
			if( args.Length != 2 )
			{
				return false;
			}
		
			_user_id  = args[0];
			_password = args[1];

			return true;
		}

		static void printUsage()
		{
			System.Console.WriteLine( "RegisterPolicy sample usage:" );
			System.Console.WriteLine( "    RegisterPolicy user_id password" );
			System.Console.WriteLine( "Edit Host URL in WebReference " );
		}

		static void dumpException( Exception e )
		{			
			System.Console.WriteLine( e.Message );
			System.Console.WriteLine( e.StackTrace );
		}

		static PDRLTypes.Policy createSamplePolicy()
		{
            // PUBLISHER POLICY ENTRY
            PDRLTypes.PolicyEntry publisher_policy_entry = new PDRLTypes.PolicyEntry();

            ArrayList publisher_permissions = new ArrayList();
            publisher_permissions.Add(new PDRLTypes.Permission("pdrl-ex:com.adobe.aps.onlineOpen", PDRLTypes.Permission.allowAccessString()));
            publisher_permissions.Add(new PDRLTypes.Permission("pdrl-ex:com.adobe.aps.offlineOpen", PDRLTypes.Permission.allowAccessString()));
            publisher_permissions.Add(new PDRLTypes.Permission("pdrl-ex:com.adobe.aps.pdf.copy", PDRLTypes.Permission.allowAccessString()));

            publisher_policy_entry.addPermissions(publisher_permissions);
            publisher_policy_entry.setPrincipal(PDRLTypes.Principal.createSpecialPrincipal(PDRLTypes.Principal.SpecialPrincipalTypes.PUBLISHER));

            // ALL USERS POLICY ENTRY
            PDRLTypes.PolicyEntry all_users_policy_entry = new PDRLTypes.PolicyEntry();

            ArrayList all_users_permissions = new ArrayList();
            all_users_permissions.Add(new PDRLTypes.Permission("pdrl-ex:com.adobe.aps.onlineOpen", PDRLTypes.Permission.allowAccessString()));
            all_users_permissions.Add(new PDRLTypes.Permission("pdrl-ex:com.adobe.aps.offlineOpen", PDRLTypes.Permission.allowAccessString()));

            all_users_policy_entry.addPermissions(all_users_permissions);
  			all_users_policy_entry.setPrincipal(PDRLTypes.Principal.createSpecialPrincipal(PDRLTypes.Principal.SpecialPrincipalTypes.ALL_PRINCIPALS_GROUP));

            // POLICY
            PDRLTypes.Policy sample_policy = new PDRLTypes.Policy(SAMPLE_POLICY_NAME, SAMPLE_POLICY_DESCRIPTION);
            sample_policy.addEntry(publisher_policy_entry);
            sample_policy.addEntry(all_users_policy_entry);

			return sample_policy;
		}		

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			if( !consumeArguments(args) )
			{
				printUsage();

				return;
			}

			PDRLTypes.Policy sample_policy = createSamplePolicy();

			string sample_policy_xml = sample_policy.ToString();

            //  Create a proxy to the LiveCycle ES Rights Management web service, and give it our
            //  user name and password credentials.

			EDCRightsManagement.RightsManagementServiceService service = new EDCRightsManagement.RightsManagementServiceService();

			service.Credentials = new NetworkCredential(_user_id, _password);

			try
			{
				EDCRightsManagement.PolicySpec new_policy = new EDCRightsManagement.PolicySpec();

				new_policy.name        = SAMPLE_POLICY_NAME;
				new_policy.description = SAMPLE_POLICY_DESCRIPTION;  
				new_policy.policyType  = 0;
				new_policy.offlineLeasePeriod = 30;
				new_policy.policyXml   = sample_policy_xml;

				service.registerPolicy(new_policy, null);

                System.Console.WriteLine( "Success!  Created policy: " + SAMPLE_POLICY_NAME );
			}
			catch(Exception s)
			{
				dumpException(s);
			}						
		}
	}
}

