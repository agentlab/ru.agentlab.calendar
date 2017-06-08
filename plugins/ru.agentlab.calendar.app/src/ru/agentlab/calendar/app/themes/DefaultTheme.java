package ru.agentlab.calendar.app.themes;

import org.eclipse.fx.ui.theme.AbstractTheme;

public class DefaultTheme extends AbstractTheme {
	public DefaultTheme() {
		super("theme.default", "Default theme", DefaultTheme.class.getClassLoader().getResource("css/default.css"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}
}
