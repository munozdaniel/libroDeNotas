package dom.nota;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "1")
@Named("Notas")
public class NotaRepositorio {
	public final Lock monitor = new ReentrantLock();

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
			final @Named("De:") Sector sector,
			final @Named("Para:") String destino,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {

		Nota nota = nuevaNota(sector, destino, descripcion,
				this.currentUserName(), adjunto);
		if (nota != null)
			return nota;

		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;

	}

	@Programmatic
	private Nota nuevaNota(final Sector sector, final String destino,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		try {
			if (monitor.tryLock(1, TimeUnit.SECONDS)) {
				try {
					final Nota unaNota = this.container
							.newTransientInstance(Nota.class);
					Integer nro = Integer.valueOf(1);

					Nota notaAnterior = recuperarElUltimo();
					if (notaAnterior != null) {
						// Si no es el ultimo del año, continua sumando el nro
						// de
						// nota.
						if (!notaAnterior.getUltimoDelAnio())
							nro = notaAnterior.getNro_nota() + 1;
						else
							notaAnterior.setUltimoDelAnio(false);
						notaAnterior.setUltimo(false);
						// container.flush();
					}
					// if (unaNota.getDescripcion().equalsIgnoreCase("ALGO")) {
					// try {
					// Thread.sleep(11000);
					// } catch (InterruptedException e) {
					//
					// }
					//
					// }
					// Si no habian nota, o si es el ultimo del año, el proximo
					// nro
					// comienza en 1.

					unaNota.setDescripcion(descripcion.toUpperCase().trim());
					unaNota.setUltimo(true);
					unaNota.setNro_nota(nro);
					unaNota.setFecha(LocalDate.now());
					unaNota.setTipo(1);
					unaNota.setCreadoPor(creadoPor);
					unaNota.setDestino(destino);
					unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
					unaNota.setAdjuntar(adjunto);
					unaNota.setSector(sector);
					unaNota.setHabilitado(true);

					container.persistIfNotAlready(unaNota);
					container.flush();

					return unaNota;
				} finally {
					monitor.unlock();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Programmatic
	@NotInServiceMenu
	private Nota recuperarElUltimo() {

		final Nota nota = this.container.firstMatch(new QueryDefault<Nota>(
				Nota.class, "recuperarUltimo"));
		if (nota == null)
			return null;
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
	@Named("Lista de Notas")
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
