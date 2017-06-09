/**
 *
 */
package ru.agentlab.calendar.consumer;

import com.calendarfx.model.CalendarSource;

import javafx.collections.ObservableList;

/**
 * @author Ivanov_AM
 *
 */
public interface ICalendarSourceProvider {

	void addCalendarSources(ObservableList<CalendarSource> calendarSources);

	void removeCalendarSources(ObservableList<CalendarSource> calendarSources);
}
