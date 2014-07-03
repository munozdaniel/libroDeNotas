package dom.persona;

import java.util.List;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.Where;

import dom.computadora.Computadora;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Persona {

	// //////////////////////////////////////
	// APELLIDO (propiedad)
	// //////////////////////////////////////

	private String apellido;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Apellido de la Persona:")
	@RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@MemberOrder(sequence = "10")
	public String getApellido() {
		return apellido;
	}

	public void setApellido(final String apellido) {
		this.apellido = apellido;
	}

	// //////////////////////////////////////
	// NOMBRE (propiedad)
	// //////////////////////////////////////

	private String nombre;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Nombre de la Persona:")
	@RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@MemberOrder(sequence = "20")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}

	// //////////////////////////////////////
	// EMAIL (propiedad)
	// //////////////////////////////////////

	private String email;

	@Optional
	@javax.jdo.annotations.Column(allowsNull = "true")
	@RegEx(validation = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
	@MemberOrder(sequence = "30")
	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
	
	// //////////////////////////////////////
	// Habilitado (propiedad)
	// //////////////////////////////////////
	
	public boolean habilitado;

	@Hidden
	@MemberOrder(sequence="40")    
	public boolean getEstaHabilitado(){
		return habilitado;
	}
	public void setHabilitado(final boolean habilitado){
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
	// Computadora (propiedad)
	// //////////////////////////////////////
	
	private Computadora computadora;
	@MemberOrder(sequence = "70")
	@javax.jdo.annotations.Column(allowsNull ="true")
	public Computadora getComputadora() {
	  		return computadora;
	  	}
	public void setComputadora(Computadora computadora){
		this.computadora = computadora;
	}

	// //////////////////////////////////////
	// Sector (propiedad)
	// //////////////////////////////////////
	
	private Sector sector;
	@MemberOrder(sequence = "100")
	@javax.jdo.annotations.Column(allowsNull ="true")
	public Sector getSector() {
	  		return sector;
	  	}
	public void setSector(Sector sector){
		this.sector = sector;
	}
	
	// //////////////////////////////////////
	// Modificar Sector
	// //////////////////////////////////////
	
	@MemberOrder(sequence = "110")
	@Named("Modificar Sector")
	public Persona modify(
			final Sector sector) {
		Sector currentSector = getSector();
		// check for no-op
		if (sector == null
				|| sector.equals(currentSector)) {
			return this;
		}
		// delegate to parent to associate
		sector.add(this);
		// additional business logic
		//onModifySector(currentSector,	sector);
		return this;
	}
	
	// //////////////////////////////////////
	// Buscar Sector
	// //////////////////////////////////////
	
	@Named("Sector")
	@DescribedAs("Buscar el Sector")
	public List<Sector> autoComplete0Modify(final @MinLength(2) String search) {
		return sectorRepositorio.autoComplete(search);
	}
	
	// //////////////////////////////////////
	// Eliminar Sector
	// //////////////////////////////////////
	
	@MemberOrder(sequence = "120")
	@Named("Eliminar Sector")
	public Persona clear() {
		Sector currentSector = this.getSector();
		// check for no-op
		if (currentSector == null) {
			return this;
		}
		// delegate to parent to dissociate
		currentSector.remove(this);
		
		return this;
		// additional business logic
		//onClearSector(currentSector);
	}
	
    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////
	
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
}