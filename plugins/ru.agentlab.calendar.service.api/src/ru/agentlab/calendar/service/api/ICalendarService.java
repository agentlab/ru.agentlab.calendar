package ru.agentlab.calendar.service.api;

import java.util.List;

public interface ICalendarService {
	void addEvent(Event e) throws Exception;
	void deleteEvent(Event e) throws Exception;
	/**
	 * TODO JavaDoc
	 * @param id
	 *
	 * @return
	 * @throws Exception
	 */
	List<Event> getEvents(String id) throws Exception;
}
