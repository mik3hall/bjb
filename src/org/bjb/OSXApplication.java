package org.bjb;

import javax.swing.JFrame;

import com.apple.eawt.*;

public class OSXApplication {
	
	private Application application = Application.getApplication();
	static final Preferences preferences = new Preferences(new JFrame(""),true);
	
	@SuppressWarnings("deprecation")
	public OSXApplication() {
		application.setEnabledPreferencesMenu(true);

		application.addApplicationListener(new ApplicationAdapter() {
			public void handlePreferences(final ApplicationEvent e) {
				OSXApplication.handlePreferences();
			}
			
			public void handleQuit(final ApplicationEvent e) {
				System.exit(0);
			}
		});
	}
	
	static void handlePreferences() {
		preferences.setVisible(true);		
	}
}