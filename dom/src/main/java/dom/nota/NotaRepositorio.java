package dom.nota;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

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
		return "nota";
	}

	/**
	 * addNota
	 * 
	 * @param sector
	 * @param destino
	 * @param descripcion
	 * @return
	 */
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Nota addNota(final @Named("De:") Sector sector,
			final @Named("Para:") String destino,
			final @Named("Descripción:") String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {

		return nuevaNota(sector, destino, descripcion, this.currentUserName(),
				adjunto);

	}

	@Programmatic
	private Nota nuevaNota(final Sector sector, final String destino,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		final Nota unaNota = this.container.newTransientInstance(Nota.class);
		Long nro = new Long(0);
		Nota notaAnterior = recuperarUltimo();
		if (notaAnterior != null) {
			if (!this.iniciarNuevoAnio()) {
				nro = notaAnterior.getNro_nota() + 1;
			}
			notaAnterior.setUltimo(false);
		}
		formato = new Formatter();
		formato.format("%04d", nro);
		unaNota.setNro_nota(Long.parseLong(formato.toString()));
		unaNota.setDescripcion(descripcion.toUpperCase().trim());
		unaNota.setFecha(LocalDate.now());
		unaNota.setTipo(1);
		unaNota.setCreadoPor(creadoPor);
		unaNota.setDestino(destino);
		unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
		unaNota.setUltimo(true);
		unaNota.setAdjuntar(adjunto);
		sector.addToDocumento(unaNota);
		unaNota.setHabilitado(true);
		container.persistIfNotAlready(unaNota);
		container.flush();
		return unaNota;
	}

	@Programmatic
	private Boolean iniciarNuevoAnio() {
		String fecha = LocalDate.now().getYear() + "-01-01"; 
		final List<Nota> lista = this.container
				.allMatches(new QueryDefault<Nota>(Nota.class, "esNuevoAnio",
						"fecha", LocalDate.now())); // AGREGAR fecha
		if (lista.isEmpty())
			return true;
		else
			return false;
	}

	@Programmatic
	private Nota recuperarUltimo() {
		final Nota nota = this.container.firstMatch(new QueryDefault<Nota>(
				Nota.class, "recuperarUltimo"));
		if (nota == null)
			return null;
		else
			return nota;
	}

	@Named("Sector")
	public List<Sector> choices0AddNota() {
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
	}

	@Programmatic
	public List<Nota> autoComplete(final String destino) {
		String criterio = "autoCompletarDestino";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "autoComplete";
		return container.allMatches(new QueryDefault<Nota>(Nota.class,
				criterio, "destino", destino));
	}

	/**
	 * Listar todas las notas, dependera del usuario y sus roles. Optimizar las
	 * busquedas por usuario D:
	 * 
	 * @return
	 */
	@Paged(12)
	@MemberOrder(sequence = "20")
	public List<Nota> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Nota> listaNotas = this.container
				.allMatches(new QueryDefault<Nota>(Nota.class, criterio));
		if (listaNotas.isEmpty()) {
			this.container.warnUser("No hay Notas cargados en el sistema");
		}
		return listaNotas;

	}

	/**
	 * Filtrar por fecha o/y sector.
	 * 
	 * @param sector
	 * @param fecha
	 * @return
	 */
	@MemberOrder(sequence = "30")
	public List<Nota> filtrar(final @Optional @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				String criterio = "filtrarPorFecha";
				if (this.container.getUser().isCurrentUser("root"))
					criterio = "filtrarPorFechaRoot";
				final List<Nota> notasPorFecha = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								criterio, "fecha", fecha));

				if (notasPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return notasPorFecha;
			} else if (fecha == null && sector != null) {
				String criterio = "filtrarPorSector";
				if (this.container.getUser().isCurrentUser("root"))
					criterio = "filtrarPorSectorRoot";
				final List<Nota> notasPorSector = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								criterio, "sector", sector));
				this.container.warnUser("Filtrado por Sector.");

				if (notasPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return notasPorSector;
			} else {
				String criterio = "filtrarPorFechaSector";
				if (this.container.getUser().isCurrentUser("root"))
					criterio = "filtrarPorFechaSectorRoot";
				final List<Nota> notas = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								criterio, "fecha", fecha, "sector", sector));
				this.container.warnUser("Filtrado por Fecha y Sector.");

				if (notas.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return notas;
			}
		}
	}

	@Named("Sector")
	public List<Sector> choices0Filtrar() {
		return sectorRepositorio.listar();

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
	// @javax.inject.Inject
	// private IsisJdoSupport isisJdoSupport;
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	private Formatter formato;

}
