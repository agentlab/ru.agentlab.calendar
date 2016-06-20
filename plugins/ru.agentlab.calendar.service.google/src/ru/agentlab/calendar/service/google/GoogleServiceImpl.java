package ru.agentlab.calendar.service.google;

import java.util.ArrayList;

import com.google.api.client.http.HttpTransport;

import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

public class GoogleServiceImpl implements ICalendarService {
	
	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;
	
	protected ArrayList<ICalendarServiceConsumer> consumersList = new ArrayList<>();
	  
	@Override
	public void addEvent(Event e) {
	}

	@Override
	public void deleteEvent(Event e) {
	}
	
	public void addConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.add(consumerService);
	}
	
	public void removeConsumer(ICalendarServiceConsumer consumerService) {
		consumersList.remove(consumerService);
	}
}
