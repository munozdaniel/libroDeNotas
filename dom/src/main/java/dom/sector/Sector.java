package dom.sector;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
//@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nombre_Sector_must_be_unique", members = { "nombre_sector" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletePorNombreSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "
				+ "WHERE nombre_sector.indexOf(:nombre_sector) >= 0"),
		@javax.jdo.annotations.Query(name = "todosLosSectoresTrue", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "todosLosSectores", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "),
		@javax.jdo.annotations.Query(name = "sectoresDisposiciones", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE disposicion==true"),
		@javax.jdo.annotations.Query(name = "sectoresResoluciones", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE resolucion==true"),
		@javax.jdo.annotations.Query(name = "sectoresExpediente", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE expediente==true"),
				@javax.jdo.annotations.Query(name = "buscarNombre", language = "JDOQL", value = "SELECT  "
						+ " FROM dom.sector.Sector " + " WHERE nombre_sector==:nombre"),
		@javax.jdo.annotations.Query(name = "buscarPorNombre", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "
				+ "WHERE "
				+ " nombre_sector.indexOf(:nombre_sector) >= 0") })
@ObjectType("SECTORES")
@Audited
@AutoComplete(repository = SectorRepositorio.class, action = "autoComplete")
//@Bookmarkable
public class Sector implements Comparable<Sector> {

	
	// //////////////////////////////////////
	// Identificacion en la UI.
	// Aparece como item del menu
	// //////////////////////////////////////

	public String title() {
		return this.getNombre_sector();
	}

	public String iconName() {
		return "sector";
	}

	// //////////////////////////////////////
	// Descripcion de las propiedades.
	// //////////////////////////////////////

	private String nombre_sector;

	@javax.jdo.annotations.Column(allowsNull = "false")
	// @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@DescribedAs("Nombre del Sector:")
	@MemberOrder(sequence = "10")
	@Named("Nombre")
	public String getNombre_sector() {
		return nombre_sector;
	}

	public void setNombre_sector(String nombre_sector) {
		this.nombre_sector = nombre_sector;
	}

	private String responsable;

	@javax.jdo.annotations.Column(allowsNull = "false")
	// @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*")
	@MemberOrder(sequence = "11")
	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	private Boolean resolucion;

//	@Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(sequence = "20")
	public Boolean getResolucion() {
		return resolucion;
	}

	public void setResolucion(Boolean resolucion) {
		this.resolucion = resolucion;
	}

	private Boolean disposicion;
//	@Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(sequence = "40")
	public Boolean getDisposicion() {
		return disposicion;
	}

	public void setDisposicion(Boolean disposicion) {
		this.disposicion = disposicion;
	}
//	@Programmatic
//	public void defaultDisposicion() {
//		this.setDisposicion(false); // TODO: return default for property when first created
//	}
	private Boolean expediente ;

//	@Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(sequence = "50")
	public Boolean getExpediente() {
		return expediente;
	}

	public void setExpediente(Boolean expediente) {
		this.expediente = expediente;
	}
//	@Programmatic
//	public Boolean defaultExpediente() {
//		return false; // TODO: return default for property when first created
//	}
	// //////////////////////////////////////
	// CreadoPor
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

	@Override
	public int compareTo(final Sector sector) {

		return ObjectContracts.compare(this, sector, "nombre_sector");
//		return 1;
	}

	// //////////////////////////////////////
	// Relacion Sector/Documento
	// //////////////////////////////////////
	   // overrides the natural ordering
//    public static class DependenciesComparator implements Comparator<Documento> {
//        @Override
//        public int compare(Documento p, Documento q) {
//            Ordering<Documento> byDescription = new Ordering<Documento>() {
//                public int compare(final Documento p, final Documento q) {
//                    return Ordering.natural().nullsFirst().compare(p.getDescripcion(), q.getDescripcion());
//                }
//            };
//            return byDescription
//                    .compound(Ordering.<Documento>natural())
//                    .compare(p, q);
//        }
//    }
//	// {{ Documentos (Collection)
//	@Join
////	@Persistent(mappedBy = "sector", dependentElement = "False")
//	@Persistent(mappedBy = "sector", dependentElement = "False")
//	private List<Documento> documentos = new ArrayList<Documento>();
//
//	@MemberOrder(sequence = "1")
//    @SortedBy(DependenciesComparator.class)
//	public List<Documento> getDocumentos() {
//		return documentos;
//	}
//
//	public void setDocumentos(final List<Documento> documentos) {
//		this.documentos = documentos;
//	}

	// }}
//	@Programmatic
//	public Sector addToDocumento(final Documento unDocumento) {
//		// check for no-op
//		if (unDocumento == null || getDocumentos().contains(unDocumento)) {
//			return this;
//		}
//		// dissociate arg from its current parent (if any).
//		unDocumento.clearSector();
//		// associate arg
//		unDocumento.setSector(this);
//		this.getDocumentos().add(unDocumento);
//		return this;
//		// additional business logic
//		// onAddToDocumento(unDocumento);
//	}
//
//	@Programmatic
//	public void removeFromDocumento(final Documento unDocumento) {
//		// check for no-op
//		if (unDocumento == null || !getDocumentos().contains(unDocumento)) {
//			return;
//		}
//		// dissociate arg
//		unDocumento.setSector(null);
//		this.getDocumentos().remove(unDocumento);
//		// additional business logic
//		onRemoveFromDocumento(unDocumento);
//	}
//
//	private void onRemoveFromDocumento(Documento unDocumento) {
//		unDocumento.setHabilitado(false);
//	}

}
