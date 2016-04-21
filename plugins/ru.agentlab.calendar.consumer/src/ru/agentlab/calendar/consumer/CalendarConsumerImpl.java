package ru.agentlab.calendar.consumer;

import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

public class CalendarConsumerImpl implements ICalendarServiceConsumer {

	@Override
	public void onEventAdded(Event e) {
	}

	@Override
	public void onEventDeleted(Event e) {
	}

}
