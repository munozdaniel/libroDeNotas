package dom.resoluciones;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

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
		return "Tecnico";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Resoluciones addResoluciones(
			final @Named("Nº Resolucion:") int nro_resolucion,
			final @Named("Fecha:") LocalDate fecha,
			final @Named("De: ") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion) {
		return this.nuevoMemo(nro_resolucion, fecha, sector, descripcion,
				this.currentUserName());

	}

	@Programmatic
	private Resoluciones nuevoMemo(int nro_resolucion, LocalDate fecha,
			Sector sector, final String descripcion, String creadoPor) {
		final Resoluciones unMemo = this.container
				.newTransientInstance(Resoluciones.class);
		unMemo.setNro_resolucion(nro_resolucion);
		unMemo.setFecha(fecha);
		unMemo.setTipo(3);
		unMemo.setDescripcion(descripcion.toUpperCase().trim());
		unMemo.setHabilitado(true);
		unMemo.setCreadoPor(creadoPor);
		unMemo.setSector(sector);
		container.persistIfNotAlready(unMemo);
		container.flush();
		return unMemo;
	}

	@Programmatic
	public List<Resoluciones> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Resoluciones>(
				Resoluciones.class, "autoCompletarDestino", "nombreSector",
				destino));
	}

	@Named("Sector")
	public List<Sector> choices2AddResoluciones() {
		return sectorRepositorio.listar();
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////

	@MemberOrder(sequence = "20")
	public List<Resoluciones> listar() {
		final List<Resoluciones> listaMemo = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						"listarHabilitados"));
		if (listaMemo.isEmpty()) {
			this.container.warnUser("No hay tecnicos cargados en el sistema");
		}
		return listaMemo;

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
