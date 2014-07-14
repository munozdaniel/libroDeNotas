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
import org.apache.isis.applib.util.ObjectContracts;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Documento implements Comparable<Documento> {
	// pk
	// private BigInteger id_documento;
	// public BigInteger getId_documento() {
	// return id_documento;
	// }
	//
	// public void setId_documento(BigInteger id_documento) {
	// this.id_documento = id_documento;
	// }

	private LocalDateTime time;

	@Hidden
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Named("system_time")
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public void loading() {
		// TODO: pre-load
		this.setDescripcion("PRE-LOAD ::: RRotisisimo");
	}

	public void loaded() {
		// TODO: post-load
		this.setDescripcion("POST-LOAD ::: ROTo");

	}

	private LocalDate fecha;

	@Disabled
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "20")
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
	@MemberOrder(sequence = "100")
	@MultiLine
	@MaxLength(150)
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
	// creadoPor
	// //////////////////////////////////////

	private String creadoPor;

	@Disabled
	@Hidden
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	// //////////////////////////////////////
	// RELACION: Documento - Nota
	// //////////////////////////////////////

	// {{ Nota (property)
	// private Nota nota;
	//
	// @MemberOrder(sequence = "1")
	// @Column(allowsNull = "False")
	// public Nota getNota() {
	// return nota;
	// }
	//
	// public void setNota(final Nota nota) {
	// this.nota = nota;
	// }
	// }}

	// //////////////////////////////////////
	// RELACIONES: Documento - Sector
	// //////////////////////////////////////

	// private int id_sector;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// @MemberOrder(sequence = "40")
	// public int getId_sector() {
	// return id_sector;
	// }
	//
	// public void setId_sector(int id_sector) {
	// this.id_sector = id_sector;
	// }

	// {{ PropertyName (property)
	private Sector sector;

	@MemberOrder(sequence = "22")
	@Column(allowsNull = "False")
	public Sector getSector() {
		return sector;
	}

	public void setSector(final Sector sector) {
		this.sector = sector;
	}

	public void clearSector() {
		// TODO Auto-generated method stub
		if (this.getSector() != null)
			this.setSector(null);
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
