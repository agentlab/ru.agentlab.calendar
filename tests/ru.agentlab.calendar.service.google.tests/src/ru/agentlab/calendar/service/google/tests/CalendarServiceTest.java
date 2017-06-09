package ru.agentlab.calendar.service.google.tests;

import static com.codeaffine.osgi.test.util.ServiceCollector.collectServices;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.calendarfx.model.CalendarSource;
import com.codeaffine.osgi.test.util.Registration;
import com.codeaffine.osgi.test.util.ServiceRegistrationRule;

import javafx.collections.ObservableList;
import ru.agentlab.calendar.consumer.ICalendarSourceProvider;
import ru.agentlab.calendar.consumer.impl.CalendarConsumerImpl;
import ru.agentlab.calendar.service.api.Calendar;
import ru.agentlab.calendar.service.api.ICalendarService;

/**
 * OSGi Declarative Services unit tests example based on
 *
 * http://www.codeaffine.com/2015/02/11/osgi-service-test-helper-serviceregistrationrule/
 * http://www.codeaffine.com/2015/02/05/osgi-service-test-helper-servicecollector/
 *
 * @author amivanoff
 *
 */
public class CalendarServiceTest {
	@Rule
	public final ServiceRegistrationRule serviceRegistration = new ServiceRegistrationRule(getClass());

	private ICalendarSourceProvider consumer;
	private ICalendarService service;
	private ObservableList<CalendarSource> viewSources;

	@Before
	public void setUp() {
		//get specific service implementation from SCR
		//(this is Fragment, so we have access to the Host internal packages with implementation classes)
		consumer = collectServices(ICalendarSourceProvider.class, CalendarConsumerImpl.class).get(0);
	}

	@Test
	public void executeNotification() throws Exception {
		//create two mocked objects with Mockito from interface and abstract class
		service = mock(ICalendarService.class);
		viewSources = mock(ObservableList.class);

		//define return value for method getCalendars()
		List<Calendar> calendars = new ArrayList<Calendar>();
		calendars.add(new Calendar("sdfs-234", "summary", "description", service));
        when(service.getCalendars()).thenReturn(calendars);

		//add mocked object
		consumer.addCalendarSources(viewSources);

		//register mocked object as a component in SCR
		Registration<?> registration = serviceRegistration.register(ICalendarService.class, service);

		assertEquals(calendars.get(0).getId(), viewSources.get(0).getCalendars().get(0).getShortName());
		//verify(viewSources).get(0).getCalendars().get(0).onEventAdded(event);

		registration.unregister();
	}

//	@Test
//	public void executeAfterListenerRemoval() throws Exception {
//		Registration<?> registration = serviceRegistration.register(ICalendarService.class, service);
//		registration.unregister();
//		service.addEvent(null);
//		verify(consumer, never()).onEventAdded(null);
//
//		registration.unregister();
//	}
}
