package dom.resoluciones;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nro_resolucion_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.resoluciones.resoluciones "
				+ "WHERE sector.getNombre_sector().indexOf(:nombreSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimaResolucionTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.resoluciones.resoluciones "
				+ "WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarUltimaResolucionFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.resoluciones.resoluciones "
				+ "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.resoluciones.resoluciones "
				+ "WHERE  habilitado == true"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.resoluciones.resoluciones  "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechaSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (fecha==:fecha && sector==:sector)"),
		@javax.jdo.annotations.Query(name = "filtrarPorFecha", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (fecha==:fecha)"),
		@javax.jdo.annotations.Query(name = "filtrarPorSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (sector==:sector)") })
@ObjectType("RESOLUCIONES")
@Audited
@AutoComplete(repository = ResolucionesRepositorio.class, action = "autoComplete")
//
@Bookmarkable
public class Resoluciones extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "RESOLUCION Nº " + this.getNro_resolucion();
	}

	public String iconName() {
		if (this.getHabilitado())
			return "resolucion";
		else
			return "delete";
	}

	private int nro_resolucion;

	@Named("Nro")
	@MemberOrder(sequence = "0")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getNro_resolucion() {
		return nro_resolucion;
	}

	public void setNro_resolucion(int nro_resolucion) {
		this.nro_resolucion = nro_resolucion;
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarResoluciones();
	}

	@Named("Eliminar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public List<Resoluciones> eliminar() {
		this.setHabilitado(false);
		return resolucionRepositorio.listar();
	}

	public boolean hideEliminar() {
		// TODO: return true if action is hidden, false if
		// visible
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@Named("Restaurar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public Resoluciones restaurar() {
		this.setHabilitado(true);
		return this;
	}

	public boolean hideRestaurar() {
		// TODO: return true if action is hidden, false if
		// visible
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@javax.inject.Inject
	private ResolucionesRepositorio resolucionRepositorio;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
}
