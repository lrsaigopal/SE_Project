package com.placement.alerts;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter; 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;



/**
 * This servlet is used to register alerts. This is a low level api and one has to use these api's to perform registering of alerts alerts
 * 
 * <b>INPUT:</b>
 * The input to this is a series of USN's semicolon seperated 
 * There has to be a single message.
 * The user also has to provide a date.And this date is to be provided as a part of the message.
 * The date should also be given the following format
 * 12-2-2014 => 12022014
 * Type 0 : set alerts
 * Type >=1: delete alerts
 * sample url query</p>
 * 		http://alert?type=0&usn=1pi10cs135;1pi10cs134&message=Intuit is comming at pesit. Stiphend is 30k;1122013
 *      </p>Encoded URI http%3A%2F%2Falert%3Ftype%3D0%26usn%3D1pi10cs135%3B1pi10cs134%26message%3DIntuit%20is%20comming%20at%20pesit.%20Stiphend%20is%2030k%3B1122013
 *      
 * <b>Very Very Important Note</b>
 * The response on successful setting of an alert returns a number that number is to be stored, incase you
 * plan on deleting the alert in future.
 * Please donot leave the usn and message blank in case of deleting.
 * message=;DD-MM-YY usn=SOME10DIGITNUMBER
 * @author Varun V Shenoy
 *
 */
 
@SuppressWarnings("serial")
public class Alerts extends HttpServlet {
	
	private final String START_RESPONSE = "<?xml version=\"1.0\"><html>"
            +"<head>"
            +"</head>"
            +"<body>";
	private final String END_RESPONSE = "</body></html>";
	
	/**
	 * The Get method of the servlet
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException 
	{
		 String usns = req.getParameter("usn");
		 String message = req.getParameter("message");
		 String type = req.getParameter("type");
		 if(message == null || usns == null)
		 {
			sendResponse(resp,"Invalid format of the request");
			 return;
		 }
		 String messageDate[] = message.split(";");
		 String usnList[] = message.split(";");
		 if(messageDate.length == 0 || usnList.length == 0 || messageDate.length !=2)
		 {
			sendResponse(resp,"Invalid format of the request");
			 return;
		 }
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 int i =1;
		 boolean successful = false;
		 // We will store the key as a main data store key, along with a number
		 // this number will help us distinguish between messages sent to different people on the same day
		 if(type.equalsIgnoreCase("0"))
		 {
				 while(!successful)
				 {	 
					 Key dateKey = KeyFactory.createKey("AlertsDataStore",messageDate[1]+new Integer(i).toString());
					 Query query = new Query("AlertRequest", dateKey);
					 List<Entity> mobileList = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(2));
					 if(mobileList.isEmpty())
					 {
						 //The first entry for the date
						 Entity messageEntity = new Entity("AlertRequest",dateKey);
						 messageEntity.setProperty("usnlist", usns);
						 messageEntity.setProperty("message", messageDate[0]);
						 datastore.put(messageEntity);
						 successful = true;
					 }
					 else{
						 ++i;
					 }
				 }
				 sendResponse(resp, ""+i);
		 }
		 else
		 {
				 i = Integer.parseInt(type);
				 Key dateKey = KeyFactory.createKey("AlertsDataStore",messageDate[1]+new Integer(i).toString());
				 Query query = new Query("AlertRequest", dateKey);
				 List<Entity> mobileList = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(2));
				 if(!mobileList.isEmpty())
				 {
					 datastore.delete(mobileList.iterator().next().getKey());
					 sendResponse(resp,"Successfully deleted the alert with type"+type);
				 }
				 else
				 {
					 sendResponse(resp, "Non existent Type");
				 }
			 
		 }
		 
	}

	
	/**
	 * The post method of the servlet
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		doGet(req,resp);
	}
	
	
	/**
	 * This method is used whenever one wants to say something to the user, ie the person who has sent a text
	 * message.
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
