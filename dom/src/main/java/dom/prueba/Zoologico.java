package dom.prueba;

import javax.annotation.PostConstruct;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Programmatic;

import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "zoounique", members = { "id" }) })
@javax.jdo.annotations.Queries({ @javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
		+ "FROM dom.prueba.Zoologico ") })
@ObjectType("ZOOLOGICO")
@Audited
@Bookmarkable
public class Zoologico {
	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////
	public Zoologico() {
		Delfin unD = new Delfin();
		Perro unP = new Perro();
		this.perro=unP;
		this.delfin = unD;
		this.ianimal=this.perro;
	}

	// {{ Perro (property)
	private Perro perro;

	@javax.jdo.annotations.Column(allowsNull = "true")
	@MemberOrder(sequence = "1")
	public Perro getPerro() {
		return perro;
	}

	public void setPerro(final Perro perro) {
		this.perro = perro;
	}

	// }}
	// {{ Delfin (property)
	private Delfin delfin;

	@javax.jdo.annotations.Column(allowsNull = "true")
	@MemberOrder(sequence = "1")
	public Delfin getDelfin() {
		return delfin;
	}

	public void setDelfin(final Delfin delfin) {
		this.delfin = delfin;
	}

	// }}

	public String title() {
		return "Zoo ";
	}

	public String iconName() {
		return "expediente";
	}

	// ********************************************
	private IAnimal ianimal;

	@MemberOrder(sequence = "1")
	@javax.jdo.annotations.Column(allowsNull = "true")
	// @Programmatic
	public IAnimal getIAnimal() {
		return ianimal;
	}

	public void setIAnimal(final IAnimal ianimal) {
		this.ianimal = ianimal;
	}

	// ********************************************
	public void modificarIAnimal(final IAnimal ianimal) {
		IAnimal currentIAnimal = getIAnimal();
		// check for no-op
		if (ianimal == null || ianimal.equals(currentIAnimal)) {
			return;
		}
		// associate new
		setIAnimal(ianimal);
		// additional business logic
	}

	public void limpiarIAnimal() {
		IAnimal currentIAnimal = getIAnimal();
		// check for no-op
		if (currentIAnimal == null) {
			return;
		}
		// dissociate existing
		setIAnimal(null);
		// additional business logic
	}

	// ********************************************

	// ********************************************
	@PostConstruct
	public Zoologico cambiarADelfin() {
		this.setIAnimal(new Delfin());
		return this;
	}
	@PostConstruct
	public Zoologico iniciar() {
		Perro perro = perroRepo.nuevo(this);
		this.setIAnimal(perro);
		return this;
	}

	@javax.inject.Inject
	private PerroRepo perroRepo;
	// ********************************************
	@javax.inject.Inject
	private ZoologicoRepo zooRepo;

	public void clearIAnimal() {
		// TODO Auto-generated method stub
		this.setIAnimal(null);
	}
}
