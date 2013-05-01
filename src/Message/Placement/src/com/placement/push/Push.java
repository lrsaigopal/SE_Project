package com.placement.push;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;


/**
 * This class Does the push operation. It is of no use to the intermediate user ie backend.
 * The execution of this servlet is done by the google cron task scheduler.
 * The 3 steps involved are
 * 1) Get the requests for that day
 * 2) Get Mobilehash for each user
 * 3) Send the message to the user if Mobilehash is valid
 * 
 * Errors Status code meanings
 * <table>

<tr>
<th>Error Code</th> <th>Error message - Description</th>
</tr>
<tr>
<td>
<p>0</p>
</td>
<td>
<p>Success!</p>
</td>
</tr>
<tr>
<td>
<p>-1</p>
</td>
<td>
<p>Unknown Exception(Usually Server side)<br> Have a retry logic in place to call the API again in case such an error code is received or wait till the APIs are back to being functional.</p>
</td>
</tr>
<tr>
<td>
<p>-3</p>
</td>
<td>
<p>Invalid input<br> Incorrect format for calling the API – Check the right syntax for making the API call</p>
</td>
</tr>
<tr>
<td>
<p>-101</p>
</td>
<td>
<p>No such mobile<br> mobile number does not exist</p>
</td>
</tr>
<tr>
<td>
<p>-103</p>
</td>
<td>
<p>MAX Publisher Allocation exceeded<br> No more than 250 messages per 5 minutes per mobile number<br> No more than 20 messages per 10 seconds per mobile number</p>
</td>
</tr>
<tr>
<td>
<p>-104</p>
</td>
<td>
<p>Number registered with NCPR</p>
</td>
</tr>
<tr>
<td>
<p>-300</p>
</td>
<td>
<p>Missing publisher key<br> Get your publisher key under “Build and Manage my apps section” on txtWeb.com and include it in the parameter list of the API call</p>
</td>
</tr>
<tr>
<td>
<p>-301</p>
</td>
<td>
<p>Incorrect publisher key<br> Check and verify your publisher key under “Build and Manage my apps section” on txtWeb.com against the one you have sent in the API request call</p>
</td>
</tr>
<tr>
<td>
<p>-400</p>
</td>
<td>
<p>Missing application key<br> Get the application key of the app under “Build and Manage my apps section” on txtWeb.com and include it in the message body list of the API call</p>
</td>
</tr>
<tr>
<td>
<p>-401</p>
</td>
<td>
<p>Incorrect application key<br> Check and verify the application key for the app under “Build and Manage my apps section” on txtWeb.com against the one you have sent in the API request call</p>
</td>
</tr>
<tr>
<td>
<p>-402</p>
</td>
<td>
<p>Maximum Throttle exceeded<br> No more than 5,000 API calls in a single day</p>
</td>
</tr>
<tr>
<td>
<p>-500</p>
</td>
<td>
<p>Mobile opted out<br> A mobile number has opted out from receiving any message from the app</p>
</td>
</tr>
<tr>
<td>
<p>-600</p>
</td>
<td>
<p>Missing message<br> Check if you have included the message to be sent in the right format</p>
</td>
</tr>
<tr>
<td>
<p>-700</p>
</td>
<td>
<p>Not a sandbox user<br> You are trying to push through an unpublished app. You need to register the mobile number in your txtweb.com account and then try the PUSH API.</p>
</td>
</tr>
</table>
 * @author Varun
 *
 */
@SuppressWarnings("serial")
public class Push extends HttpServlet {

	private String START_RESPONSE ="<?xml version=\"1.0\"><html><head><title>RESPONSE</title></head><body>";
	private String END_RESPONSE = "</body></html>";
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
	
	
	/**
	 * the do post method of the servlet   
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		doGet(req, resp);
	} 
	
	
	/**
     * This method makes the actual api call and is very very slow.
     * @param mobileHash the mobile Hash of the mobile number to whom the message is intended to.
	 * @param message The message that is to be sent to that mobile number.
     * @return
     */
    public  int sendPushMessage(String message, String mobileHash) {
        String head = "<html>"
            +"<head>"
            +"<meta name=\"txtweb-appkey\" content=\""+appKey+"\">"
            +"</head>"
            +"<body>";
        String tail = "</body></html>";
        String htmlMessage = head + message + tail;
        try{
            String urlParams =     "txtweb-message="+URLEncoder.encode(htmlMessage,"UTF-8")
            +"&txtweb-mobile="+URLEncoder.encode(mobileHash,"UTF-8")
            +"&txtweb-pubkey="+URLEncoder.encode(pubKey,"UTF-8");
            //Using DOM parser to parse the XML response from the API
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URLConnection conn = new URL("http://api.txtweb.com/v1/push").openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(urlParams);
            wr.flush();
            Document doc = db.parse(conn.getInputStream());
            NodeList statusNodes = doc.getElementsByTagName("status");
            String code = "-1";
            for(int index = 0; index < statusNodes.getLength(); index++)
            {
                Node childNode = statusNodes.item(index);
                if( childNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element element = (Element) childNode;
                    code = getTagValue("code", element);                    
                    return Integer.parseInt(code);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return -999; //APPLICATION ERROR
    }
   
    private static final String appKey = "64f42155-855a-4bef-9549-883c7da61a06";
    private static final String pubKey = "38fdd5e8-7c24-48ce-83fd-e63f6284f6ae";
    
    
    /**
	 * This method given a tag name and an element within which the tagname is present.(UPTO 1st Level)
	 * It returns the content ie INNERHTML of that tag.
	 * Very very Important: if the tag is at a final most refined level ie it is a bare element, then it will
	 * throw a NULLPOINTER EXCEPTION
	 * Reason for not handling it: something catastrophic has happened to cause this, there is no point handling it
	 * now. And the wise thing to do will be to terminate
	 * 
	 * @param tagName html tag to be searched within an html element
	 * @param element the html element within which the html tag is to be searched
	 * @return the innerHTML/ content of that tag
	 */    
    private  String getTagValue(String sTag, Element eElement) {
        NodeList nodeList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
	/**
	 * The do get method of the servlet
	 */
    @Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		 //Get all the messages that are due today
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String dateNow =com.placement.push.Timezone.getStringedDate();
		Integer index = new Integer(1);
		Key dueDatesKey;
		boolean notSuccessful = true;
		Query dueEntityQuery;
		List<Entity> usnsMessage ;
		String s = null;

		while(notSuccessful)
		{
			dueDatesKey = KeyFactory.createKey("AlertsDataStore", dateNow+index.toString());
			dueEntityQuery = new Query("AlertRequest", dueDatesKey);
			usnsMessage = datastore.prepare(dueEntityQuery).asList(FetchOptions.Builder.withLimit(2));
			String message = "";
			if(usnsMessage.isEmpty())
			{
				notSuccessful = false;
			}
			else{
				
				String usns = (String)usnsMessage.iterator().next().getProperty("usnlist");
				message =(String)usnsMessage.iterator().next().getProperty("message");
				String usnList[] = usns.split(";");
				for(String USN: usnList)
				{
					String mobileHash = getMobileHash(USN);
					if(mobileHash != null)
					s+= (USN +":"+sendPushMessage(message, mobileHash)+";");
				}
				++index;
				datastore.delete(usnsMessage.iterator().next().getKey());
			}
			
		}
		System.out.println("USN:STATUSCODE"+s);
		sendResponse(resp, s);
	}
    
    
    /**
     * Gets mobilehash for a particular usn
     * @param usn usn of the student
     * @return null if student has not verified else the mobilehash
     */
    private String getMobileHash(String usn) 
    {
		
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	Key usnKey = KeyFactory.createKey("MobileHashDataStore", usn);
    	Query mobileHash = new Query("MobileHash",usnKey);
    	List<Entity> mobileHashList = datastore.prepare(mobileHash).asList(FetchOptions.Builder.withLimit(2));
    	if(mobileHashList.size() == 1)
    	{
    		return (String)mobileHashList.iterator().next().getProperty("mobilehash");
    	}
    	
		return null;
	}
  
}

