package ru.agentlab.calendar.consumer.impl;

import java.time.LocalDateTime;
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
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.google.common.collect.HashBiMap;

import javafx.application.Platform;
import ru.agentlab.calendar.consumer.ICalendarSourceProvider;
import ru.agentlab.calendar.service.api.Calendar;
import ru.agentlab.calendar.service.api.Event;
import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

@Component
public class CalendarConsumerImpl implements ICalendarServiceConsumer, ICalendarSourceProvider {

	protected HashBiMap<ICalendarService, CalendarSource> calendarServicesSources = HashBiMap.create(1);

	protected CalendarView view;

	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE)
	public void addCalendarService(ICalendarService service) {
		if(calendarServicesSources.containsKey(service)) {
			if(view != null) {
				CalendarSource fxCalendarSource = calendarServicesSources.get(service);
				if(!view.getCalendarSources().contains(fxCalendarSource)) {
					runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
				}
			}
			return;
		}

		CalendarSource fxCalendarSource = new CalendarSource(service.toString());
		calendarServicesSources.put(service, fxCalendarSource);
		if(view != null) {
			runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}

	public void removeCalendarService(ICalendarService service) {
		CalendarSource fxCalendarSource = calendarServicesSources.remove(service);
		if(view != null) {
			view.getCalendarSources().removeAll(fxCalendarSource);
		}
	}

	@Override
	public void onCalendarsAdded(Collection<Calendar> calendars) {
		for (ru.agentlab.calendar.service.api.Calendar calendar : calendars) {
			com.calendarfx.model.Calendar fxCal = new com.calendarfx.model.Calendar();
			fxCal.setShortName(calendar.getId());
			String summary = calendar.getSummary();
			fxCal.setName(summary);
			fxCal.setStyle(Style.STYLE1);

			ICalendarService calendarService = calendar.getSourceService();

			CalendarSource fxCalendarSource1 = calendarServicesSources.get(calendarService);
			if(fxCalendarSource1 == null) {
				addCalendarService(calendarService);
				fxCalendarSource1 = calendarServicesSources.get(calendarService);
			}

			CalendarSource fxCalendarSource = fxCalendarSource1;

			fxCal.addEventHandler(evt -> {
				if (evt.getEventType().equals(CalendarEvent.ENTRY_CHANGED)) {
					calendarServicesSources.values().stream().filter(src -> src.getCalendars().contains(calendar)).findFirst().ifPresent(src2 -> {
						ICalendarService service = calendarServicesSources.inverse().get(src2);

						Entry<?> entry = evt.getEntry();
						Event event = new Event(entry.getTitle());
						event.setStartDateTime(entry.getEndAsLocalDateTime());
						event.setEndDateTime(entry.getStartAsLocalDateTime());
						try {
							service.addEvent(event);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			});

			try {
				List<Event> events = calendarService.getEvents(calendar.getId());
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

				Runnable r = () -> fxCalendarSource.getCalendars().add(fxCal);

				if(view != null ) {
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
	}

	void runLater(Runnable r) {
		Platform.runLater(r);
	}

	@Override
	public void onEventAdded(Event e) {
	}

	@Override
	public void onEventDeleted(Event e) {
	}

	@Override
	public void addView(DateControl view) {
		for (CalendarSource fxCalendarSource : calendarServicesSources.values()) {
			runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}

	@Override
	public void removeView(DateControl view) {
		for (CalendarSource fxCalendarSource : calendarServicesSources.values()) {
			runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}
}
