package ru.agentlab.calendar.service.api;

import java.util.List;

public interface ICalendarServiceConsumer {
	public void onCalendarsAdded(List<Calendar> c);

	public void onEventAdded(Event e);
	public void onEventDeleted(Event e);
}
