package ru.agentlab.tasks.service.google;

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
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

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
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import ru.agentlab.calendar.service.api.Calendar;
import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

@Component(immediate = true)
public class GoogleTaskServiceImpl implements ICalendarService {

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	ConcurrentHashMap<String, Calendar> calendars = new ConcurrentHashMap<String, Calendar>();

	private static FileDataStoreFactory dataStoreFactory;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	com.google.api.services.tasks.Tasks client;

	private static final String APPLICATION_NAME = "";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/tasks_sample");

	protected ArrayList<ICalendarServiceConsumer> consumersList = new ArrayList<>();

	private static Credential authorize() throws Exception {
		// load client secrets
		InputStreamReader is = new InputStreamReader(new FileInputStream("client_secrets.json"));//new InputStreamReader(GoogleServiceImpl.class.getResourceAsStream("client_secrets.json")); //$NON-NLS-1$
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, is);

		if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar " + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
			Collections.singleton(TasksScopes.TASKS))
			.setDataStoreFactory(dataStoreFactory)
			.setAccessType("offline")
            .build();
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	@Override
	public void addEvent(Event e) {
	}

	@Override
	public void deleteEvent(Event e) {
		return;
	}

	@Activate
	public void start() {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			Credential credential = authorize();
			client = (new Tasks.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)).build();

			TaskLists result = client.tasklists().list()
	             .setMaxResults(Long.valueOf(10))
	             .execute();

			ArrayList<Calendar> newCalendars = getAllCalendars();

			//notify consumers
			if(!newCalendars.isEmpty()) {
				for (Calendar calendar : newCalendars) {
					calendars.put(calendar.getId(), calendar);
				}
				for (ICalendarServiceConsumer consumer : consumersList) {
					consumer.onCalendarsAdded(newCalendars);
				}
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
	private void addConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.add(consumerService);
		if (!calendars.isEmpty()) {
			consumerService.onCalendarsAdded(calendars.values());
		}
	}

	private void removeConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.remove(consumerService);
	}

	@Deactivate
	private void stop() {
	}

	@Override
	public List<Event> getEvents(String calendarId) throws Exception {
		List<Event> events = new LinkedList<Event>();
		com.google.api.services.tasks.model.Tasks tasks = client.tasks().list(calendarId).setDueMin(toDateTime(LocalDateTime.now()).toStringRfc3339()).execute(); //;get("kozlovaira1@gmail.com","").execute();

		//com.google.api.services.tasks.model.Tasks tasks = client.tasks().list(calendarId).execute();
		LocalDateTime ldt;

		List<Task> items = tasks.getItems();
		if(items != null) {
			for (Task entry : items) {
				Event event = new Event(entry.getId(), entry.getTitle());
				event.setDescription(entry.getNotes());
				//event.setLocation(entry.());
				event.setCalendar(calendars.get(calendarId));

				DateTime startDate = entry.getDue();
				if (startDate != null) {
					ldt = toLocalDateTime(startDate);
					if (ldt != null) {
						event.setStartDateTime(ldt);
						event.setEndDateTime(ldt.plusHours(1));
					}
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

	protected LocalDateTime toLocalDateTime(DateTime dt) {
		if (dt != null) {
			long epochSecond = dt.getValue();
			int nanoOfSecond = (int)(epochSecond % 1000 * 1000);
			epochSecond /= 1000;
			LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, ZoneOffset.ofTotalSeconds(dt.getTimeZoneShift() * 60));
			return ldt;
		}
		return null;
	}

	protected ArrayList<Calendar> getAllCalendars() throws IOException {
		TaskLists feed = client.tasklists().list().execute();
		ArrayList<Calendar> calendars = new ArrayList<>();

		//retrieve existing calendars
		if (feed.getItems() != null) {
			for (TaskList entry : feed.getItems()) {
				calendars.add(new Calendar(entry.getId(), entry.getTitle(), entry.getKind(), this));
			}
		}
		return calendars;
	}
}
