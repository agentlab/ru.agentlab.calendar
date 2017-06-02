/**
 *
 */
package ru.agentlab.calendar.service.api;

/**
 * @author Ivanov_AM
 *
 */
public class Calendar {
	protected String id;
	protected String summary;
	protected String description;
	protected ICalendarService sourceService;

	public Calendar(String id, String summary, String description, ICalendarService service) {
		setId(id);
		setSummary(summary);
		setDescription(description);
		setSourceService(service);
	}

	/**
	 * @return the name
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param name the name to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
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
	 * @return the sourceService
	 */
	public ICalendarService getSourceService() {
		return sourceService;
	}

	/**
	 * @param sourceService the sourceService to set
	 */
	public void setSourceService(ICalendarService sourceService) {
		this.sourceService = sourceService;
	}

}
