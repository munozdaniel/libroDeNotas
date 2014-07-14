package dom.expediente;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import dom.memo.Memo;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("EXPEDIENTE")
public class ExpedienteRepositorio {

	public ExpedienteRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "expediente";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Expediente addExpediente(
			final @Named("Inicia: ") Sector sector,
			final @Named("Codigo: ") @MaxLength(1) String expte_cod_letra,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Motivo:") String descripcion) {
		return this
				.nuevoExpediente(expte_cod_letra,sector, descripcion, this.currentUserName());

	}

	private Expediente nuevoExpediente(final String expte_cod_letra,final Sector sector,
			final String descripcion, final String creadoPor) {
		final Expediente unExpediente = this.container
				.newTransientInstance(Expediente.class);
		int nro = recuperarNroResolucion();
		nro += 1;
		formato = new Formatter();
		formato.format("%04d", nro);
		unExpediente.setNro_expediente(Integer.parseInt(000 + formato
				.toString()));
		unExpediente.setExpte_cod_letra(expte_cod_letra);
		unExpediente.setFecha(LocalDate.now());
		unExpediente.setTipo(5);
		unExpediente.setDescripcion(descripcion.toUpperCase().trim());
		unExpediente.setHabilitado(true);
		unExpediente.setCreadoPor(creadoPor);
		unExpediente.setExpte_cod_anio(LocalDate.now().getYear());
		unExpediente.setExpte_cod_empresa("IMPS");
//		unExpediente.setSector(sector);
		sector.addToDocumento(unExpediente);
		container.persistIfNotAlready(unExpediente);
		container.flush();
		return unExpediente;
	}

	@Programmatic
	private int recuperarNroResolucion() {
		final Expediente expediente = this.container
				.firstMatch(new QueryDefault<Expediente>(Expediente.class,
						"buscarUltimoExpedienteTrue"));
		if (expediente == null)
			return 0;
		else
			return expediente.getNro_expediente();
	}

	@Named("Sector")
	public List<Sector> choices0AddExpediente() {
		return sectorRepositorio.listarExpediente();
	}

	@MemberOrder(sequence = "20")
	public List<Expediente> listar() {
		final List<Expediente> listaExpedientes = this.container
				.allMatches(new QueryDefault<Expediente>(Expediente.class,
						"listarHabilitados"));
		if (listaExpedientes.isEmpty()) {
			this.container.warnUser("No hay tecnicos cargados en el sistema");
		}
		return listaExpedientes;

	}

	@Programmatic
	public List<Memo> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Memo>(Memo.class,
				"autoCompletarDestino", "destinoSector", destino));
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
