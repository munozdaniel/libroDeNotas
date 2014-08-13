package dom.memo;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("MEMO")
public class MemoRepositorio {

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

	// @CssClass("x-prueba")
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Memo addMemo(final @Named("De:") Sector sector,
			final @Named("Sector Destino:") Sector destinoSector,
			@Optional @Named("otro Sector:") String otroSector,
			final @Named("Descripción:") String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		if (!destinoSector.getNombre_sector().contentEquals("OTRO SECTOR"))
			otroSector = "";
		return this.nuevoMemo(sector, destinoSector, otroSector, descripcion,
				this.currentUserName(), adjunto);

	}

	@Named("Sector")
	public List<Sector> choices1AddMemo() {
		List<Sector> lista = sectorRepositorio.listar();
		// Sector unSector = new Sector();
		// unSector.setNombre_sector("OTRO SECTOR");
		// unSector.setResponsable("INFORMATICA");
		// unSector.setResolucion(true);
		// unSector.setDisposicion(true);
		// unSector.setExpediente(true);
		// unSector.setCreadoPor("root");
		// unSector.setHabilitado(true);
		// lista.add(unSector);
		return lista;
	}

	@Named("Para")
	public List<Sector> choices0AddMemo() {
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
	}


	public Sector default1AddMemo() {
		return this.sectorRepositorio.listar().get(1);
	}

	public String validateAddMemo(final Sector sector, final Sector destino,
			 String otro, final String descripcion, final Blob adj) {
		 if(!destino.getNombre_sector().contentEquals("OTRO SECTOR") )
			 otro = "";
		 
			 
			 return  null;
		 
		
	}
	// @Named("Disable")
	// public Sector disableAddMemo(final Sector sector) {
	// if(sector.getNombre_sector().contentEquals("OTRO SECTOR")) {
	// return null;
	// }
	// return sector;
	// }
	@Programmatic
	private Memo nuevoMemo(final Sector sector, final Sector destinoSector,
			final String otroSector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		final Memo unMemo = this.container.newTransientInstance(Memo.class);
		Memo memoAnterior = recuperarUltimo();
		int nro = 0;
		if (memoAnterior != null) {
			nro = memoAnterior.getNro_memo() + 1;
			memoAnterior.setUltimo(false);

		}
		formato = new Formatter();
		formato.format("%04d", nro);
		unMemo.setNro_memo(Integer.parseInt(000 + formato.toString()));
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
		// if (otroSector != "")
		unMemo.setOtroDestino(otroSector);
		sector.addToDocumento(unMemo);
		container.persistIfNotAlready(unMemo);
		container.flush();
		return unMemo;
	}
	@Programmatic
	private Memo recuperarUltimo() {
		final Memo doc = this.container.firstMatch(new QueryDefault<Memo>(
				Memo.class, "recuperarUltimo"));
		if (doc == null)
			return null;
		else
			return doc;
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

	// @Named("Sector")
	// @DescribedAs("Buscar el Sector en mayuscula")
	// public List<Sector> autoComplete0AddMemo(final @MinLength(2) String
	// search) {
	// return sectorRepositorio.autoComplete(search);
	// }

	
	@Programmatic
	public List<Memo> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Memo>(Memo.class,
				"autoCompletarDestino", "destinoSector", destino));
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////

	@MemberOrder(sequence = "20")
	public List<Memo> listar() {
		final List<Memo> listaMemo = this.container
				.allMatches(new QueryDefault<Memo>(Memo.class,
						"listarHabilitados"));
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
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
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
