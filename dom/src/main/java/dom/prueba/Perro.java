package dom.prueba;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "idP")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "perrounique", members = { "idP" }) })
@ObjectType("PERRO")
@Audited
@Bookmarkable
public class Perro implements IAnimal {
	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Perro ";
	}

	public String iconName() {
		return "expediente";
	}

	@Override
	public String accion() {
		// TODO Auto-generated method stub
		return "PERRO: CORRE.";
	}

	// {{ Zoologico (Collection)

	@Element(column = "perrito", dependent = "False")
	private SortedSet<Zoologico> collectionName = new TreeSet<Zoologico>();

	@MemberOrder(sequence = "1")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public SortedSet<Zoologico> getZoologico() {
		return collectionName;
	}

	public void setZoologico(final SortedSet<Zoologico> collectionName) {
		this.collectionName = collectionName;
	}
	public void addToZoologico(final Zoologico zoologico) {
		// check for no-op
		if (zoologico == null
				|| getZoologico().contains(zoologico)) {
			return;
		}
		// dissociate arg from its current parent (if any).
		zoologico.clearIAnimal();
		// associate arg
		zoologico.setIAnimal(this);
		getZoologico().add(zoologico);
		// additional business logic
	}

	public void removeFromZoologico(
			final Zoologico zoologico) {
		// check for no-op
		if (zoologico == null
				|| !getZoologico().contains(zoologico)) {
			return;
		}
		// dissociate arg
		zoologico.setIAnimal(null);
		getZoologico().remove(zoologico);
		// additional business logic
	}
	// }}
	// ********************************************
	// {{ Zoologico (Collection)
	// @Persistent(mappedBy = "ianimal", dependentElement = "false")
	// @Join
	// private SortedSet<Zoologico> zologico = new TreeSet<Zoologico>();
	//
	// @MemberOrder(sequence = "1")
	// @javax.jdo.annotations.Column(allowsNull = "true")
	// public SortedSet<Zoologico> getZoologico() {
	// return zologico;
	// }
	//
	// public void setZoologico(final SortedSet<Zoologico> zologico) {
	// this.zologico = zologico;
	// }
	// }}
	// ********************************************

}
