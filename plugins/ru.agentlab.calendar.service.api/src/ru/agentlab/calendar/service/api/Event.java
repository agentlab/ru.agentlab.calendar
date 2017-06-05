package ru.agentlab.calendar.service.api;

import java.time.LocalDateTime;

public class Event {
	protected String id;
	protected String title;

	protected Calendar calendar;
	protected String description;

	protected LocalDateTime startDateTime;
	protected LocalDateTime endDateTime;
	protected String recurrence;

	protected String location;

	public Event(String description) {
		setDescription(description);
	}

	public Event(String id, String title) {
		setId(id);
		setTitle(title);
	}

	public String toString() {
		String s = new String("Event: "); //$NON-NLS-1$
		s += title + " " + startDateTime + " " + endDateTime + " " + recurrence; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return s;
	}

	/**
	 * @return the title
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param title the title to set
	 */
	public void setId(String id) {
		this.id = id;
	}/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return calendar;
	}
	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the startDate
	 */
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
	/**
	 * @return the endDate
	 */
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	/**
	 * @return the recurrence
	 */
	public String getRecurrence() {
		return recurrence;
	}
	/**
	 * @param recurrence the recurrence to set
	 */
	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
