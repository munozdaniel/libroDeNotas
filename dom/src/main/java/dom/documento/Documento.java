package dom.documento;

import java.util.List;

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
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

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
	@Named("Fecha")
	@MemberOrder(sequence = "1")
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

	@MemberOrder(name = "Observaciones", sequence = "1")
	@Named("Descripcion")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MultiLine
	@MaxLength(255)
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

	private Blob adjuntar;

	@MemberOrder(name = "Observaciones", sequence = "5")
	@javax.jdo.annotations.Persistent(defaultFetchGroup = "false")
	@javax.jdo.annotations.Column(allowsNull = "true", name = "adjunto")
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
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	private Sector sector;

	@MemberOrder(sequence = "2")
	@Column(allowsNull = "False")
	@Named("Origen")
	// @Hidden(where=Where.STANDALONE_TABLES)
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

	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listar();
	}

	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	// }}
	// {{ Ultimo (property)
	private Boolean ultimo;

	@Hidden
	@MemberOrder(sequence = "100")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public boolean getUltimo() {
		return ultimo;
	}

	public void setUltimo(final boolean ultimo) {
		this.ultimo = ultimo;
	}

	// //////////////////////////////////////
	// CompareTo
	// //////////////////////////////////////

	@Override
	public int compareTo(Documento documento) {
		return ObjectContracts.compare(this, documento, "time,descripcion");
	}

	// {{ UltimoDelAnio (property)
	private boolean ultimoDelAnio;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "1")
	@Hidden
	public boolean getUltimoDelAnio() {
		return ultimoDelAnio;
	}

	public void setUltimoDelAnio(final boolean ultimoDelAnio) {
		this.ultimoDelAnio = ultimoDelAnio;
	}

	// }}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@SuppressWarnings("unused")
	@javax.inject.Inject
	private DomainObjectContainer container;

}
