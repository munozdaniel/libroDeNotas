package dom.resoluciones;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("RESOLUCIONES")
public class ResolucionesRepositorio {

	public ResolucionesRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "resoluciones";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Resoluciones addResoluciones(
			final @Named("Nº Resolucion:") int nro_resolucion,
			final @Named("Fecha:") LocalDate fecha,
			final @Named("De: ") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion) {
		return this.nuevaResolucion(nro_resolucion, fecha, sector, descripcion,
				this.currentUserName());

	}

	@Programmatic
	private Resoluciones nuevaResolucion(int nro_resolucion, LocalDate fecha,
			Sector sector, final String descripcion, String creadoPor) {
		final Resoluciones unaResolucion = this.container
				.newTransientInstance(Resoluciones.class);
		unaResolucion.setNro_resolucion(nro_resolucion);
		unaResolucion.setFecha(fecha);
		unaResolucion.setTipo(3);
		unaResolucion.setDescripcion(descripcion.toUpperCase().trim());
		unaResolucion.setHabilitado(true);
		unaResolucion.setCreadoPor(creadoPor);
		// unaResolucion.setSector(sector);
		unaResolucion.setTime(LocalDateTime.now().withMillisOfSecond(3));
		sector.addToDocumento(unaResolucion);
		container.persistIfNotAlready(unaResolucion);
		container.flush();
		return unaResolucion;
	}

	@Programmatic
	public List<Resoluciones> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Resoluciones>(
				Resoluciones.class, "autoCompletarDestino", "nombreSector",
				destino));
	}

	@Named("Sector")
	public List<Sector> choices2AddResoluciones() {
		return sectorRepositorio.listarResoluciones();
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////

	@MemberOrder(sequence = "20")
	public List<Resoluciones> listar() {
		final List<Resoluciones> listaMemo = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						"listarHabilitados"));
		if (listaMemo.isEmpty()) {
			this.container.warnUser("No hay tecnicos cargados en el sistema");
		}
		return listaMemo;

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
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
}
