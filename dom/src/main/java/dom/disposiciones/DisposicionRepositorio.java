package dom.disposiciones;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "4")
@Named("DISPOSICION")
public class DisposicionRepositorio {
	public final Lock monitor = new ReentrantLock();

	public DisposicionRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "disposicion";
	}

	public String iconName() {
		return "disposicion";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Disposicion addDisposicion(final @Named("Sector") Sector sector,
			final @Named("Descripción:") String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		Disposicion disposicion = this.nuevaDisposicion(sector, descripcion,
				this.currentUserName(), adjunto);
		if (disposicion != null)
			return disposicion;
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	@Programmatic
	private Disposicion nuevaDisposicion(final Sector sector,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		final Disposicion unaDisposicion = this.container
				.newTransientInstance(Disposicion.class);
		try {
			if (monitor.tryLock(1, TimeUnit.SECONDS)) {
				try {
					Disposicion anterior = recuperarUltimo();
					Integer nro = Integer.valueOf(1);
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio())
							nro = anterior.getNro_Disposicion() + 1;
						else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}

					unaDisposicion.setNro_Disposicion(nro);
					unaDisposicion.setUltimo(true);

					unaDisposicion.setFecha(LocalDate.now());
					unaDisposicion.setTipo(4);
					unaDisposicion.setAdjuntar(adjunto);
					unaDisposicion.setDescripcion(descripcion.toUpperCase()
							.trim());
					unaDisposicion.setHabilitado(true);
					unaDisposicion.setCreadoPor(creadoPor);

					unaDisposicion.setTime(LocalDateTime.now()
							.withMillisOfSecond(3));
					unaDisposicion.setSector(sector);

					container.persistIfNotAlready(unaDisposicion);
					container.flush();
					return unaDisposicion;
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
	private Disposicion recuperarUltimo() {
		final Disposicion doc = this.container
				.firstMatch(new QueryDefault<Disposicion>(Disposicion.class,
						"recuperarUltimo"));
		if (doc == null)
			return null;
		return doc;
	}

	@Named("Sector")
	public List<Sector> choices0AddDisposicion() {
		return sectorRepositorio.listarDisposiciones(); // TODO: return list of
														// choices for
		// property
	}

	// @Programmatic
	// private int recuperarNroDisposicion() {
	//
	// final Disposicion disposicion = this.container.firstMatch(new
	// QueryDefault<Disposicion>(
	// Disposicion.class, "buscarUltimaDisposicionTrue"));
	// if (disposicion == null)
	// return 0;
	// else
	// return disposicion.getNro_Disposicion();
	// }
	@Programmatic
	private int recuperarNroDisposicion() {
		final List<Disposicion> disposiciones = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
						"listarHabilitados"));

		if (disposiciones.isEmpty())
			return 0;
		else
			return disposiciones.get(disposiciones.size() - 1)
					.getNro_Disposicion();
	}

	@Programmatic
	public List<Disposicion> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Disposicion>(
				Disposicion.class, "autoCompletarDestino", "destinoSector",
				destino));
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////
	@Named("Lista de Disposiciones")
	@MemberOrder(sequence = "20")
	public List<Disposicion> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Disposicion> listaMemo = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
						criterio));
		if (listaMemo.isEmpty()) {
			this.container
					.warnUser("No hay Disposiciones cargados en el sistema");
		}
		return listaMemo;

	}

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////

	@MemberOrder(sequence = "30")
	public List<Disposicion> filtrar(
			final @Optional @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				final List<Disposicion> filtrarPorFecha = this.container
						.allMatches(new QueryDefault<Disposicion>(
								Disposicion.class, "filtrarPorFecha", "fecha",
								fecha));

				if (filtrarPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return filtrarPorFecha;
			} else if (fecha == null && sector != null) {
				final List<Disposicion> filtrarPorSector = this.container
						.allMatches(new QueryDefault<Disposicion>(
								Disposicion.class, "filtrarPorSector",
								"sector", sector));
				this.container.warnUser("Filtrado por Sector.");

				if (filtrarPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return filtrarPorSector;
			} else {
				final List<Disposicion> filtrarFechaSector = this.container
						.allMatches(new QueryDefault<Disposicion>(
								Disposicion.class, "filtrarPorFechaSector",
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
		return sectorRepositorio.listarDisposiciones();
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
