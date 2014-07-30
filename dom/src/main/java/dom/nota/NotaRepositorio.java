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
	public Nota addNota(
			final  @Named("De:") Sector sector,
			final  @Named("Para:") String destino,
			final @Named("Descripción:") String descripcion
			,final @Optional @Named("Ajuntar:") Blob adjunto) {
		// return nuevaNota(sector, destino, descripcion);
		return nuevaNota(sector, destino, descripcion, this.currentUserName(),adjunto);
	}

	@Programmatic
	private Nota nuevaNota(final Sector sector, final String destino,
			final String descripcion, final String creadoPor,final Blob adjunto) {
		final Nota unaNota = this.container.newTransientInstance(Nota.class);
		int nro = recuperarNroNota();
		nro += 1;
		formato = new Formatter();
		formato.format("%04d", nro);
		unaNota.setNro_nota(Integer.parseInt(formato.toString()));
		unaNota.setFecha(LocalDate.now());
		unaNota.setTipo(1);
		unaNota.setDescripcion(descripcion.toUpperCase().trim());
		unaNota.setHabilitado(true);
		unaNota.setCreadoPor(creadoPor);
		unaNota.setDestino(destino);
		unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
		// container.warnUser("Time:: : " + unaNota.getTime().toString());
		// unaNota.setSector(sector);
		unaNota.setAdjuntar(adjunto);
		sector.addToDocumento(unaNota);
		container.persistIfNotAlready(unaNota);
		container.flush();
		return unaNota;
	}

	@Programmatic
	private int recuperarNroNota() {
		final List<Nota> notas = this.container
				.allMatches(new QueryDefault<Nota>(Nota.class,
						"listarHabilitados"));

		if (notas.isEmpty())
			return 0;
		else
			return notas.get(notas.size() - 1).getNro_nota();
	}

	// public String numero()
	// {
	// return "CANTIDAD: " + this.recuperarNroNota();
	// }

	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////

	// @Named("Sector")
	// @DescribedAs("Buscar el Sector en mayuscula")
	// public List<Sector> autoComplete0AddNota(final @MinLength(2) String
	// search) {
	// return sectorRepositorio.autoComplete(search);
	// }
	@Named("Sector")
	public List<Sector> choices0AddNota() {
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
	}

	@Programmatic
	public List<Nota> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Nota>(Nota.class,
				"autoCompletarDestino", "destino", destino));
	}

	// //////////////////////////////////////
	// Listar Notas
	// //////////////////////////////////////
	@Paged(12)
	@MemberOrder(sequence = "20")
	public List<Nota> listar() {
		final List<Nota> listaNotas = this.container
				.allMatches(new QueryDefault<Nota>(Nota.class,
						"listarHabilitados"));
		if (listaNotas.isEmpty()) {
			this.container.warnUser("No hay Notas cargados en el sistema");
		}
		return listaNotas;

	}

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////
	@MemberOrder(sequence = "30")
	public List<Nota> filtrar(
			final @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				final List<Nota> notasPorFecha = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								"filtrarPorFecha", "fecha", fecha));

				if (notasPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return notasPorFecha;
			} else if (fecha == null && sector != null) {
				final List<Nota> notasPorSector = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								"filtrarPorSector", "sector", sector));
				this.container.warnUser("Filtrado por Sector.");

				if (notasPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return notasPorSector;
			} else {
				final List<Nota> notas = this.container
						.allMatches(new QueryDefault<Nota>(Nota.class,
								"filtrarPorFechaSector", "fecha", fecha,
								"sector", sector));
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

	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	private Formatter formato;
}
