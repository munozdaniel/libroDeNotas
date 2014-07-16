package dom.dashboard;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.HomePage;

@Hidden
public class NotaDashboardService {
	private static final String ID = "dashboard";

	public String getId() {
		return ID;
	}

	public String iconName() {
		return ID;
	}

	// //////////////////////////////////////
	@ActionSemantics(Of.SAFE)
	@HomePage
	public NotaDashboard lookup() {
		return container.newViewModelInstance(NotaDashboard.class, ID);
	}

	// //////////////////////////////////////
	// Injected services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;
}
