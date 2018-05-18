package ru.agentlab.calendar.service.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

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
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import ru.agentlab.calendar.service.api.Calendar;
import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;

@Component(immediate=true)
public class GoogleServiceImpl implements ICalendarService {

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	private static FileDataStoreFactory dataStoreFactory;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static com.google.api.services.calendar.Calendar client;

	private static final String APPLICATION_NAME = "";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

	private static Credential authorize() throws Exception {
		// load client secrets
		InputStreamReader is = new InputStreamReader(new FileInputStream("client_secrets.json"));//new InputStreamReader(GoogleServiceImpl.class.getResourceAsStream("client_secrets.json")); //$NON-NLS-1$
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, is);

		if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar " + "into working dir with name client_secrets.json");
			System.exit(1);
		}

		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory).build();

		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	@Override
	public void addEvent(Event e) {
		com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
		event.setSummary(e.getTitle());

		DateTime start = toDateTime(e.getStartDateTime());
		DateTime end = toDateTime(e.getEndDateTime());

		event.setStart(new EventDateTime().setDateTime(start));
		event.setEnd(new EventDateTime().setDateTime(end));
		try {
			com.google.api.services.calendar.model.Event result = client.events().insert(e.getCalendarId(), event).execute();
		}
		catch (IOException evt) {
			System.err.println(evt.getMessage());
		}
	}

	@Override
	public void deleteEvent(Event e) {
		try {
			client.events().delete(e.getCalendarId(), e.getId()).execute();
		}
		catch (IOException evt) {
			System.err.println(evt.getMessage());
		}
	}

	@Activate
	private void start() {
		try {
			// initialize the transport
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			// initialize the data store factory
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// authorization
			Credential credential = authorize();

			// set up global Calendar instance
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Deactivate
	private void stop() {
	}

	@Override
	public List<Calendar> getCalendars() throws Exception {
		CalendarList feed = client.calendarList().list().execute();
		ArrayList<Calendar> calendars = new ArrayList<>();

		//retrieve existing calendars
		if (feed.getItems() != null) {
			for (CalendarListEntry entry : feed.getItems()) {
				calendars.add(new Calendar(entry.getId(), entry.getSummary(), entry.getDescription(), this));
			}
		}
		return calendars;
	}

	@Override
	public List<Event> getEvents(String calendarId) throws IOException {
		List<Event> events = new LinkedList<Event>();
		Events feed = client.events().list(calendarId)
            .setTimeMin(toDateTime(LocalDateTime.
            	now()
            //	.minusMonths(1)
            ))
            .execute();
		LocalDateTime ldt;

		if (feed.getItems() != null) {
			for (com.google.api.services.calendar.model.Event entry : feed.getItems()) {
				Event event = new Event(entry.getId(), entry.getSummary());
				event.setDescription(entry.getDescription());
				event.setLocation(entry.getLocation());
				event.setCalendarId(calendarId);

				EventDateTime startDate = entry.getStart();
				if(startDate != null) {
					ldt = toLocalDateTime(startDate);
					if(ldt != null) {
						event.setStartDateTime(ldt);
					}
				}
				EventDateTime endDate = entry.getEnd();
				if(endDate != null) {
					ldt = toLocalDateTime(endDate);
					if(ldt != null) {
						event.setEndDateTime(ldt);
					}
				}
				List<String> recurrence = entry.getRecurrence();
				if(recurrence != null) {
					String rec = ""; //$NON-NLS-1$
					for (String r : recurrence) {
						rec += r;
					}
					event.setRecurrence(rec);
				}
				events.add(event);
			}
		}
		return events;
	}



	private DateTime toDateTime(LocalDateTime ldt) {
		Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		DateTime start = new DateTime(d, TimeZone.getDefault());
		return start;
	}

	protected LocalDateTime toLocalDateTime(EventDateTime edt) {
		DateTime dt = edt.getDateTime();
		if (dt == null) {
			dt = edt.getDate();
		}
		if (dt != null) {
			long epochSecond = dt.getValue();
			int nanoOfSecond = (int)(epochSecond % 1000 * 1000);
			epochSecond /= 1000;
			LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, ZoneOffset.ofTotalSeconds(dt.getTimeZoneShift() * 60));
			return ldt;
		}
		return null;
	}
}
