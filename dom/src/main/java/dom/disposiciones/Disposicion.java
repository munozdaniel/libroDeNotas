package dom.disposiciones;

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
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nroDisposicion_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE destinoSector.indexOf(:destinoSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimaDisposicionTrue", language = "JDOQL", value = "SELECT MAX(nro_Disposicion) "
				+ "FROM dom.disposiciones.Disposicion "),
		@javax.jdo.annotations.Query(name = "buscarUltimaDisposicionFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE  habilitado == true"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechaSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion  "
				+ "WHERE  (habilitado == true) && (fecha==:fecha && sector==:sector)"),
		@javax.jdo.annotations.Query(name = "filtrarPorFecha", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE  (habilitado == true) && (fecha==:fecha)"),
		@javax.jdo.annotations.Query(name = "filtrarPorSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion  "
				+ "WHERE  (habilitado == true) && (sector==:sector)"),
		@javax.jdo.annotations.Query(name = "filtrarEntreFechas", language = "JDOQL", value = " SELECT  "
				+ "FROM dom.nota.Disposicion "
				+ "WHERE  fecha >= :desde && fecha<=:hasta  "),

		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Disposicion " + "WHERE  (ultimo == true)") })
@ObjectType("DISPOSICION")
@Audited
@AutoComplete(repository = DisposicionRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Disposicion extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Disposicion: " + this.getNro_Disposicion();
	}

	public String iconName() {
		if (this.getHabilitado())
			return "disposicion";
		else
			return "delete";
	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	private int nro_Disposicion;

	@Named("Nro")
	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getNro_Disposicion() {
		return nro_Disposicion;
	}

	public void setNro_Disposicion(int nro_Disposicion) {
		this.nro_Disposicion = nro_Disposicion;
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarDisposiciones();
	}

	@Named("Eliminar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public List<Disposicion> eliminar() {
		this.setHabilitado(false);
		return disposicionRepositorio.listar();
	}

	public boolean hideEliminar() {
		// TODO: return true if action is hidden, false if
		// visible
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	// @Named("Restaurar")
	// @DescribedAs("Necesario privilegios de Administrador.")
	// public Disposicion restaurar() {
	// this.setHabilitado(true);
	// return this;
	// }
	//
	// public boolean hideRestaurar() {
	// // TODO: return true if action is hidden, false if
	// // visible
	// if (this.container.getUser().isCurrentUser("root"))
	// return false;
	// else
	// return true;
	// }

	@javax.inject.Inject
	private DisposicionRepositorio disposicionRepositorio;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
}
