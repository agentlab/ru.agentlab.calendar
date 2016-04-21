package ru.agentlab.calendar.service.google.tests;

import static com.codeaffine.osgi.test.util.ServiceCollector.collectServices;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.osgi.test.util.Registration;
import com.codeaffine.osgi.test.util.ServiceRegistrationRule;

import ru.agentlab.calendar.service.api.ICalendarService;
import ru.agentlab.calendar.service.api.ICalendarServiceConsumer;
import ru.agentlab.calendar.service.google.GoogleServiceImpl;

public class CalendarServiceTest {
	@Rule
	public final ServiceRegistrationRule serviceRegistration = new ServiceRegistrationRule(getClass());

	private ICalendarServiceConsumer consumer;
	private ICalendarService service;

	@Before
	public void setUp() {
		service = collectServices(ICalendarService.class, GoogleServiceImpl.class).get(0);
		consumer = mock(ICalendarServiceConsumer.class);
	}

	@Test
	public void executeNotification() {
		assertTrue("True is OK", true);

		serviceRegistration.register(ICalendarServiceConsumer.class, consumer);
		service.addEvent(null);
		verify(consumer).addEvent(null);
	}

	@Test
	public void executeAfterListenerRemoval() {
		Registration registration = serviceRegistration.register(ICalendarServiceConsumer.class, consumer);
		registration.unregister();
		service.addEvent(null);
		verify(consumer, never()).addEvent(null);
	}
}
