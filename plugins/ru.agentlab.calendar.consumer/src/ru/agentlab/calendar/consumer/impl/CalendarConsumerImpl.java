package ru.agentlab.calendar.consumer.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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

	@Reference(policy=ReferencePolicy.DYNAMIC)
	public void addCalendarService(ICalendarService service) {
		CalendarSource fxCalendarSource = new CalendarSource(service.toString());
		calendarServicesSources.put(service, fxCalendarSource);
		if(view != null) {
			Platform.runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}

	public void removeCalendarService(ICalendarService service) {
		CalendarSource fxCalendarSource = calendarServicesSources.remove(service);
		if(view != null) {
			view.getCalendarSources().removeAll(fxCalendarSource);
		}
	}

	@Override
	public void onCalendarsAdded(List<Calendar> calendars) {
		for (ru.agentlab.calendar.service.api.Calendar calendar : calendars) {
			com.calendarfx.model.Calendar fxCal = new com.calendarfx.model.Calendar();
			fxCal.setShortName(calendar.getId());
			fxCal.setName(calendar.getDescription());
			fxCal.setStyle(Style.STYLE1);

			ICalendarService calendarService = calendar.getSourceService();
			CalendarSource fxCalendarSource = calendarServicesSources.get(calendarService);

			fxCal.addEventHandler(evt -> {
				if (evt.getEventType().equals(CalendarEvent.ENTRY_CHANGED)) {
					calendarServicesSources.values().stream().filter(src -> src.getCalendars().contains(calendar)).findFirst().ifPresent(src2 -> {
						ICalendarService service = calendarServicesSources.inverse().get(src2);

						Event event = new Event(evt.getEntry().getTitle());
						LocalDate startLocalDate = evt.getEntry().getStartDate();
						event.setStartDate(Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						LocalDate endLocalDate = evt.getEntry().getEndDate();
						event.setEndDate(Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
					Entry entry = new Entry();

					entry.setTitle(event.getTitle());
					ZoneId defaultZoneId = ZoneId.systemDefault();

					Date startDate = event.getStartDate();
					if(startDate != null) {
						Instant startInstant = startDate.toInstant();
						LocalDate startLocalDate = startInstant.atZone(defaultZoneId).toLocalDate();
						entry.changeStartDate(startLocalDate);
					}

					Date endDate = event.getEndDate();
					if(endDate != null) {
						Instant endInstant = endDate.toInstant();
						LocalDate endLocalDate = endInstant.atZone(defaultZoneId).toLocalDate();
						entry.changeEndDate(endLocalDate);
					}

					entries.add(entry);
				}

				fxCal.addEntries(entries);

				Platform.runLater(() -> fxCalendarSource.getCalendars().add(fxCal));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
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
			Platform.runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}

	@Override
	public void removeView(DateControl view) {
		for (CalendarSource fxCalendarSource : calendarServicesSources.values()) {
			Platform.runLater(() -> view.getCalendarSources().setAll(fxCalendarSource));
		}
	}
}
