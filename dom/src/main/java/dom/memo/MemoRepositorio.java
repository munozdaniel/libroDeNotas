package dom.memo;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
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
	// public final Lock monitor = new ReentrantLock();
	public boolean ocupado = false;

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
	//FIXME: HABRIA QUE QUITAR OTRO SECTOR. CHEQUEAR QUE NO HAYA PROBLEMAS AL HACERLO. EN NOTA TMB.
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Memo addMemo(
			final @Named("De:") Sector sector,
			final @Optional @Named("Sector Destino:") Sector destinoSector,
			final @Named("otro Sector? ") boolean otro,
			@Optional @Named("Destino:") String otroSector,
			final @Named("Descripción:") @MultiLine(numberOfLines = 2) @MaxLength(255) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		// if (!destinoSector.getNombre_sector().contentEquals("OTRO SECTOR"))
		// otroSector = "";
		if (!otro)
			otroSector = "";
		Memo memo = this.nuevoMemo(sector, destinoSector, otroSector,
				descripcion, this.currentUserName(), adjunto);
		if (memo != null) {
			this.container
					.informUser("El Memo ha sido guardado correctamente.");
			return memo;
		}
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	// @Named("Enviar")
	// @MemberOrder(sequence = "10")
	// public Memo enviarMemo(
	// final @Named("De:") Sector sector,
	// final @Named("Sector Destino:") Sector destinoSector,
	// final @Named("otro Sector? ") boolean otro,
	// @Optional @Named("otro Sector:") String otroSector,
	// final @Named("Descripción:") @MultiLine(numberOfLines = 2)
	// @MaxLength(255) String descripcion,
	// final @Optional @Named("Ajuntar:") Blob adjunto) {
	// if (!destinoSector.getNombre_sector().contentEquals("OTRO SECTOR"))
	// otroSector = "";
	// Memo memo = this.nuevoMemo(sector, destinoSector, otroSector,
	// descripcion, this.currentUserName(), adjunto);
	// if (memo != null) {
	// this.container
	// .informUser("El Memo ha sido guardado correctamente.");
	// return memo;
	// }
	// this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
	// return null;
	// }

	public String validateAddMemo(Sector sector, Sector destinoSector,
			boolean otro, String otroSector, String descripcion, Blob adjunto) {
		if (!otro && destinoSector == null)
			return "Seleccione un destino.";
		if (otro && (otroSector == null || otroSector == ""))
			return "Ingrese un Sector Destino.";
		if (!this.ocupado)
			this.ocupado = true;
		else
			return "Sistema ocupado, intente nuevamente.";

		return null;
	}

	// public String validateAddMemo(final Sector sector, final Sector destino,
	// String otro, final String descripcion, final Blob adj) {
	// if (!this.ocupado)
	// this.ocupado = true;
	// else
	// return "Sistema ocupado, intente nuevamente.";
	// if (!destino.getNombre_sector().contentEquals("OTRO SECTOR"))
	// otro = "";
	// else if (otro == "" || otro == null)
	// return "Ingrese un Sector.";
	// return null;
	//
	// }
	public List<Sector> choices1AddMemo(Sector sector, Sector destinoSector,
			boolean otro, String otroSector) {
		if (otro)
			return null;
		else {
			otroSector = "";
			return sectorRepositorio.listar();
		}
	}

	@Programmatic
	private Memo nuevoMemo(final Sector sector, final Sector destinoSector,
			final String otroSector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		// try {
		// if (monitor.tryLock(4, TimeUnit.MILLISECONDS)) {
		try {
			final Memo unMemo = this.container.newTransientInstance(Memo.class);
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
			unMemo.setFecha(LocalDate.now());
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
		} catch (Exception e) {
			container
					.warnUser("Por favor, verifique que la informacion se ha guardado correctamente. En caso contrario informar a Sistemas.");
		} finally {
			// monitor.unlock();
			this.ocupado = false;
		}
		// }
		// } catch (InterruptedException e) {
		// this.container
		// .informUser("Verifique que los datos se hayan almacenado");
		// e.printStackTrace();
		// }
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

	// @Named("Sector")
	// public List<Sector> choices1AddMemo() {
	// return sectorRepositorio.listar();
	//
	// }

	@Named("Para")
	public List<Sector> choices0AddMemo() {
		List<Sector> lista = sectorRepositorio.listar();
		if (!lista.isEmpty())
			lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
		return lista;
	}

	//
	// public Sector default1AddMemo() {
	// List<Sector> lista = this.sectorRepositorio.listar();
	// if (!lista.isEmpty())
	// return lista.get(0);
	// return null;
	// }

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

		List<Memo> lista = this.container.allMatches(new QueryDefault<Memo>(
				Memo.class, "filtrarPorDescripcion", "descripcion", descripcion
						.toUpperCase()));
		if (lista.isEmpty()) {
			this.container.warnUser("No se encontraron Registros.");
		}
		return lista;
	}

	public String validateFiltrarPorDescripcion(final String descripcion) {
		if (descripcion.trim() == "" || descripcion == null)
			return "Por favor, ingrese una descripción.";
		return null;
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
	@Hidden
	public List<Memo> filtrarPorFecha(final @Named("Desde:") LocalDate desde,
			final @Named("Hasta:") LocalDate hasta) {

		final List<Memo> lista = this.container
				.allMatches(new QueryDefault<Memo>(Memo.class,
						"filtrarPorFechas", "desde", desde, "hasta", hasta));
		if (lista.isEmpty()) {
			this.container.warnUser("No se encontraron Registros.");
		}
		return lista;
	}

	@MemberOrder(sequence = "30")
	@Named("Filtro por Sector y Fecha")
	public List<Memo> filtrarCompleto(
			final @DescribedAs("Seleccione un Destino") @Named("Sector Origen") @Optional Sector origen,
			final @DescribedAs("Seleccione un Origen") @Named("Sector Destino") @Optional String destino,
			final @Optional @Named("Desde:") LocalDate desde,
			final @Optional @Named("Hasta:") LocalDate hasta) {
		List<Memo> lista = new ArrayList<Memo>();

		Sector sectorDestino = sectorRepositorio.buscarPorNombre(destino);

		// Todos ===========================================================
		if (origen != null && destino != null && desde != null && hasta != null)
			if (sectorDestino != null)
				lista = this.container
						.allMatches(new QueryDefault<Memo>(Memo.class,
								"filtrarCompleto", "origen", origen,
								"sectorDestino", sectorDestino, "otroDestino",
								destino.toUpperCase(), "desde", desde, "hasta",
								hasta));
			else
				lista = this.container.allMatches(new QueryDefault<Memo>(
						Memo.class, "filtrarCompletoOtroDestino", "origen",
						origen, "otroDestino", destino.toUpperCase(), "desde",
						desde, "hasta", hasta));

		else {
			// solo las fechas ============================================
			if (origen == null && destino == null && desde != null
					&& hasta != null)
				lista = this.filtrarPorFecha(desde, hasta);
			else {
				// solo origen =============================================
				if (origen != null && destino == null && desde == null
						&& hasta == null)
					lista = this.container.allMatches(new QueryDefault<Memo>(
							Memo.class, "filtrarOrigen", "origen", origen));
				else {
					// solo destino ========================================
					if (origen == null && destino != null && desde == null
							&& hasta == null) {
						if (sectorDestino != null)
							lista = this.container
									.allMatches(new QueryDefault<Memo>(
											Memo.class, "filtrarDestino",
											"sectorDestino", sectorDestino,
											"otroDestino", destino
													.toUpperCase()));
						else
							lista = this.container
									.allMatches(new QueryDefault<Memo>(
											Memo.class, "filtrarOtroDestino",
											"otroDestino", destino
													.toUpperCase()));

					} else {
						// fecha y Origen ===================================
						if (origen != null && destino == null && desde != null
								&& hasta != null)
							lista = this.container
									.allMatches(new QueryDefault<Memo>(
											Memo.class, "filtrarFechaYOrigen",
											"origen", origen, "desde", desde,
											"hasta", hasta));
						else {
							// fecha y Destino ==============================
							if (origen == null && destino != null
									&& desde != null && hasta != null)
								if (sectorDestino != null)
									lista = this.container
											.allMatches(new QueryDefault<Memo>(
													Memo.class,
													"filtrarFechaYDestino",
													"sectorDestino",
													sectorDestino,
													"otroDestino", destino
															.toUpperCase(),
													"desde", desde, "hasta",
													hasta));
								else
									lista = this.container
											.allMatches(new QueryDefault<Memo>(
													Memo.class,
													"filtrarFechaYOtroDestino",
													"otroDestino", destino
															.toUpperCase(),
													"desde", desde, "hasta",
													hasta));

							else {
								// Origen y Destino ============================
								if (origen != null && destino != null
										&& desde == null && hasta == null)
									if (sectorDestino != null)
										lista = this.container
												.allMatches(new QueryDefault<Memo>(
														Memo.class,
														"filtrarOrigenYDestino",
														"origen", origen,
														"sectorDestino",
														sectorDestino,
														"otroDestino", destino
																.toUpperCase()));

									else
										lista = this.container
												.allMatches(new QueryDefault<Memo>(
														Memo.class,
														"filtrarOrigenYOtroDestino",
														"origen", origen,
														"otroDestino", destino
																.toUpperCase()));
							}

						}
					}
				}
			}
		}

		if (lista.isEmpty())
			this.container.informUser("NO SE ENCONTRARON REGISTROS.");

		return lista;

	}

	public String validateFiltrarCompleto(Sector origen, String destino,
			LocalDate desde, LocalDate hasta) {
		if ((desde != null && hasta == null))
			return "Por favor, ingrese una fecha final estimativa.";
		else if (desde == null && hasta != null)
			return "Por favor, ingrese una fecha inicial estimativa.";
		else if (origen == null && destino == null && desde == null
				&& hasta == null)
			return "Por favor, ingrese datos para realizar la busqueda.";
		return null;
	}

	public List<Sector> choices0FiltrarCompleto() {//Habria que eliminar el primer elemento? OTRO SECTOR? o eliminarlo de la BD
		return sectorRepositorio.listar();
	}

	/******************************************************************************
	 * PARA MIGRAR
	 */
	@Programmatic
	public Memo insertar(final int nro, final int tipo, final Sector sector,
			final String descripcion, final int eliminado, final int ultimo,
			final Sector destinoSector, final String otroDestino,
			final LocalDate fechacompleta) {

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
