package dom.impresora;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

import dom.usuario.UsuarioRepositorio;

@Named("IMPRESORA")
public class ImpresoraRepositorio {

	// //////////////////////////////////////
	// Icono
	// //////////////////////////////////////

	public String title() {
		return "IMPRESORA";
	}

	public String iconName() {
		return "IMPRESORA";
	}

	// //////////////////////////////////////
	// Agregar Impresora
	// //////////////////////////////////////

	@MemberOrder(sequence = "10")
	@Named("Agregar")
	public Impresora addImpresora(
			final @Named("Modelo") String modeloImpresora,
			final @Named("Fabricante") String fabricanteImpresora,
			final @Named("Tipo") String tipoImpresora) {
		return nuevaImpresora(modeloImpresora, fabricanteImpresora,
				tipoImpresora, this.currentUserName());
	}

	@Programmatic
	public Impresora nuevaImpresora(final String modeloImpresora,
			final String fabricanteImpresora, final String tipoImpresora,
			final String creadoPor) {
		final Impresora unaImpresora = container
				.newTransientInstance(Impresora.class);
		unaImpresora.setModeloImpresora(modeloImpresora.toUpperCase().trim());
		unaImpresora.setFabricanteImpresora(fabricanteImpresora.toUpperCase()
				.trim());
		unaImpresora.setTipoImpresora(tipoImpresora.toUpperCase().trim());
		unaImpresora.setHabilitado(true);
		unaImpresora.setCreadoPor(creadoPor);
		container.persistIfNotAlready(unaImpresora);
		container.flush();
		return unaImpresora;
	}

	// //////////////////////////////////////
	// Listar Impresora
	// //////////////////////////////////////

	@MemberOrder(sequence = "20")
	public List<Impresora> listar() {
		final List<Impresora> listaImpresora = this.container
				.allMatches(new QueryDefault<Impresora>(Impresora.class,
						"eliminarImpresoraTrue", "creadoPor", this
								.currentUserName()));
		if (listaImpresora.isEmpty()) {
			this.container
					.warnUser("No hay Impresoras cargadas en el sistema.");
		}
		return listaImpresora;
	}

	// //////////////////////////////////////
	// Buscar Impresora
	// //////////////////////////////////////

	@DescribedAs("Buscar Impresora Mayuscula")
	public List<Impresora> autoComplete0AddImpresora(
			final @MinLength(2) String search) {
		return impresoraRepositorio.autoComplete(search);

	}

	@MemberOrder(sequence = "30")
	public List<Impresora> buscar(
			final @Named("Modelo") @MinLength(2) String modeloImpresora) {
		final List<Impresora> listaImpresora = this.container
				.allMatches(new QueryDefault<Impresora>(Impresora.class,
						"buscarPormodeloImpresora", "creadoPor", this
								.currentUserName(), "modeloImpresora",
						modeloImpresora.toUpperCase().trim()));
		if (listaImpresora.isEmpty())
			this.container
					.warnUser("No se encontraron Impresoras cargados en el sistema.");
		return listaImpresora;
	}

	@Programmatic
	public List<Impresora> autoComplete(final String modeloImpresora) {
		return container.allMatches(new QueryDefault<Impresora>(
				Impresora.class, "autoCompletePorModeloImpresora", "creadoPor",
				this.currentUserName(), "modeloImpresora", modeloImpresora
						.toUpperCase().trim()));
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
	private ImpresoraRepositorio impresoraRepositorio;

	@javax.inject.Inject
	private UsuarioRepositorio usuarioRepositorio;
}
