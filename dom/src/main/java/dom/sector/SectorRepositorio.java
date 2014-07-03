package dom.sector;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;

@Named("SECTOR")
public class SectorRepositorio {

	public SectorRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "sector";
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Insertar un Sector.
	// //////////////////////////////////////
	@Named("Agregar")
	@MemberOrder(sequence = "10")
	public Sector agregar(
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Nombre") String nombre_sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Responsable") String responsable,
			final @Named("Disposicion") Boolean disposicion,
			final @Named("Expediente") Boolean expediente,
			final @Named("Resolucion") Boolean resolucion) {
		return nuevoSector(nombre_sector, responsable, disposicion, expediente,
				resolucion, this.currentUserName());
	}

	@Programmatic
	private Sector nuevoSector(final String nombre_sector,
			final String responsable, final Boolean disposicion,
			final Boolean expediente, final Boolean resolucion,
			final String creadoPor) {
		final Sector unSector = this.container
				.newTransientInstance(Sector.class);
		unSector.setNombre_sector(nombre_sector.toUpperCase().trim());
		unSector.setHabilitado(true);
		unSector.setCreadoPor(creadoPor);
		unSector.setResponsable(responsable);
		unSector.setResolucion(resolucion);
		unSector.setDisposicion(disposicion);
		unSector.setExpediente(expediente);
		this.container.persistIfNotAlready(unSector);
		this.container.flush();
		return unSector;
	}

	// //////////////////////////////////////
	// ListarTodos
	// //////////////////////////////////////
	@MemberOrder(sequence = "20")
	public List<Sector> listar() {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"todosLosSectores", "creadoPor", this.currentUserName()));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
	}

	/**
	 * Buscar
	 * 
	 * @param nombreSector
	 * @return
	 */

	@MemberOrder(sequence = "21")
	public List<Sector> buscar(
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Nombre") @MinLength(2) String nombreSector) {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"buscarPorNombre", "nombre_sector", nombreSector.toUpperCase()
								.trim()));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
	}

	// //////////////////////////////////////
	// AutoComplete: Servicio utilizado por Sector.
	// //////////////////////////////////////
	@Programmatic
	public List<Sector> autoComplete(final String buscarNombreSector) {
		return container.allMatches(new QueryDefault<Sector>(Sector.class,
				"autoCompletePorNombreSector", "nombre_sector",
				buscarNombreSector.toUpperCase().trim()));
	}

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