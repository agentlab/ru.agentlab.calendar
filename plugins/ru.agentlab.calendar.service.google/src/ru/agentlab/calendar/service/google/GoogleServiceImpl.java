package ru.agentlab.calendar.service.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.EventDateTime;

import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

public class GoogleServiceImpl implements ICalendarService {
	
	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;
	
	private static Calendar calendar;
	
	private static FileDataStoreFactory dataStoreFactory;
	
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static com.google.api.services.calendar.Calendar client;
	
	private static final String APPLICATION_NAME = "";
	
	private static final java.io.File DATA_STORE_DIR =
		      new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");
	
	protected ArrayList<ICalendarServiceConsumer> consumersList = new ArrayList<>();
	
	private static Credential authorize() throws Exception {
	    // load client secrets
	    FileInputStream fis = new FileInputStream("client_secrets.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(fis));//GoogleServiceImpl.class.getResourceAsStream("/client_secrets.json")));
	    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
	        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
	      System.out.println(
	          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
	          + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
	      System.exit(1);
	    }
	    // set up authorization code flow
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	        httpTransport, JSON_FACTORY, clientSecrets,
	        Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
	        .build();
	    // authorize
	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	  }
	  
	@Override
	public void addEvent(Event e) {
		com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
		event.setSummary(e.title);
		DateTime start = new DateTime(e.startDate, TimeZone.getTimeZone("UTC"));
		DateTime end = new DateTime(e.endDate, TimeZone.getTimeZone("UTC"));
	    event.setStart(new EventDateTime().setDateTime(start));
	    event.setEnd(new EventDateTime().setDateTime(end));
		try {
			com.google.api.services.calendar.model.Event result = client.events().insert(calendar.getId(), event).execute();
		}catch(IOException evt){
	        System.err.println(evt.getMessage());
	    }
	}

	@Override
	public void deleteEvent(Event e) {
		/*try {
		client.events().delete(calendar.getId(), e.id).execute();
		}catch(IOException evt){
	        System.err.println(evt.getMessage());
	    }*/
	}
	
	public void start() {
	    try {
	        // initialize the transport
	        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	  
	        // initialize the data store factory
	        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	  
	        // authorization
	        Credential credential = authorize();
	  
	        // set up global Calendar instance
	        client = new com.google.api.services.calendar.Calendar.Builder(
	            httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	        
	        calendar = client.calendars().get("a.a.aleksandrovskaya@gmail.com").execute();	
	       
	      } catch (IOException e) {
	        System.err.println(e.getMessage());
	      } catch (Throwable t) {
	        t.printStackTrace();
	      }
	}

	public void addConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.add(consumerService);
	}
	
	public void removeConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.remove(consumerService);
	}
}
