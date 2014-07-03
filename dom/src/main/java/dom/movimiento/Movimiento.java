package dom.movimiento;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

import dom.computadora.Computadora;
import dom.computadora.ComputadoraRepositorio;
import dom.tecnico.Tecnico;
import dom.tecnico.TecnicoRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
// @javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name =
// "Sector_nombreSector_must_be_unique", members = {
// "creadoPor", "nombreSector" }) })
@javax.jdo.annotations.Queries({
	@javax.jdo.annotations.Query(name = "autoCompletePorMovimiento", language = "JDOQL", value = "SELECT "
			+ "FROM dom.movimiento.Movimiento "
			+ "WHERE creadoPor == :creadoPor && " + "tecnico.getNombre().indexOf(:buscarTecnico) >= 0"),
	@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
			+ "FROM dom.movimiento.Movimiento "
			+ "WHERE ingresadoPor == :ingresadoPor "
			+ "   && habilitado == true"),
	@javax.jdo.annotations.Query(name = "buscarPorIp", language = "JDOQL", value = "SELECT "
			+ "FROM dom.movimiento.Movimiento "
			+ "WHERE ingresadoPor == :ingresadoPor "
			+ "   && computadora.getIp().indexOf(:ip) >= 0"), })
@ObjectType("MOVIMIENTO")
@Audited
@AutoComplete(repository = MovimientoRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Movimiento  implements Comparable<Movimiento>{

	// //////////////////////////////////////
	// Identificacion en la UI. Aparece como item del menu
	// //////////////////////////////////////

	public String title() {
		return this.creadoPor;
	}

	public String iconName() {
		return "Movimiento";
	}

	// //////////////////////////////////////
	// Habilitado (propiedad)
	// //////////////////////////////////////

	public boolean habilitado;

	@Hidden
	@MemberOrder(sequence = "40")
	public boolean getEstaHabilitado() {
		return habilitado;
	}

	public void setHabilitado(final boolean habilitado) {
		this.habilitado = habilitado;
	}

	// //////////////////////////////////////
	// creadoPor
	// //////////////////////////////////////

	private String creadoPor;

	@Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	// //////////////////////////////////////
	// relacion Computadora/Movimiento.
	// //////////////////////////////////////
	private Computadora computadora;

	@MemberOrder(sequence = "100")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public Computadora getComputadora() {
		return computadora;
	}
	

	public void setComputadora(Computadora computadora) {
		this.computadora = computadora;
	}

	public void clearComputadora() {
		// TODO Auto-generated method stub

	}

	// //////////////////////////////////////
	// relacion Tecnico/Movimiento.
	// //////////////////////////////////////
	// {{ Tecnico (property)
	private Tecnico tecnico;

	@Optional
	@MemberOrder(sequence = "1")
	@Column(allowsNull = "True")
	public Tecnico getTecnico() {
		return tecnico;
	}
	
	public void setTecnico(final Tecnico tecnico) {
		this.tecnico = tecnico;
	}
//	}

	@Override
	public int compareTo(final Movimiento movimiento) {
		return ObjectContracts.compare(this, movimiento, "tecnico.getApellido(),computadora.getIp()");
	}

	 
	 //////////////////////////////////////
	 //Injected Services
	 //////////////////////////////////////

	@SuppressWarnings("unused")
	@javax.inject.Inject
	private DomainObjectContainer container;
	@SuppressWarnings("unused")
	@javax.inject.Inject
	private TecnicoRepositorio tecnicoRepositorio;
	@SuppressWarnings("unused")
	@javax.inject.Inject
	private ComputadoraRepositorio computadoraRepositorio;

}
