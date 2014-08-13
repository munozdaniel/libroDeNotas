package dom.documento;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Documento implements Comparable<Documento> {

	private LocalDateTime time;

	@Hidden
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Named("system_time")
	@MemberOrder(sequence = "100")
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	private LocalDate fecha;

	@Disabled
	@javax.jdo.annotations.Column(allowsNull = "false")
	// @MemberOrder(name="Datos Generales" ,sequence = "20")
	@Named("Fecha")
	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/*
	 * tipo - 1:Nota - 2:Memo - 3:Resoluciones - 4:Disposiciones - 5:Expedientes
	 */
	private int tipo;

	@Disabled
	@Hidden
	@javax.jdo.annotations.Column(allowsNull = "false")
	@DescribedAs("Tipo de Documento")
	@MemberOrder(sequence = "30")
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	private String descripcion;

	@Named("Descripcion")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name = "Observaciones", sequence = "50")
	@MultiLine
	@MaxLength(200)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	private Boolean habilitado;

	@Hidden
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "60")
	public Boolean getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}

	// ELIMINADO se obtendra el ultimo a travez de una consulta, con el MAX, si
	// es posible.
	// private int ultimo;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// @MemberOrder(sequence = "70")
	// public int getUltimo() {
	// return ultimo;
	// }
	//
	// public void setUltimo(int ultimo) {
	// this.ultimo = ultimo;
	// }
	// Observacion; todos los campos en la base de datos se encontraban null.
	// private String observacion;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// @MemberOrder(sequence = "80")
	// public String getObservacion() {
	// return observacion;
	// }
	//
	// public void setObservacion(String observacion) {
	// this.observacion = observacion;
	// }
	// //////////////////////////////////////
	// Attachment (property)
	// //////////////////////////////////////

	private Blob adjuntar;

	@MemberOrder(name = "Observaciones", sequence = "50")
	@javax.jdo.annotations.Persistent(defaultFetchGroup = "false")
	@javax.jdo.annotations.Column(allowsNull = "true")
	@Named("Adjuntar")
	public Blob getAdjuntar() {
		return adjuntar;
	}

	public void setAdjuntar(final Blob adjunto) {
		this.adjuntar = adjunto;
	}

	// //////////////////////////////////////
	// creadoPor
	// //////////////////////////////////////

	private String creadoPor;

	@Hidden
	@Disabled
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	private Sector sector;

	// @MemberOrder(name="Datos Generales" ,sequence = "30")
	@Column(allowsNull = "False")
	@Named("Origen")
	public Sector getSector() {
		return sector;
	}

	public void setSector(final Sector sector) {
		this.sector = sector;
	}

	public void clearSector() {
		if (this.getSector() != null)
			this.setSector(null);
	}

	// }}
	// {{ Ultimo (property)
	private Boolean ultimo;

	@Hidden
	@MemberOrder(sequence = "100")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public Boolean getUltimo() {
		return ultimo;
	}

	public void setUltimo(final Boolean ultimo) {
		this.ultimo = ultimo;
	}

	// }}

	@Override
	public int compareTo(Documento documento) {
		return ObjectContracts.compare(this, documento, "time");
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@SuppressWarnings("unused")
	@javax.inject.Inject
	private DomainObjectContainer container;

}
