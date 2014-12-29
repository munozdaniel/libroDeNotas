package dom.resoluciones;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
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
	public boolean ocupado = false;

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

	@NotContributed
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Resoluciones addResoluciones(
			final @Named("Fecha:")  LocalDate fecha,
			final @Named("De: ") Sector sector,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) @MaxLength(255) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		return this.nuevaResolucion(fecha, sector, descripcion,
				this.currentUserName(), adjunto);

	}

	public String validateAddResoluciones(final LocalDate fecha,
			final Sector sector, final String descripcion, final Blob adjunto) {

		if (fecha.getYear() < LocalDate.now().getYear())
			return "Fecha Incorrecta: El Año no debe ser menor al año actual.";
		else {
			if (!this.ocupado) {
				this.ocupado = true;
				return null;
			} else
				return "Sistema ocupado, intente nuevamente.";
		}

	}

	@Programmatic
	private Resoluciones nuevaResolucion(final LocalDate fecha,
			final Sector sector, final String descripcion,
			final String creadoPor, final Blob adjunto) {

		try {
			final Resoluciones unaResolucion = this.container
					.newTransientInstance(Resoluciones.class);
			Integer nro = Integer.valueOf(1);

			Resoluciones resolucionAnterior = recuperarElUltimo();

			if (resolucionAnterior != null) {
				if (!resolucionAnterior.getUltimoDelAnio()) {
					if (!resolucionAnterior.getHabilitado())
						nro = resolucionAnterior.getNro_resolucion();
					else
						nro = resolucionAnterior.getNro_resolucion() + 1;
				} else
					resolucionAnterior.setUltimoDelAnio(false);
				resolucionAnterior.setUltimo(false);
			}
			unaResolucion.setDescripcion(descripcion.toUpperCase().trim());
			// if (unaResolucion.getDescripcion().equalsIgnoreCase("ALGO")) {
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
			unaResolucion.setNro_resolucion(nro);
			unaResolucion.setUltimo(true);
			unaResolucion.setUltimoDelAnio(false);
			unaResolucion.setFecha(fecha);
			unaResolucion.setTipo(3);
			unaResolucion.setHabilitado(true);
			unaResolucion.setCreadoPor(creadoPor);
			unaResolucion.setAdjuntar(adjunto);
			unaResolucion.setTime(LocalDateTime.now().withMillisOfSecond(3));
			unaResolucion.setSector(sector);

			container.persistIfNotAlready(unaResolucion);
			container.flush();
			return unaResolucion;
		} catch (Exception e) {
			container
					.warnUser("Por favor, verifique que la informacion se ha guardado correctamente. En caso contrario informar a Sistemas.");
		} finally {
			// monitor.unlock();
			this.ocupado = false;
		}
		return null;

	}

	private Resoluciones recuperarElUltimo() {
		final Resoluciones resoluciones = this.container
				.firstMatch(new QueryDefault<Resoluciones>(Resoluciones.class,
						"recuperarUltimo"));
		if (resoluciones == null)
			return null;
		return resoluciones;
	}

	@Programmatic
	public List<Resoluciones> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Resoluciones>(
				Resoluciones.class, "autoCompletarDestino", "nombreSector",
				destino));
	}

	@Named("Sector")
	public List<Sector> choices1AddResoluciones() {
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

	public List<Resoluciones> filtrarPorDescripcion(
			final @Named("Descripcion") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion) {

		List<Resoluciones> lista = this.listar();
		Resoluciones unaResolucion = new Resoluciones();
		List<Resoluciones> listaRetorno = new ArrayList<Resoluciones>();
		for (int i = 0; i < lista.size(); i++) {
			unaResolucion = new Resoluciones();
			unaResolucion = lista.get(i);
			if (unaResolucion.getDescripcion().contains(
					descripcion.toUpperCase()))
				listaRetorno.add(unaResolucion);
		}
		if (listaRetorno.isEmpty())
			this.container.warnUser("No se encotraron Registros.");
		return listaRetorno;
	}

	/**
	 * Filtrar por fecha
	 * 
	 * @param sector
	 * @param fecha
	 * @return
	 */
	@MemberOrder(sequence = "30")
	@Named("Filtro por Fecha")
	@DescribedAs("Seleccione una fecha de inicio y una fecha final.")
	public List<Resoluciones> filtrarPorFecha(
			final @Named("Desde:") LocalDate desde,
			final @Named("Hasta:") LocalDate hasta) {

		final List<Resoluciones> lista = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						"filtrarPorFechas", "desde", desde, "hasta", hasta));
		if (lista.isEmpty()) {
			this.container.warnUser("No se encontraron Registros.");
		}
		return lista;
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

	/**
	 * PARA MIGRAR
	 */
	@Programmatic
	public Resoluciones insertar(final int nro, final int tipo,
			final Sector sector, final String descripcion, final int eliminado,
			final int ultimo, final LocalDate fechacompleta) {

		final Resoluciones doc = this.container
				.newTransientInstance(Resoluciones.class);
		doc.setNro_resolucion(nro);

		// FECHA :: INICIO
		doc.setFecha(fechacompleta);
		doc.setTime(LocalDateTime.now().withMillisOfSecond(3));
		// FIN :: FECHA

		doc.setTipo(tipo);
		if (sector != null)
			doc.setSector(sector);
		doc.setDescripcion(descripcion.toUpperCase().trim());
		if (eliminado == 0)
			doc.setHabilitado(true);
		else
			doc.setHabilitado(false);

		if (ultimo == 0)
			doc.setUltimo(false);
		else
			doc.setUltimo(true);

		doc.setCreadoPor("root");
		doc.setAdjuntar(null);
		doc.setUltimoDelAnio(false);

		container.persistIfNotAlready(doc);
		container.flush();

		return doc;
	}
}
