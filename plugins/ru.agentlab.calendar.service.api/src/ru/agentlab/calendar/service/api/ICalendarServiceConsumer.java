package ru.agentlab.calendar.service.api;

public interface ICalendarServiceConsumer {
	public void onEventAdded(Event e);
	public void onEventDeleted(Event e);
}
