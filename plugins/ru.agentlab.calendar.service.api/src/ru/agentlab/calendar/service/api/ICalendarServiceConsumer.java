package ru.agentlab.calendar.service.api;

public interface ICalendarServiceConsumer {
	public void addEvent(Event e);
	public void deleteEvent(Event e);
}
