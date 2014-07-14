package dom.disposiciones;

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
	public Disposicion addDisposicion(final @Named("Sector") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion) {
		return this.nuevaDisposicion(sector,descripcion, this.currentUserName());

	}

	@Programmatic
	private Disposicion nuevaDisposicion(final Sector sector,String descripcion, String creadoPor) {
		final Disposicion unaDisposicion = this.container.newTransientInstance(Disposicion.class);
		int nro = recuperarNroMemo();
		nro += 1;
		@SuppressWarnings("resource")
		Formatter formato = new Formatter();
		formato.format("%04d", nro);
		unaDisposicion.setNro_Disposicion(Integer.parseInt(000 + formato.toString()));
		unaDisposicion.setFecha(LocalDate.now());
		unaDisposicion.setTipo(4);
		unaDisposicion.setDescripcion(descripcion.toUpperCase().trim());
		unaDisposicion.setHabilitado(true);
		unaDisposicion.setCreadoPor(creadoPor);
//		unaDisposicion.setSector(sector);// hay que devolver el sector del usuario que tiene acceso.
		sector.addToDocumento(unaDisposicion);
		container.persistIfNotAlready(unaDisposicion);
		container.flush();
		return unaDisposicion;
	}

	@Named("Sector")
	public List<Sector> choices0AddDisposicion() {
		return sectorRepositorio.listarDisposiciones(); // TODO: return list of choices for
											// property
	}

	@Programmatic
	private int recuperarNroMemo() {
		final Disposicion disposicion = this.container.firstMatch(new QueryDefault<Disposicion>(
				Disposicion.class, "buscarUltimaDisposicionTrue"));
		if (disposicion == null)
			return 0;
		else
			return disposicion.getNro_Disposicion();
	}

	@Programmatic
	public List<Disposicion> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
				"autoCompletarDestino", "destinoSector", destino));
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
