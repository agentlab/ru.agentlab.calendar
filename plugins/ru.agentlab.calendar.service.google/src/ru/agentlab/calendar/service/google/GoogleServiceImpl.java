package ru.agentlab.calendar.service.google;

import com.google.api.client.http.HttpTransport;

import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;

public class GoogleServiceImpl implements ICalendarService{
	
	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;
	  
	@Override
	public void addEvent(Event e) {
	}

	@Override
	public void deleteEvent(Event e) {
	}
}
