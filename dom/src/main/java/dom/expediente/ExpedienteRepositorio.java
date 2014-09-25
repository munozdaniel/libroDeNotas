package dom.expediente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "5")
@Named("EXPEDIENTE")
public class ExpedienteRepositorio {
	public final Lock monitor = new ReentrantLock();

	public ExpedienteRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "expediente";
	}

	public String iconName() {
		return "expediente";
	}

	@NotContributed
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Expediente addExpediente(
			final @Named("Inicia: ") Sector sector,
			final @RegEx(validation = "^[a-zA-Z]") @MaxLength(1) @Named("Letra Inicial: ") String expte_cod_letra,
			final @Named("Motivo:") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {
		Expediente expediente = this.nuevoExpediente(expte_cod_letra, sector,
				descripcion, this.currentUserName(), adjunto);
		if (expediente != null)
			return expediente;
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	private Expediente nuevoExpediente(final String expte_cod_letra,
			final Sector sector, final String descripcion,
			final String creadoPor, final Blob adjunto) {
		try {
			if (monitor.tryLock(25, TimeUnit.MILLISECONDS)) {
				try {
					final Expediente unExpediente = this.container
							.newTransientInstance(Expediente.class);
					Expediente anterior = recuperarUltimo();
					int nro = 1;
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio()) {
							if (!anterior.getHabilitado())
								nro = anterior.getNro_expediente();
							else
								nro = anterior.getNro_expediente() + 1;
						} else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}
					unExpediente.setNro_expediente(nro);
					unExpediente.setUltimo(true);

					unExpediente.setExpte_cod_letra(expte_cod_letra);
					unExpediente.setFecha(LocalDate.now());
					unExpediente.setTipo(5);
					unExpediente.setDescripcion(descripcion.toUpperCase()
							.trim());
					unExpediente.setHabilitado(true);
					unExpediente.setCreadoPor(creadoPor);
					unExpediente.setExpte_cod_anio(LocalDate.now().getYear());
					unExpediente.setExpte_cod_empresa("IMPS");
					int anio = LocalDate.now().getYear();
					unExpediente.setExpte_cod_numero((anio - 2010));

					unExpediente.setTime(LocalDateTime.now()
							.withMillisOfSecond(3));
					unExpediente.setAdjuntar(adjunto);
					unExpediente.setSector(sector);

					container.persistIfNotAlready(unExpediente);
					container.flush();
					return unExpediente;
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
	private Expediente recuperarUltimo() {
		final Expediente doc = this.container
				.firstMatch(new QueryDefault<Expediente>(Expediente.class,
						"recuperarUltimo"));
		if (doc == null)
			return null;
		return doc;
	}

	@Programmatic
	private int recuperarNroResolucion() {
		final List<Expediente> expedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						"listarHabilitados"));

		if (expedientes.isEmpty())
			return 0;
		else
			return expedientes.get(expedientes.size() - 1).getNro_expediente();
	}

	@Named("Sector")
	public List<Sector> choices0AddExpediente() {
		return sectorRepositorio.listarExpediente();
	}

	@MemberOrder(sequence = "20")
	@Named("Lista de Expedientes")
	public List<Expediente> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Expediente> listaExpedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						criterio));
		if (listaExpedientes.isEmpty()) {
			this.container
					.warnUser("No hay Expedientes cargados en el sistema");
		}
		return listaExpedientes;

	}

	@Programmatic
	public List<Expediente> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Expediente>(
				Expediente.class, "autoCompletarDestino", "destinoSector",
				destino));
	}

	public List<Expediente> filtrarPorDescripcion(
			final @Named("Descripcion") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion) {
		
		List<Expediente> lista = this.listar();
		Expediente expediente = new Expediente();
		List<Expediente> listaRetorno = new ArrayList<Expediente>();
		for(int i=0;i<lista.size();i++)
		{
			expediente = new Expediente();
			expediente = lista.get(i);
			if(expediente.getDescripcion().contains(descripcion.toUpperCase()))
				listaRetorno.add(expediente);
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
	@Named("Expediente: Filtro por Fecha.")
	@DescribedAs("Seleccione una fecha de inicio y una fecha final.")
	public List<Expediente> filtrarPorFecha(
			final  @Named("Desde:") LocalDate desde, final  @Named("Hasta:") LocalDate hasta) {
			
				final List<Expediente> lista = this.container
						.allMatches(new QueryDefault<Expediente>(Expediente.class,
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
	public Expediente insertar(final int nro, 
			final int tipo, final Sector sector, final String descripcion,
			final int eliminado, final int ultimo, final String empresa,
			final int numero, final int anio, final String letra,
			final LocalDate fechacompleta) {

		final Expediente doc = this.container
				.newTransientInstance(Expediente.class);
		doc.setNro_expediente(nro);

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

		doc.setExpte_cod_empresa(empresa);
		doc.setExpte_cod_numero(numero);
		doc.setExpte_cod_anio(anio);
		doc.setExpte_cod_letra(letra);

		doc.setCreadoPor("root");
		doc.setAdjuntar(null);
		doc.setUltimoDelAnio(false);

		container.persistIfNotAlready(doc);
		container.flush();

		return doc;
	}

}
