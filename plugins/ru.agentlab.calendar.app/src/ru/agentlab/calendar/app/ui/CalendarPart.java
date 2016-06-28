package ru.agentlab.calendar.app.ui;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.core.di.Service;
import org.eclipse.fx.ui.di.FXMLLoader;
import org.eclipse.fx.ui.di.FXMLLoaderFactory;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.page.WeekPage;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;

public class CalendarPart {
	@Inject
	@FXMLLoader
	FXMLLoaderFactory factory;
	
	  @Inject
	  @Service
	  private List<ICalendarServiceConsumer> consumersList; // all instances available and reinject when services are added/removed
	
	public CalendarPart() {
		System.out.println("Hello");
	}

	@PostConstruct
	void initUI(BorderPane pane) {
		try {
			Node node = initWeek();
			pane.setCenter(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Node initWeek() {
		Calendar cal = new Calendar("Birthdays");
		cal.setShortName("K");
		cal.setStyle(Style.STYLE1);
			
		CalendarSource myCalendarSource = new CalendarSource("Family");
		myCalendarSource.getCalendars().clear();
		myCalendarSource.getCalendars().add(cal);
		
		EventHandler<CalendarEvent> handler = evt -> foo(evt);
		myCalendarSource.getCalendars().forEach(c -> c.addEventHandler(handler));
		
		WeekPage view = new WeekPage();
		view.getCalendarSources().setAll(myCalendarSource);
		view.setRequestedTime(LocalTime.now());
		view.setWeekFields(WeekFields.of(DayOfWeek.SUNDAY, 5));
		return view;
	}
	
	Node initCal() {
		Calendar cal = new Calendar("Katja");
        cal.setShortName("K");
		cal.setStyle(Style.STYLE1);
		
		CalendarSource myCalendarSource = new CalendarSource("Family");
		myCalendarSource.getCalendars().addAll(cal);
		
		EventHandler<CalendarEvent> handler = evt -> foo(evt);
		myCalendarSource.getCalendars().forEach(c -> c.addEventHandler(handler));
		
		CalendarView view = new CalendarView();
		view.getCalendarSources().setAll(myCalendarSource);
		view.setRequestedTime(LocalTime.now());
		view.setWeekFields(WeekFields.of(DayOfWeek.SUNDAY, 5));          
        return view;
	}
	
	private Object foo(CalendarEvent evt) {
		return null;
	}
}
