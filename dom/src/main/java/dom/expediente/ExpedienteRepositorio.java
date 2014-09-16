package dom.expediente;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "5")
@Named("EXPEDIENTE")
public class ExpedienteRepositorio {
	public final Lock monitor = new ReentrantLock();

	public ExpedienteRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "expediente";
	}

	public String iconName() {
		return "expediente";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Expediente addExpediente(
			final @Named("Inicia: ") Sector sector,
			final @RegEx(validation = "^[a-zA-Z]") @MaxLength(1) @Named("Letra Inicial: ") String expte_cod_letra,
			final @Named("Motivo:") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		Expediente expediente = this.nuevoExpediente(expte_cod_letra, sector,
				descripcion, this.currentUserName(), adjunto);
		if (expediente != null)
			return expediente;
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	private Expediente nuevoExpediente(final String expte_cod_letra,
			final Sector sector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		try {
			if (monitor.tryLock(1, TimeUnit.SECONDS)) {
				try {
					final Expediente unExpediente = this.container
							.newTransientInstance(Expediente.class);
					Expediente anterior = recuperarUltimo();
					int nro = 1;
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio())
							nro = anterior.getNro_expediente() + 1;
						else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}
					unExpediente.setNro_expediente(nro);
					unExpediente.setUltimo(true);

					unExpediente.setExpte_cod_letra(expte_cod_letra);
					unExpediente.setFecha(LocalDate.now());
					unExpediente.setTipo(5);
					unExpediente.setDescripcion(descripcion.toUpperCase()
							.trim());
					unExpediente.setHabilitado(true);
					unExpediente.setCreadoPor(creadoPor);
					unExpediente.setExpte_cod_anio(LocalDate.now().getYear());
					unExpediente.setExpte_cod_empresa("IMPS");
					unExpediente
							.setExpte_cod_numero((LocalDate.now().getYear() + "")
									.charAt(3));

					unExpediente.setTime(LocalDateTime.now()
							.withMillisOfSecond(3));
					unExpediente.setAdjuntar(adjunto);
					unExpediente.setSector(sector);

					container.persistIfNotAlready(unExpediente);
					container.flush();
					return unExpediente;
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
	private Expediente recuperarUltimo() {
		final Expediente doc = this.container
				.firstMatch(new QueryDefault<Expediente>(Expediente.class,
						"recuperarUltimo"));
		if (doc == null)
			return null;
		return doc;
	}

	@Programmatic
	private int recuperarNroResolucion() {
		final List<Expediente> expedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						"listarHabilitados"));

		if (expedientes.isEmpty())
			return 0;
		else
			return expedientes.get(expedientes.size() - 1).getNro_expediente();
	}

	@Named("Sector")
	public List<Sector> choices0AddExpediente() {
		return sectorRepositorio.listarExpediente();
	}

	@MemberOrder(sequence = "20")
	@Named("Lista de Expedientes")
	public List<Expediente> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Expediente> listaExpedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						criterio));
		if (listaExpedientes.isEmpty()) {
			this.container
					.warnUser("No hay Expedientes cargados en el sistema");
		}
		return listaExpedientes;

	}

	@Programmatic
	public List<Expediente> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Expediente>(
				Expediente.class, "autoCompletarDestino", "destinoSector",
				destino));
	}

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////

	@MemberOrder(sequence = "30")
	public List<Expediente> filtrar(
			final @Optional @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				final List<Expediente> filtrarPorFecha = this.container
						.allMatches(new QueryDefault<Expediente>(
								Expediente.class, "filtrarPorFecha", "fecha",
								fecha));

				if (filtrarPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return filtrarPorFecha;
			} else if (fecha == null && sector != null) {
				final List<Expediente> filtrarPorSector = this.container
						.allMatches(new QueryDefault<Expediente>(
								Expediente.class, "filtrarPorSector", "sector",
								sector));
				this.container.warnUser("Filtrado por Sector.");

				if (filtrarPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return filtrarPorSector;
			} else {
				final List<Expediente> filtrarFechaSector = this.container
						.allMatches(new QueryDefault<Expediente>(
								Expediente.class, "filtrarPorFechaSector",
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
		return sectorRepositorio.listarExpediente();
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
