package dom.sectores;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.util.ObjectContracts;


@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Sector_nombre_Sector_must_be_unique", members = { "nombre_sector" }) })
@ObjectType("SECTORES")
@Audited
// @AutoComplete(repository = SectorRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Sector implements Comparable<Sector> {

	// //////////////////////////////////////
	// Identificacion en la UI. Aparece como item del menu
	// //////////////////////////////////////

	public String title() {
		return this.getNombre_sector();
	}

	public String iconName() {
		return "Sector";
	}

	// //////////////////////////////////////
	// Descripcion de las propiedades.
	// //////////////////////////////////////

	private String nombre_sector;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@DescribedAs("Nombre del Sector:")
	@MemberOrder(sequence = "10")
	public String getNombre_sector() {
		return nombre_sector;
	}

	public void setNombre_sector(String nombre_sector) {
		this.nombre_sector = nombre_sector;
	}

	private int resolucion;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "20")
	public int getResolucion() {
		return resolucion;
	}

	public void setResolucion(int resolucion) {
		this.resolucion = resolucion;
	}

	private String responsable;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@MemberOrder(sequence = "30")
	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	private int disposicion;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "40")
	public int getDisposicion() {
		return disposicion;
	}

	public void setDisposicion(int disposicion) {
		this.disposicion = disposicion;
	}

	private int expediente;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "50")
	public int getExpediente() {
		return expediente;
	}

	public void setExpediente(int expediente) {
		this.expediente = expediente;
	}

	@Override
	public int compareTo(final Sector sector) {
		
		return ObjectContracts.compare(this, sector, "nombre_sector");
	}

}
