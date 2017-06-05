package ru.agentlab.calendar.service.api;

import java.util.Collection;

public interface ICalendarServiceConsumer {
	public void onCalendarsAdded(Collection<Calendar> c);

	public void onEventAdded(Event e);
	public void onEventDeleted(Event e);
}
