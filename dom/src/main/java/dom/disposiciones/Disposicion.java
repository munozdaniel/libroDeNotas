package dom.disposiciones;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
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
				+ "WHERE  habilitado == true  ORDER BY fecha DESC, nro_Disposicion DESC"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion ORDER BY fecha DESC, nro_Disposicion DESC"),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion " + "WHERE  (ultimo == true)"),
		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_Disposicion DESC ") })
@ObjectType("DISPOSICION")
@Audited
@AutoComplete(repository = DisposicionRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Disposicion extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Disposicion Nº " + String.format("%03d",this.getNro_Disposicion());
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
	@Disabled
	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Hidden
	public int getNro_Disposicion() {
		return nro_Disposicion;
	}

	public void setNro_Disposicion(int nro_Disposicion) {
		this.nro_Disposicion = nro_Disposicion;
	}
	
	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;
	@Disabled
	@MemberOrder(sequence = "0")
	@Named("Nro")
	@NotPersisted
	public String getNro() {
		return 	String.format("%03d",this.getNro_Disposicion());
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
