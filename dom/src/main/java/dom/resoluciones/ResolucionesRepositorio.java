package dom.resoluciones;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "3")
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
		return "resolucion";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Resoluciones addResoluciones(
			final @Named("Nº Resolucion:") int nro_resolucion,
			final @Named("Fecha:") LocalDate fecha,
			final @Named("De: ") Sector sector,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) @MaxLength(255) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		return this.nuevaResolucion(nro_resolucion, fecha, sector, descripcion,
				this.currentUserName(), adjunto);

	}

	@Programmatic
	private Resoluciones nuevaResolucion(final int nro_resolucion,
			final LocalDate fecha, final Sector sector,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		final Resoluciones unaResolucion = this.container
				.newTransientInstance(Resoluciones.class);
		unaResolucion.setNro_resolucion(nro_resolucion);
		unaResolucion.setFecha(fecha);
		unaResolucion.setTipo(3);
		unaResolucion.setDescripcion(descripcion.toUpperCase().trim());
		unaResolucion.setHabilitado(true);
		unaResolucion.setCreadoPor(creadoPor);
		unaResolucion.setAdjuntar(adjunto);
		unaResolucion.setTime(LocalDateTime.now().withMillisOfSecond(3));
		unaResolucion.setSector(sector);

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
	@Named("Lista de Resoluciones")
	public List<Resoluciones> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Resoluciones> listaMemo = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						criterio));
		if (listaMemo.isEmpty()) {
			this.container
					.warnUser("No hay Resoluciones cargados en el sistema");
		}
		return listaMemo;

	}

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////

	@MemberOrder(sequence = "30")
	public List<Resoluciones> filtrar(
			final @Optional @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				final List<Resoluciones> filtrarPorFecha = this.container
						.allMatches(new QueryDefault<Resoluciones>(
								Resoluciones.class, "filtrarPorFecha", "fecha",
								fecha));

				if (filtrarPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return filtrarPorFecha;
			} else if (fecha == null && sector != null) {
				final List<Resoluciones> filtrarPorSector = this.container
						.allMatches(new QueryDefault<Resoluciones>(
								Resoluciones.class, "filtrarPorSector",
								"sector", sector));
				this.container.warnUser("Filtrado por Sector.");

				if (filtrarPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return filtrarPorSector;
			} else {
				final List<Resoluciones> filtrarFechaSector = this.container
						.allMatches(new QueryDefault<Resoluciones>(
								Resoluciones.class, "filtrarPorFechaSector",
								"fecha", fecha, "sector", sector));
				this.container.warnUser("Filtrado por Fecha y Sector.");

				if (filtrarFechaSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return filtrarFechaSector;
			}
		}
	}

	@Named("Sector")
	public List<Sector> choices0Filtrar() {
		return sectorRepositorio.listarResoluciones();
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
