package dom.memo;

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

@DomainService(menuOrder = "2")
@Named("MEMO")
public class MemoRepositorio {
	public final Lock monitor = new ReentrantLock();

	public MemoRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "memo";
	}

	public String iconName() {
		return "memo";
	}

	@NotContributed
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Memo addMemo(
			final @Named("De:") Sector sector,
			final @Named("Sector Destino:") Sector destinoSector,
			@Optional @Named("otro Sector:") String otroSector,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) @MaxLength(255) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		if (!destinoSector.getNombre_sector().contentEquals("OTRO SECTOR"))
			otroSector = "";
		Memo memo = this.nuevoMemo(sector, destinoSector, otroSector,
				descripcion, this.currentUserName(), adjunto);
		if (memo != null)
			return memo;
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	@Programmatic
	private Memo nuevoMemo(final Sector sector, final Sector destinoSector,
			final String otroSector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		try {
			if (monitor.tryLock(25, TimeUnit.MILLISECONDS)) {
				try {
					final Memo unMemo = this.container
							.newTransientInstance(Memo.class);
					Memo anterior = recuperarUltimo();
					Integer nro = Integer.valueOf(1);
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio()) {
							if (!anterior.getHabilitado())
								nro = anterior.getNro_memo();
							else
								nro = anterior.getNro_memo() + 1;
						} else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}

					unMemo.setNro_memo(nro);
					unMemo.setUltimo(true);
					unMemo.setFecha(LocalDate.now().toString("dd/MM/yyyy"));
					unMemo.setAdjuntar(adjunto);
					unMemo.setTipo(2);
					unMemo.setDescripcion(descripcion.toUpperCase().trim());
					unMemo.setHabilitado(true);
					unMemo.setCreadoPor(creadoPor);
					unMemo.setTime(LocalDateTime.now().withMillisOfSecond(3));

					unMemo.setDestinoSector(destinoSector);
					unMemo.setOtroDestino(otroSector);
					unMemo.setSector(sector);

					container.persistIfNotAlready(unMemo);
					container.flush();
					return unMemo;
				} finally {
					monitor.unlock();
				}
			}
		} catch (InterruptedException e) {
			this.container
					.informUser("Verifique que los datos se hayan almacenado");
			e.printStackTrace();
		}
		return null;
	}

	@Programmatic
	private Memo recuperarUltimo() {
		final Memo doc = this.container.firstMatch(new QueryDefault<Memo>(
				Memo.class, "recuperarUltimo"));
		if (doc == null)
			return null;
		return doc;
	}

	@Named("Sector")
	public List<Sector> choices1AddMemo() {
		return sectorRepositorio.listar();

	}

	@Named("Para")
	public List<Sector> choices0AddMemo() {
		List<Sector> lista = sectorRepositorio.listar();
		if (!lista.isEmpty())
			lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
		return lista;
	}

	public Sector default1AddMemo() {
		List<Sector> lista = this.sectorRepositorio.listar();
		if (!lista.isEmpty())
			return lista.get(0);
		return null;
	}

	public String validateAddMemo(final Sector sector, final Sector destino,
			String otro, final String descripcion, final Blob adj) {
		if (!destino.getNombre_sector().contentEquals("OTRO SECTOR"))
			otro = "";
		else if (otro == "" || otro == null)
			return "Ingrese un Sector.";
		return null;

	}

	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////

	@Programmatic
	public List<Memo> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Memo>(Memo.class,
				"autoCompletarDestino", "destinoSector", destino));
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////
	@NotContributed
	@MemberOrder(sequence = "20")
	@Named("Lista de Memo")
	public List<Memo> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Memo> listaMemo = this.container
				.allMatches(new QueryDefault<Memo>(Memo.class, criterio));
		if (listaMemo.isEmpty()) {
			this.container.warnUser("No hay Memos cargados en el sistema");
		}
		return listaMemo;

	}

	public List<Memo> filtrarPorDescripcion(
			final @Named("Descripcion") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion) {
		
		List<Memo> lista = this.listar();
		Memo unMemo = new Memo();
		List<Memo> listaRetorno = new ArrayList<Memo>();
		for(int i=0;i<lista.size();i++)
		{
			unMemo = new Memo();
			unMemo = lista.get(i);
			if(unMemo.getDescripcion().contains(descripcion.toUpperCase()))
				listaRetorno.add(unMemo);
		}
		if (listaRetorno.isEmpty())
			this.container.warnUser("No se encotraron Registros.");
		return listaRetorno;
	}


	/**
	 * PARA MIGRAR
	 */
	@Programmatic
	public Memo insertar(final int nro, final String fecha, final int tipo,
			final Sector sector, final String descripcion, final int eliminado,
			final int ultimo, final Sector destinoSector,
			final String otroDestino, final String fechacompleta) {

		final Memo doc = this.container.newTransientInstance(Memo.class);
		doc.setNro_memo(nro);
		doc.setCreadoPor("root");
		doc.setAdjuntar(null);
		doc.setUltimoDelAnio(false);
		// FECHA :: INICIO
		doc.setFecha(fechacompleta);
		doc.setTime(LocalDateTime.now().withMillisOfSecond(3));
		// FIN :: FECHA

		doc.setTipo(tipo);
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

		if (destinoSector == null)
			doc.setDestinoSector(null);
		else
			doc.setDestinoSector(destinoSector);
		doc.setOtroDestino(otroDestino.toUpperCase());

		container.persistIfNotAlready(doc);
		container.flush();

		return doc;
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
