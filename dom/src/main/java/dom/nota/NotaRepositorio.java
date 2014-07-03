package dom.nota;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.RegEx;
import org.joda.time.LocalDate;

import dom.sectores.Sector;

@Named("Notas")
public class NotaRepositorio {

	public NotaRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "nota";
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Insertar un Sector.
	// //////////////////////////////////////
	@Named("Agregar")
	@MemberOrder(sequence = "10")
	public Nota addNota(
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("De:") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Para:") String destino,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion			
			) {
//		return nuevaNota(sector, destino, descripcion);
		return nuevaNota(sector,destino,descripcion,this.currentUserName());
	}

	private Nota nuevaNota(final Sector sector,final String destino,
			final String descripcion, final String currentUserName) {
		final Nota unaNota = this.container.newTransientInstance(Nota.class);
		unaNota.setNro_nota(1);
		unaNota.setFecha(LocalDate.now());
		unaNota.setTipo(1);
		unaNota.setDescripcion(descripcion);
		unaNota.setHabilitado(true);
		container.persistIfNotAlready(unaNota);
		container.flush();
		return unaNota;
	}

	/**
	 * Buscar
	 * 
	 * @param nombreSector
	 * @return
	 */


	// @Programmatic
	// public Nota nuevaNota(final Sector sector, final String destino,
	// final String descripcion) {
	// final Nota unaNota = this.container
	// .newTransientInstance(Nota.class);
	//
	// unSector.setNombreSector(nombreSector.toUpperCase().trim());
	// unSector.setHabilitado(true);
	// unSector.setCreadoPor(creadoPor);
	// this.container.persistIfNotAlready(unSector);
	// this.container.flush();
	// return unSector;
	//
	// }

	// //////////////////////////////////////
	// CurrentUserName
	// //////////////////////////////////////

	private String currentUserName() {
		return container.getUser().getName();
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;
}
