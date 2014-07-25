package dom.expediente;


import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.expediente.Expediente.Letras;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("EXPEDIENTE")
public class ExpedienteRepositorio {

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
			final @Named("Codigo: ") @MaxLength(1) Letras expte_cod_letra,
			final  @Named("Motivo:") String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		return this.nuevoExpediente(expte_cod_letra, sector, descripcion,
				this.currentUserName(),adjunto);

	}

	private Expediente nuevoExpediente(final Letras expte_cod_letra,
			final Sector sector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		final Expediente unExpediente = this.container
				.newTransientInstance(Expediente.class);
		int nro = recuperarNroResolucion();
		nro += 1;
		formato = new Formatter();
		formato.format("%04d", nro);
		unExpediente.setNro_expediente(Integer.parseInt(000 + formato
				.toString()));
		unExpediente.setExpte_cod_letra(expte_cod_letra);
		unExpediente.setFecha(LocalDate.now());
		unExpediente.setTipo(5);
		unExpediente.setDescripcion(descripcion.toUpperCase().trim());
		unExpediente.setHabilitado(true);
		unExpediente.setCreadoPor(creadoPor);
		unExpediente.setExpte_cod_anio(LocalDate.now().getYear());
		unExpediente.setExpte_cod_empresa("IMPS");
		unExpediente.setTime(LocalDateTime.now().withMillisOfSecond(3));
		// unExpediente.setSector(sector);
		unExpediente.setAdjuntar(adjunto);
		sector.addToDocumento(unExpediente);
		container.persistIfNotAlready(unExpediente);
		container.flush();
		return unExpediente;
	}

	// @Programmatic
	// private int recuperarNroResolucion() {
	// final Expediente expediente = this.container
	// .firstMatch(new QueryDefault<Expediente>(Expediente.class,
	// "buscarUltimoExpedienteTrue"));
	// if (expediente == null)
	// return 0;
	// else
	// return expediente.getNro_expediente();
	// }
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
	public List<Expediente> listar() {
		final List<Expediente> listaExpedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						"listarHabilitados"));
		if (listaExpedientes.isEmpty()) {
			this.container.warnUser("No hay Expedientes cargados en el sistema");
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
			final @Optional @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("De:") Sector sector,
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
	private Formatter formato;
}
