package dom.disposiciones;

import java.util.ArrayList;
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
import org.apache.isis.applib.annotation.NotContributed;
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

	@NotContributed
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Disposicion addDisposicion(
			final @Named("Sector") Sector sector,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) @MaxLength(255) String descripcion,
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
			if (monitor.tryLock(25, TimeUnit.MILLISECONDS)) {
				try {
					Disposicion anterior = recuperarUltimo();
					Integer nro = Integer.valueOf(1);
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio()) {
							if (!anterior.getHabilitado())
								nro = anterior.getNro_Disposicion();
							else
								nro = anterior.getNro_Disposicion() + 1;
						} else
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

	public List<Disposicion> filtrarPorDescripcion(
			final @Named("Descripcion") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion) {
		
		List<Disposicion> lista = this.listar();
		Disposicion disposicion = new Disposicion();
		List<Disposicion> listaRetorno = new ArrayList<Disposicion>();
		for(int i=0;i<lista.size();i++)
		{
			disposicion = new Disposicion();
			disposicion = lista.get(i);
			if(disposicion.getDescripcion().contains(descripcion.toUpperCase()))
				listaRetorno.add(disposicion);
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
	public Disposicion insertar(final int nro, 
			final int tipo, final Sector sector, final String descripcion,
			final int eliminado, final int ultimo, final LocalDate fechacompleta) {

		final Disposicion doc = this.container
				.newTransientInstance(Disposicion.class);
		doc.setNro_Disposicion(nro);

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
