package dom.nota;

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
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "1")
@Named("Notas")
public class NotaRepositorio {
	public final Lock monitor = new ReentrantLock();

	public NotaRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "nota";
	}

	public String iconName() {
		return "nota";
	}

	/**
	 * addNota
	 * 
	 * @param sector
	 * @param destino
	 * @param descripcion
	 * @return
	 */
	@NotContributed
	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Nota addNota(
			final @Named("De:") Sector sector,
			final @Named("Para:") String destino,
			final @Named("Descripción:") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion,
			final @Optional @Named("Ajuntar:") Blob adjunto) {

		Nota nota = nuevaNota(sector, destino, descripcion,
				this.currentUserName(), adjunto);
		if (nota != null)
			return nota;

		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;

	}

	@Programmatic
	private Nota nuevaNota(final Sector sector, final String destino,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		try {
			if (monitor.tryLock(25, TimeUnit.MILLISECONDS)) {
				try {
					final Nota unaNota = this.container
							.newTransientInstance(Nota.class);
					Integer nro = Integer.valueOf(1);

					Nota notaAnterior = recuperarElUltimo();
					/*
					 * Si es nulo => cero Si no es nulo, si no es el
					 * ulitmoDelAnio y si no esta habilitado => igual Si no es
					 * nulo y si no es el ultimoDelAnio y esta Habilitado =>
					 * suma Si no es nulo y si es el ulitmo del Anio => cero
					 */
					if (notaAnterior != null) {
						if (!notaAnterior.getUltimoDelAnio()) {
							if (!notaAnterior.getHabilitado())
								nro = notaAnterior.getNro_nota();
							else
								nro = notaAnterior.getNro_nota() + 1;
						} else
							notaAnterior.setUltimoDelAnio(false);
						notaAnterior.setUltimo(false);
					}
					// if (unaNota.getDescripcion().equalsIgnoreCase("ALGO")) {
					// try {
					// Thread.sleep(11000);
					// } catch (InterruptedException e) {
					//
					// }
					//
					// }
					// Si no habian nota, o si es el ultimo del año, el proximo
					// nro
					// comienza en 1.

					unaNota.setDescripcion(descripcion.toUpperCase().trim());
					unaNota.setUltimo(true);
					unaNota.setNro_nota(nro);
					unaNota.setFecha(LocalDate.now());
					unaNota.setTipo(1);
					unaNota.setCreadoPor(creadoPor);
					unaNota.setDestino(destino);
					unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
					unaNota.setAdjuntar(adjunto);
					unaNota.setSector(sector);
					unaNota.setHabilitado(true);

					container.persistIfNotAlready(unaNota);
					container.flush();

					return unaNota;
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
	@NotInServiceMenu
	private Nota recuperarElUltimo() {

		final Nota nota = this.container.firstMatch(new QueryDefault<Nota>(
				Nota.class, "recuperarUltimo"));
		if (nota == null)
			return null;
		return nota;

	}

	@Named("Sector")
	public List<Sector> choices0AddNota() {
		List<Sector> lista = sectorRepositorio.listar();
		if (!lista.isEmpty())
			lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
		return lista;
	}

	@Programmatic
	public List<Nota> autoComplete(final String destino) {
		String criterio = "autoCompletarDestino";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "autoComplete";
		return container.allMatches(new QueryDefault<Nota>(Nota.class,
				criterio, "destino", destino));
	}

	/**
	 * Listar todas las notas, dependera del usuario y sus roles. Optimizar las
	 * busquedas por usuario D:
	 * 
	 * @return
	 */
	@Paged(12)
	@MemberOrder(sequence = "20")
	@Named("Lista de Notas")
	public List<Nota> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Nota> listaNotas = this.container
				.allMatches(new QueryDefault<Nota>(Nota.class, criterio));
		if (listaNotas.isEmpty()) {
			this.container.warnUser("No hay Notas cargados en el sistema");
		}
		return listaNotas;

	}

	public List<Nota> filtrarPorDescripcion(
			final @Named("Descripcion") @MaxLength(255) @MultiLine(numberOfLines = 2) String descripcion) {
		
		List<Nota> lista = this.listar();
		Nota unaNota = new Nota();
		List<Nota> listaRetorno = new ArrayList<Nota>();
		for(int i=0;i<lista.size();i++)
		{
			unaNota = new Nota();
			unaNota = lista.get(i);
			if(unaNota.getDescripcion().contains(descripcion.toUpperCase()))
				listaRetorno.add(unaNota);
		}
		if (listaRetorno.isEmpty())
			this.container.warnUser("No se encotraron Registros.");
		return listaRetorno;
	}

	private String currentUserName() {
		return container.getUser().getName();
	}

	/**
	 * PARA MIGRAR
	 */
	@Programmatic
	public Nota insertar(final int nro, final Sector sector,
			final String destino, final String descripcion, final int ultimo,
			final String fecha, final int habilitado, final LocalDate fechacompleta) {

		final Nota unaNota = this.container.newTransientInstance(Nota.class);
		unaNota.setNro_nota(nro);
		unaNota.setSector(sector);
		unaNota.setDestino(destino);
		unaNota.setDescripcion(descripcion.toUpperCase().trim());
		unaNota.setCreadoPor("root");
		unaNota.setAdjuntar(null);
		if (ultimo == 0)
			unaNota.setUltimo(false);
		else
			unaNota.setUltimo(true);

		unaNota.setUltimoDelAnio(false);
		// String[] vector = fechacompleta.split("/");
		// int dia = Integer.parseInt(vector[0]);
		// int mes = Integer.parseInt(vector[1]);
		// int anio = Integer.parseInt(vector[2]);
		// LocalDate date =new LocalDate(anio, mes, dia);

		// final DateTimeFormatter forPattern =
		// DateTimeFormat.forPattern("yyyy-MMM-dd").withLocale(Locale.ENGLISH);
		// LocalDate local =forPattern.parseLocalDate(anio+"-"+mes+"-"+dia);

		// this.container.warnUser(fechacompleta);
		unaNota.setFecha(fechacompleta);

		if (habilitado == 0)
			unaNota.setHabilitado(true);
		else
			unaNota.setHabilitado(false);
		unaNota.setTipo(1);
		unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
		container.persistIfNotAlready(unaNota);
		container.flush();

		return unaNota;
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;

}
