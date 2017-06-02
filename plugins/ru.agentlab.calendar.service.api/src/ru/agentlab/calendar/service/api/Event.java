package ru.agentlab.calendar.service.api;

import java.util.Date;

public class Event {
	protected String id;
	protected String title;

	protected Calendar calendar;
	protected String description;

	protected Date startDate;
	protected Date endDate;

	public Event(String description) {
		setDescription(description);
	}

	public Event(String id, String description) {
		setId(id);
		setDescription(description);
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
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
