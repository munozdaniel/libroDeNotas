package dom.resoluciones;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
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
		unaResolucion.setUltimo(false);
		unaResolucion.setUltimoDelAnio(false);
		unaResolucion.setNro_resolucion(nro_resolucion);
		unaResolucion.setFecha(fecha.toString("dd/MM/yyyy"));
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
	public Resoluciones insertar(final int nro, final String fecha,
			final int tipo, final Sector sector, final String descripcion,
			final int eliminado, final int ultimo, final String fechacompleta) {

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
