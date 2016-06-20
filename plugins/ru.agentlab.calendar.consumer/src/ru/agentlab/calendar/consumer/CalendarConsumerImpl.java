package ru.agentlab.calendar.consumer;

import java.util.ArrayList;

import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

public class CalendarConsumerImpl implements ICalendarServiceConsumer {
	
	protected ArrayList<ICalendarService> calendarsList = new ArrayList<>();

	@Override
	public void onEventAdded(Event e) {
	}

	@Override
	public void onEventDeleted(Event e) {
	}

	public void addCalendar(ICalendarService service) {
		calendarsList.add(service);
	}
	
	public void removeCalendar(ICalendarService service) {
		calendarsList.remove(service);
	}
}
