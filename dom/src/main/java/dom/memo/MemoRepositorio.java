package dom.memo;

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
			if (monitor.tryLock(1, TimeUnit.SECONDS)) {
				try {
					final Memo unMemo = this.container
							.newTransientInstance(Memo.class);
					Memo anterior = recuperarUltimo();
					Integer nro = Integer.valueOf(1);
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio())
							nro = anterior.getNro_memo() + 1;
						else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}

					unMemo.setNro_memo(nro);
					unMemo.setUltimo(true);

					unMemo.setFecha(LocalDate.now());
					unMemo.setAdjuntar(adjunto);
					unMemo.setTipo(2);
					unMemo.setDescripcion(descripcion.toUpperCase().trim());
					unMemo.setHabilitado(true);
					unMemo.setCreadoPor(creadoPor);
					unMemo.setSector(sector);
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
		List<Sector> lista = sectorRepositorio.listar();
		return lista;
	}

	@Named("Para")
	public List<Sector> choices0AddMemo() {
		return sectorRepositorio.listar();
	}

	public Sector default1AddMemo() {
		return this.sectorRepositorio.listar().get(0);
	}

	public String validateAddMemo(final Sector sector, final Sector destino,
			String otro, final String descripcion, final Blob adj) {
		if (!destino.getNombre_sector().contentEquals("OTRO SECTOR"))
			otro = "";

		return null;

	}

	@Programmatic
	private int recuperarNroMemo() {
		final List<Memo> memos = this.container
				.allMatches(new QueryDefault<Memo>(Memo.class,
						"listarHabilitados"));

		if (memos.isEmpty())
			return 0;
		else
			return memos.get(memos.size() - 1).getNro_memo();
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

	// //////////////////////////////////////
	// Filtrar por Fecha o Sector
	// //////////////////////////////////////

	@MemberOrder(sequence = "30")
	public List<Memo> filtrar(final @Optional @Named("De:") Sector sector,
			final @Optional @Named("Fecha") LocalDate fecha) {
		if (fecha == null && sector == null) {
			this.container.warnUser("Sin Filtro");
			return this.listar();

		} else {
			if (fecha != null && sector == null) {
				final List<Memo> filtrarPorFecha = this.container
						.allMatches(new QueryDefault<Memo>(Memo.class,
								"filtrarPorFecha", "fecha", fecha));

				if (filtrarPorFecha.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				this.container.warnUser("Filtrado por Fechas.");

				return filtrarPorFecha;
			} else if (fecha == null && sector != null) {
				final List<Memo> filtrarPorSector = this.container
						.allMatches(new QueryDefault<Memo>(Memo.class,
								"filtrarPorSector", "sector", sector));
				this.container.warnUser("Filtrado por Sector.");

				if (filtrarPorSector.isEmpty()) {
					this.container.warnUser("No se encontraron Notas.");
				}
				return filtrarPorSector;
			} else {
				final List<Memo> filtrarFechaSector = this.container
						.allMatches(new QueryDefault<Memo>(Memo.class,
								"filtrarPorFechaSector", "fecha", fecha,
								"sector", sector));
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
		return sectorRepositorio.listar();
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
