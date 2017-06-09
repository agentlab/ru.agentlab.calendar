package ru.agentlab.calendar.consumer.impl;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.google.common.collect.HashBiMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import ru.agentlab.calendar.consumer.ICalendarSourceProvider;
import ru.agentlab.calendar.service.api.Calendar;
import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;

@Component
public class CalendarConsumerImpl implements ICalendarSourceProvider {

	protected HashBiMap<ICalendarService, CalendarSource> calendarServicesSources = HashBiMap.create(1);

	protected ObservableList<CalendarSource> viewSources;


	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE)
	public void addCalendarService(ICalendarService service) {
		if(calendarServicesSources.containsKey(service)) {
			return;
		}

		try {
			CalendarSource fxCalendarSource = getAllEvents(service);
			calendarServicesSources.put(service, fxCalendarSource);

			if(viewSources != null) {
				runLater(() -> viewSources.addAll(fxCalendarSource));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeCalendarService(ICalendarService service) {
		CalendarSource fxCalendarSource = calendarServicesSources.remove(service);
		if(viewSources != null) {
			viewSources.removeAll(fxCalendarSource);
		}
	}

	public CalendarSource getAllEvents(ICalendarService service) throws Exception {
		Collection<Calendar> calendars = service.getCalendars();
		CalendarSource fxCalendarSource = new CalendarSource(service.toString());

		for (ru.agentlab.calendar.service.api.Calendar calendar : calendars) {
			com.calendarfx.model.Calendar fxCal = new com.calendarfx.model.Calendar();
			fxCal.setShortName(calendar.getId());
			String summary = calendar.getSummary();
			fxCal.setName(summary);
			fxCal.setStyle(Style.STYLE1);

			if(fxCalendarSource == null) {
				addCalendarService(service);
				fxCalendarSource = calendarServicesSources.get(service);
			}

			fxCal.addEventHandler(evt -> {
				if (evt.getEventType().equals(CalendarEvent.ENTRY_CHANGED)) {
					calendarServicesSources.values().stream().filter(src -> src.getCalendars().contains(calendar)).findFirst().ifPresent(src2 -> {
						ICalendarService service2 = calendarServicesSources.inverse().get(src2);

						Entry<?> entry = evt.getEntry();
						Event event = new Event(entry.getTitle());
						event.setStartDateTime(entry.getEndAsLocalDateTime());
						event.setEndDateTime(entry.getStartAsLocalDateTime());
						try {
							service2.addEvent(event);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			});

			try {
				List<Event> events = service.getEvents(calendar.getId());
				Collection<Entry<?>> entries = new LinkedList<>();

				for (Event event : events) {
					Entry<String> entry = new Entry<String>();
					entry.setTitle(event.getTitle());
					entry.setId(event.getId());
					entry.setLocation(event.getLocation());
					entry.setUserObject(event.getDescription());

					String recurrence = event.getRecurrence();
					if(recurrence != null && (!recurrence.contains("RDATE"))) { //$NON-NLS-1$
						entry.setRecurrenceRule(event.getRecurrence());
					}

					LocalDateTime startDateTime = event.getStartDateTime();
					LocalDateTime endDateTime = event.getEndDateTime();

					if((startDateTime != null) && (endDateTime != null)) {
						entry.setInterval(startDateTime, endDateTime);

						Period p = Period.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
						if(p.getDays() > 0)
							entry.setFullDay(true);
					}
					else {
						if(startDateTime != null) {
							entry.changeStartDate(startDateTime.toLocalDate());
							entry.changeStartTime(startDateTime.toLocalTime());
						}
						if(endDateTime != null) {
							entry.changeEndDate(endDateTime.toLocalDate());
							entry.changeEndTime(endDateTime.toLocalTime());
						}
					}
					entries.add(entry);
				}

				fxCal.addEntries(entries);

				CalendarSource fxCalendarSource2 = fxCalendarSource;
				Runnable r = () -> fxCalendarSource2.getCalendars().add(fxCal);

				if(viewSources != null ) {
					runLater(r);
				}
				else {
					r.run();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fxCalendarSource;
	}

	void runLater(Runnable r) {
		Platform.runLater(r);
	}

	@Override
	public void addCalendarSources(ObservableList<CalendarSource> calendarSources) {
		viewSources = calendarSources;
		for (CalendarSource fxCalendarSource : calendarServicesSources.values()) {
			runLater(() -> viewSources.addAll(fxCalendarSource));
		}
	}

	@Override
	public void removeCalendarSources(ObservableList<CalendarSource> calendarSources) {
		runLater(() -> calendarSources.clear());
	}
}
