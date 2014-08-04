package dom.prueba;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nroExpediente_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({ @javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
		+ "FROM dom.prueba.Zoologico ") })
@ObjectType("ZOOLOGICO")
@Audited
@Bookmarkable
public class Zoologico {
	// {{ IAnimal (property)
	private IAnimal ianimal;

	@MemberOrder(sequence = "1")
	public IAnimal getIAnimal() {
		return ianimal;
	}

	public void setIAnimal(final IAnimal ianimal) {
		this.ianimal = ianimal;
	}

	// }}
	public Zoologico cambiarADelfin() {
		return zooRepo.cambiar(this);
	}
	@javax.inject.Inject
	private ZoologicoRepo zooRepo;
}
