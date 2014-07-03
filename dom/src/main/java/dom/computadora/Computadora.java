package dom.computadora;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.Where;

import dom.impresora.Impresora;
import dom.movimiento.Movimiento;
import dom.persona.Persona;
import dom.usuario.Usuario;
import dom.usuario.UsuarioRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Computadora_ip_must_be_unique", members = {
		"creadoPor", "ip" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletePorComputadora", language = "JDOQL", value = "SELECT "
				+ "FROM dom.computadora.Computadora "
				+ "WHERE creadoPor == :creadoPor && " + "ip.indexOf(:ip) >= 0"),
		@javax.jdo.annotations.Query(name = "eliminarComputadoraFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.computadora.Computadora "
				+ "WHERE creadoPor == :creadoPor "
				+ "   && habilitado == false"),
		@javax.jdo.annotations.Query(name = "eliminarComputadoraTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.computadora.Computadora "
				+ "WHERE creadoPor == :creadoPor " + "   && habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarPorIp", language = "JDOQL", value = "SELECT "
				+ "FROM dom.computadora.Computadora "
				+ "WHERE creadoPor == :creadoPor "
				+ "   && ip.indexOf(:ip) >= 0"), })
@ObjectType("COMPUTADORA")
@Audited
@AutoComplete(repository = ComputadoraRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Computadora {

	// //////////////////////////////////////
	// Identificacion en la UI
	// //////////////////////////////////////

	public String title() {
		return this.getIp();
	}

	public String iconName() {
		return "COMPUTADORA";
	}

	// //////////////////////////////////////
	// IP (propiedad)
	// //////////////////////////////////////

	private String ip;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Direccion IP de la Computadora:")
	@MemberOrder(sequence = "10")
	public String getIp() {
		return ip;
	}

	public void setIp(final String ip) {
		this.ip = ip;
	}

	// //////////////////////////////////////
	// Mother (propiedad)
	// //////////////////////////////////////

	private String mother;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Mother de la Computadora:")
	@MemberOrder(sequence = "20")
	public String getMother() {
		return mother;
	}

	public void setMother(final String mother) {
		this.mother = mother;
	}

	// //////////////////////////////////////
	// Procesador (propiedad)
	// //////////////////////////////////////

	private String procesador;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Procesador de la Computadora:")
	@MemberOrder(sequence = "30")
	public String getProcesador() {
		return procesador;
	}

	public void setProcesador(final String procesador) {
		this.procesador = procesador;
	}

	// //////////////////////////////////////
	// Disco (propiedad)
	// //////////////////////////////////////

	// private String disco;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// @DescribedAs("Disco de la Computadora:")
	// @MemberOrder(sequence = "40")
	// public String getDisco() {
	// return disco;
	// }
	//
	// public void setDisco(final String disco) {
	// this.disco = disco;
	// }

	public static enum CategoriaDisco {
		Seagate, Western, Otro;

	}

	private CategoriaDisco disco;

	@javax.jdo.annotations.Column(allowsNull = "false")
	public CategoriaDisco getDisco() {
		return disco;
	}

	public void setDisco(CategoriaDisco disco) {
		this.disco = disco;
	}

	// //////////////////////////////////////
	// Memoria (propiedad)
	// //////////////////////////////////////

	private String memoria;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Memoria de la Computadora:")
	@MemberOrder(sequence = "50")
	public String getMemoria() {
		return memoria;
	}

	public void setMemoria(final String memoria) {
		this.memoria = memoria;
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
	// Impresora (propiedad)
	// //////////////////////////////////////

	private Impresora impresora;
	@MemberOrder(sequence = "50")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public Impresora getImpresora() {
		return impresora;
	}

	public void setImpresora(Impresora impresora) {
		this.impresora = impresora;
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
	// Relacion Computadora/Persona
	// //////////////////////////////////////

	@Persistent(mappedBy = "computadora", dependentElement = "False")
	@Join
	private SortedSet<Persona> personas = new TreeSet<Persona>();

	@MemberOrder(sequence = "100")
	public SortedSet<Persona> getPersona() {
		return personas;
	}

	public void setPersona(final SortedSet<Persona> personas) {
		this.personas = personas;
	}

	// }}
	@Named("Buscar Persona")
	@PublishedAction
	// D:
	@MemberOrder(name = "personas", sequence = "110")
	public Computadora add(final Persona persona) {
		// check for no-op
		if (persona == null || getPersona().contains(persona)) {
			return this;
		}
		// dissociate arg from its current parent (if any).
		persona.clear();
		// associate arg
		persona.setComputadora(this);
		this.getPersona().add(persona);
		return this;
		// additional business logic
		// onAddToPersona(persona);
	}

	@Named("Persona")
	@DescribedAs("Buscar el Usuario en mayuscula")
	public List<Persona> autoComplete0Add(final @MinLength(2) String search) {
		List<Usuario> usuarios = usuarioRepositorio.autoComplete(search);
		List<Persona> personas = new ArrayList<Persona>();
		for (Usuario usuario : usuarios) {
			Persona unaP = usuario;
			personas.add(unaP);
		}
		return personas;
	}
	

	// //////////////////////////////////////
	// Relacion Computadora(Parent)/Movimiento(Child). 
	// //////////////////////////////////////
	@Persistent(mappedBy = "computadora", dependentElement = "False")
	@Join
	private SortedSet<Movimiento> movimientos = new TreeSet<Movimiento>();

	public SortedSet<Movimiento> getMovimientos() {
		return movimientos;
	}
	
	public void setMovimientos(SortedSet<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}
	public void addToMovimiento(final Movimiento unMovimiento) {
		// check for no-op
		if (unMovimiento == null
				|| getMovimientos().contains(unMovimiento)) {
			return;
		}
		// dissociate arg from its current parent (if any).
		unMovimiento.clearComputadora();
		// associate arg
		unMovimiento.setComputadora(this);
		getMovimientos().add(unMovimiento);
	}

	public void removeFromMovimiento(
			final Movimiento unMovimiento) {
		// check for no-op
		if (unMovimiento == null
				|| !getMovimientos().contains(unMovimiento)) {
			return;
		}
		// dissociate arg
		unMovimiento.setComputadora(null);
		getMovimientos().remove(unMovimiento);
	}
	

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////


	@javax.inject.Inject
	private UsuarioRepositorio usuarioRepositorio;
}
