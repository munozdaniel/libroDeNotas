package dom.documento;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Documento implements Comparable<Documento> {
	// pk private id_documento;

//	private int nro_documento;
//
//	@javax.jdo.annotations.Column(allowsNull = "false")
//	@MemberOrder(sequence = "10")
//	public int getNro_documento() {
//		return nro_documento;
//	}
//
//	public void setNro_documento(int nro_documento) {
//		this.nro_documento = nro_documento;
//	}

	private int fecha;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "20")
	public int getFecha() {
		return fecha;
	}

	public void setFecha(int fecha) {
		this.fecha = fecha;
	}

	private int tipo;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "30")
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	private String descripcion;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "50")
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	private int eliminado;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "60")
	public int getEliminado() {
		return eliminado;
	}

	public void setEliminado(int eliminado) {
		this.eliminado = eliminado;
	}

	private int ultimo;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "70")
	public int getUltimo() {
		return ultimo;
	}

	public void setUltimo(int ultimo) {
		this.ultimo = ultimo;
	}

	private String observacion;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "80")
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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
	// RELACION: Documento - Nota
	// //////////////////////////////////////

	// {{ Nota (property)
//	private Nota nota;
//
//	@MemberOrder(sequence = "1")
//	@Column(allowsNull = "False")
//	public Nota getNota() {
//		return nota;
//	}
//
//	public void setNota(final Nota nota) {
//		this.nota = nota;
//	}
	// }}


	// //////////////////////////////////////
	// RELACIONES: Documento - Sector
	// //////////////////////////////////////

//	private int id_sector;
//
//	@javax.jdo.annotations.Column(allowsNull = "false")
//	@MemberOrder(sequence = "40")
//	public int getId_sector() {
//		return id_sector;
//	}
//
//	public void setId_sector(int id_sector) {
//		this.id_sector = id_sector;
//	}

	// //////////////////////////////////////
	// Implementando los metodos de comparable
	// //////////////////////////////////////

	@Override
	public int compareTo(Documento documento) {
		return ObjectContracts.compare(this, documento, "id_documento");
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@SuppressWarnings("unused")
	@javax.inject.Inject
	private DomainObjectContainer container;

}
