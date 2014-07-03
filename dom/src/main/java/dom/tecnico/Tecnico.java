package dom.tecnico;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.util.ObjectContracts;

import dom.movimiento.Movimiento;
import dom.persona.Persona;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Tecnico_apellido_must_be_unique", members = {
		"creadoPor", "apellido" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarPorApellido", language = "JDOQL", value = "SELECT "
				+ "FROM dom.tecnico.Tecnico "
				+ "WHERE creadoPor == :creadoPor && "
				+ "apellido.indexOf(:apellido) >= 0"),
		@javax.jdo.annotations.Query(name = "eliminarTecnicoFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.tecnico.Tecnico "
				+ "WHERE creadoPor == :creadoPor "
				+ "   && habilitado == false"),
		@javax.jdo.annotations.Query(name = "eliminarTecnicoTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.tecnico.Tecnico "
				+ "WHERE creadoPor == :creadoPor " + "   && habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarPorApellido", language = "JDOQL", value = "SELECT "
				+ "FROM dom.tecnio.Tecnico"
				+ "WHERE creadoPor == :creadoPor && "
				+ "apellido.indexOf(:apellido) >= 0"),
		@javax.jdo.annotations.Query(name = "getTecnico", language = "JDOQL", value = "SELECT FROM dom.tecnico.Tecnico WHERE creadoPor == :creadoPor") })
@ObjectType("TECNICO")
@Audited
@AutoComplete(repository = TecnicoRepositorio.class, action = "autoComplete")
//
@Bookmarkable
public class Tecnico extends Persona implements Comparable<Persona> {
	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return this.getApellido() + ", " + this.getNombre();
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Borrar Usuario
	// //////////////////////////////////////
	/**
	 * Método que utilizo para deshabilitar un Tecnico.
	 * 
	 * @return la propiedad habilitado en false.
	 */
	@Named("Eliminar")
	@PublishedAction
	@Bulk
	@MemberOrder(name = "accionEliminar", sequence = "1")
	public List<Tecnico> eliminar() {
		if (getEstaHabilitado() == true) {
			setHabilitado(false);
			container.isPersistent(this);
			container.warnUser("Eliminado " + container.titleOf(this));
		}
		return null;
	}

	// Campo que diferencia a tecnico de usuario. El valor por el momento se
	// hara manualmente
	// pero lo ideal es que cambie automaticamente segun el patron State.
	private BigDecimal cantidadComputadora;

	@javax.jdo.annotations.Column(allowsNull = "true")
	public BigDecimal getCantidadComputadora() {
		return cantidadComputadora;
	}

	public void setCantidadComputadora(BigDecimal cantidadComputadora) {
		this.cantidadComputadora = cantidadComputadora;
	}

	// {{ Movimiento (property)
	private Movimiento movimiento;

	@MemberOrder(sequence = "200")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public Movimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(final Movimiento movimiento) {
		this.movimiento = movimiento;
	}

	// }}

	// //////////////////////////////////////
	// CompareTo
	// //////////////////////////////////////
	/**
	 * Implementa Comparable<Tecnico> Necesario para ordenar por apellido la
	 * clase Tecnico.
	 */
	@Override
	public int compareTo(final Persona persona) {
		return ObjectContracts.compare(this, persona, "apellido");
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;
}