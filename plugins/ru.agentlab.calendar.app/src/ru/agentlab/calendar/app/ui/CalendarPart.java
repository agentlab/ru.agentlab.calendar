package ru.agentlab.calendar.app.ui;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.fx.core.di.Service;

import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.page.WeekPage;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import ru.agentlab.calendar.consumer.ICalendarSourceProvider;

public class CalendarPart {
	/**
	 * all instances available and reinject when services are added/removed
	 */
	@Inject
	@Service
	protected List<ICalendarSourceProvider> calendarServices;

	protected DateControl view;

	@PostConstruct
	void initUI(BorderPane pane) {
		try {
			Node node = initCal();
			pane.setCenter(node);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	void destroyUI() {
		if(calendarServices != null) {
			for (ICalendarSourceProvider provider : calendarServices) {
				provider.removeCalendarSources(view.getCalendarSources());
			}
		}
	}

	Node initWeek() {
		view = new WeekPage();
		if(calendarServices != null) {
			for (ICalendarSourceProvider provider : calendarServices) {
				provider.addCalendarSources(view.getCalendarSources());
			}
		}
		view.setRequestedTime(LocalTime.now());
		view.setWeekFields(WeekFields.of(DayOfWeek.SUNDAY, 5));
		return view;
	}

	Node initCal() {
		view = new CalendarView();
		((CalendarView)view).setTransitionsEnabled(true);
		if(calendarServices != null) {
			for (ICalendarSourceProvider provider : calendarServices) {
				provider.addCalendarSources(view.getCalendarSources());
			}
		}
		view.setRequestedTime(LocalTime.now());
		view.setWeekFields(WeekFields.of(DayOfWeek.SUNDAY, 5));
		return view;
	}
}
