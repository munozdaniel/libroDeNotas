package dom.nota;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;
import dom.tecnico.Tecnico;

@Named("Notas")
public class NotaRepositorio {

	public NotaRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "nota";
	}

	public String iconName() {
		return "Tecnico";
	}

	/**
	 * addNota
	 * 
	 * @param sector
	 * @param destino
	 * @param descripcion
	 * @return
	 */
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Nota addNota(
			final @Optional @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("De:") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Para:") String destino,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion) {
		// return nuevaNota(sector, destino, descripcion);
		return nuevaNota(sector, destino, descripcion, this.currentUserName());
	}

	@Programmatic
	private Nota nuevaNota(final Sector sector, final String destino,
			final String descripcion, final String currentUserName) {
		final Nota unaNota = this.container.newTransientInstance(Nota.class);
		int nro = recuperarNroNota();
		nro += 1;
		formato = new Formatter();
		formato.format("%04d", nro);
		unaNota.setNro_nota(Integer.parseInt(formato.toString()));
		unaNota.setFecha(LocalDate.now());
		unaNota.setTipo(1);
		unaNota.setDescripcion(descripcion.toUpperCase().trim());
		unaNota.setHabilitado(true);
		container.persistIfNotAlready(unaNota);
		container.flush();
		return unaNota;
	}

	@Programmatic
	private int recuperarNroNota() {
		final Integer nroNota = this.container
				.uniqueMatch(new QueryDefault<Integer>(Integer.class,
						"buscarUltimaNotaTrue"));
		if (nroNota == null || nroNota == 0)
			return 0;
		else
			return nroNota;
	}

	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////

	@Named("Sector")
	@DescribedAs("Buscar el Sector en mayuscula")
	public List<Sector> autoComplete0AddNota(
			final @MinLength(2) String search) {
		return sectorRepositorio.autoComplete(search);
	}

	@Programmatic
	public List<Nota> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Nota>(Nota.class,
				"autoCompletarDestino",
				"destino",destino));
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
