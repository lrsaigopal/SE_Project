package com.placement.subscribe;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter; 
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

/**<p>	
 * This class is used to register a number for alerts. I call this process as subcription
 * To subscribe for usn's the user of this class has to generate a unique key, which is also encrypted.
 * Now this unique key is associated with each usn is matched with the key that is received from the mobile
 * user to authenticate the request.</p>
 * 
 * <b>To Use the servlet:</b>
 * 
 * make an api call with the following parameters
 * <table>
 * 	<tr>
 * 		<th> type </th>
 * 		<td> 0: To add Mobile Numbers </td>
 * 		<td> 1: To remove Add Numbers </td>
 *  </tr>
 *  <tr>
 *  	<th> usn </th>
 *  	<td> A list of USN's, each seperated by semicolon </td>
 *  </tr>
 *  <tr>
 *  	<th> hash </th>
 *  	<td> A list of hashkeys, each seperated by semicolon for the corresponding usn </td>
 *  </tr>
 * </table>
 * This method can be used generously but not too much since there is a limit on the incomming and outgoing bandwidth.
 * Also this servlet makes a lot of datastore calls.
 * 
 * <b> NOTE: </b>
 * This method replies back in html(http-response) not in XML. 
 * henceForth urllib can be used
 * @author Varun V Shenoy
 * @throws java.io.IOException
 */
@SuppressWarnings("serial") 
public class Subscribe extends HttpServlet{
	
	private String subscribeRequestDataStore ="SubscribeRequestDataStore";
	private String subscribeEntity ="SubscribeRequest";
	private String verificationProp = "verificationhash";
	private final String START_RESPONSE = "<?xml version=\"1.0\"><html>"
            +"<head>"
            +"</head>"
            +"<body>";
	private final String END_RESPONSE = "</body></html>";
	
	
	/**
	 * This is the get version of the servlet.
	 * It adds/removes a list of USN's from the subscription based on the type sent.
	 * @param req an request object containing the details of the request
	 * @param resp the response object in which the response is sent.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException 
	{
		String type = req.getParameter("type");
		String usn = req.getParameter("usn");
		String hash = req.getParameter("hash");
		if(type == null || usn == null|| hash == null )
		{
			sendResponse(resp, "Invalid format of the request");
			return;
		} 
		String usnList[] = usn.split(";");
		String hashList[] = hash.split(";");
		if(usnList.length == 0 || hashList.length == 0||usnList.length != hashList.length)
		{
			sendResponse(resp, "Sorry. The Length of the USN List and the length of the hashkeys are not the same");
			return;
		}
		else if(type.equals("0"))
		{
			int i =0;
			String added="";
			String notAdded="";
			for(String USN: usnList)
			{
				if(USN.length() == 10 && addToSubscribeRequest(USN, hashList[i]))
				{
					added += (USN + " ");
				}else
				{
					notAdded += (USN + " ");
				}
				++i;
			}
			sendResponse(resp, "<p>ADDED: "+ added+ "</p>"+"<p>NOT ADDED: "+ notAdded+ "</p>");
			
		}
		else if(type.equals("1"))
		{
			int i =0;
			String removed= "";
			String notRemoved= "";
			for(String USN: usnList)
			{
				if(USN.length() == 10 && removeSubscribeRequest(USN, hashList[i]))
				{
					removed += USN + " ";
				}
				else{
					notRemoved += USN + " ";
				}
				++i;
			}
			sendResponse(resp, "<p>REMOVED: "+ removed+ "</p>"+"<p>NOT REMOVED: "+ notRemoved+ "</p>");
		}
	}
	
	
	/**
	 * This is the get version of the servlet
	 * It adds/removes a list of USN's from the subscription based on the type sent.
	 * @param req an request object containing the details of the request
	 * @param resp the response object in which the response is sent.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		doGet(req,resp);
	}
	
	
	/**
	 * Adds a person to the datastore of valid users.
	 * 
	 * @param usn the usn of the person whose subscription is to be added.
	 * @param verificationHash the verificationHash is added to the database for future verifications for registering mobile users.
	 * @return whether the operation was successful or not.
	 */
	private boolean addToSubscribeRequest(String usn,String verificationHash)
	{
		 boolean successful = false;
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 Key usnKey = KeyFactory.createKey("SubscribeRequestDataStore", usn);
		 // Run an ancestor query to ensure we see the most up-to-date
		 // view of the SubscribeRequests belonging to the selected USN.
		 Query query = new Query("SubscribeRequest", usnKey);
		 List<Entity> mobileList = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(2));
		 if(mobileList.isEmpty())
		 {
			// The user is subscribing for the first time
		    	Entity usnEntity = new Entity("SubscribeRequest",usnKey);
		    	usnEntity.setProperty("verificationhash", verificationHash);
		    	datastore.put(usnEntity);
		    	successful = true;
		 }
		 else if(mobileList.size() == 1)
		 {
			Iterator<Entity> usnEntity = mobileList.iterator();
			Entity verificationHashPresentForTheUSN = usnEntity.next();
			verificationHashPresentForTheUSN.setProperty("verificationhash", verificationHash);
			datastore.put(verificationHashPresentForTheUSN);
			successful = true;
		 }else
		 {
			 //Something wrong, we do not allow one person to register 2 verification keys
			 successful = false;
		 }
		return successful;
	}
	

	/**
	 * Adds a person to the datastore of valid users.
	 * 
	 * @param usn the usn of the person whose subscription is to be removed.
	 * @param verificationHash the verificationHash authenticates the whole removal transaction.
	 * @return whether the operation was successful or not.
	 */
	private boolean removeSubscribeRequest(String usn,String verificationHash)
	{ 
		 boolean successful = false;
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 Key usnKey = KeyFactory.createKey(this.subscribeRequestDataStore, usn);
		 // Run an ancestor query to ensure we see the most up-to-date
		 // view of the SubscribeRequests belonging to the selected USN.
		 Query query = new Query(this.subscribeEntity, usnKey);
		 List<Entity> mobileList = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(2));
		 if(mobileList.isEmpty())
		 {
			// Nothing to remove
		    	
		 }
		 else if(mobileList.size() == 1)
		 {
			
			Iterator<Entity> usnEntity = mobileList.iterator();
			Entity verificationHashPresentForTheUSN = usnEntity.next();
			if(verificationHash.equals(verificationHashPresentForTheUSN.getProperty(this.verificationProp)))
			{
				datastore.delete(verificationHashPresentForTheUSN.getKey());
				
				successful = true;
			}
			else{
				successful = false;
			}
		 }else
		 {
			 //Something wrong, we do not allow one person to register 2 verification keys
			 // Should we remove those inconsistencies
			 // Probably yes if something catastrophic is wrong then allow the poor user to
			 // start fresh
			 // But what if its due to the time needed from consensus
			 // But, from '3db' Its no longer an issue and my app will be communicating
			 // to a single replica. What if the master/coordinator changes??. The previous coordiator
			 // will return network address of new coordinator internally.
			 
			 for(Entity usnEntity : mobileList)
			 {
				 if(successful && verificationHash.equals(usnEntity.getProperty(this.verificationProp)))
					{
						usnEntity.removeProperty(this.verificationProp);
						datastore.delete(usnEntity.getKey()); 
						successful = true;
					}
					else{
						successful = false;
					} 
			 }
			 successful = false;
		 }
		return successful;
		}
	

	/**
	 * This method is used whenever one wants to say something to the user, ie the person who has made the 
	 * API call.
	 * It is strongly advised that you call this method no more than once in the entire servlet.(Not a requirement)
	 * 
	 * @param httpResponse the response object sent by the servlet container
	 * @param response  the response text to be sent to the user, ie the person who has sent a text message
	 * @throws IOException
	 */
	private void sendResponse(HttpServletResponse httpResponse, String response) throws IOException
	{
		httpResponse.setContentType("text/html");
        try(PrintWriter out = httpResponse.getWriter())
        {   
            out.println(this.START_RESPONSE+response+this.END_RESPONSE);
        }
    }
} 
 
