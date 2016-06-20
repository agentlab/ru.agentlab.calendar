package ru.agentlab.calendar.app.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.ui.di.FXMLLoader;
import org.eclipse.fx.ui.di.FXMLLoaderFactory;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class CurrencyPart {
	@Inject
	@FXMLLoader
	FXMLLoaderFactory factory;
	
	public CurrencyPart() {
		System.out.println("Hello");
	}

	@PostConstruct
	void initUI(BorderPane pane) {
		try {
			@SuppressWarnings("deprecation")
			//Node node = (Node) factory.loadRequestorRelative("Currency.fxml");
					//.resourceBundle(ResourceBundle.getBundle("ru.agentlab.calendar.app")).load();
			Button button = new Button("Sasdasd");
			pane.setCenter(button);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
