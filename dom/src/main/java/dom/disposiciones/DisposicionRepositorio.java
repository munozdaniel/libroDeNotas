package dom.disposiciones;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
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

@Named("DISPOSICION")
public class DisposicionRepositorio {

	public DisposicionRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "disposicion";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Disposicion addDisposicion(
			final @Named("Sector") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion,
			final @Optional Blob adjunto) {
		return this.nuevaDisposicion(sector, descripcion,
				this.currentUserName(), adjunto);

	}

	@Programmatic
	private Disposicion nuevaDisposicion(final Sector sector,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		final Disposicion unaDisposicion = this.container
				.newTransientInstance(Disposicion.class);
		int nro = recuperarNroDisposicion();
		nro += 1;
		@SuppressWarnings("resource")
		Formatter formato = new Formatter();
		formato.format("%04d", nro);
		unaDisposicion.setNro_Disposicion(Integer.parseInt(000 + formato
				.toString()));
		unaDisposicion.setFecha(LocalDate.now());
		unaDisposicion.setTipo(4);
		unaDisposicion.setAdjuntar(adjunto);
		unaDisposicion.setDescripcion(descripcion.toUpperCase().trim());
		unaDisposicion.setHabilitado(true);
		unaDisposicion.setCreadoPor(creadoPor);
		// unaDisposicion.setSector(sector);// hay que devolver el sector del
		// usuario que tiene acceso.
		unaDisposicion.setTime(LocalDateTime.now().withMillisOfSecond(3));
		sector.addToDocumento(unaDisposicion);
		container.persistIfNotAlready(unaDisposicion);
		container.flush();
		return unaDisposicion;
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

	@MemberOrder(sequence = "20")
	public List<Disposicion> listar() {
		final List<Disposicion> listaMemo = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
						"listar"));
		if (listaMemo.isEmpty()) {
			this.container.warnUser("No hay tecnicos cargados en el sistema");
		}
		return listaMemo;

	}

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////

	@MemberOrder(sequence = "30")
	public List<Disposicion> filtrar(
			final @Optional @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("De:") Sector sector,
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
