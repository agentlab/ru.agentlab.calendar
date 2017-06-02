/**
 *
 */
package ru.agentlab.calendar.consumer;

import com.calendarfx.view.DateControl;

/**
 * @author Ivanov_AM
 *
 */
public interface ICalendarSourceProvider {

	/**
	 * TODO JavaDoc
	 *
	 * @param view
	 */
	void addView(DateControl view);

	void removeView(DateControl view);

}
