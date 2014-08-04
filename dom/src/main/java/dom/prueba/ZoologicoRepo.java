package dom.prueba;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

import dom.expediente.Expediente;

@Named("ZOOLOGICO")
public class ZoologicoRepo {
	public ZoologicoRepo() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "zoologico";
	}

	public String iconName() {
		return "expediente";
	}
	public Zoologico nuevo()
	{
		Zoologico unZoo = this.container.newTransientInstance(Zoologico.class);
		unZoo.setIAnimal(new Perro());
		this.container.persistIfNotAlready(unZoo);
		this.container.flush();
		return unZoo;
	}
	public Zoologico cambiar(Zoologico zoo)
	{
		zoo.setIAnimal(new Delfin());
		return zoo;
	}
	
	public List<Zoologico> listar()
	{
		final List<Zoologico> lista = this.container
				.allMatches(new QueryDefault<Zoologico>(Zoologico.class,
						"listar"));
		if (lista.isEmpty()) {
			this.container
					.warnUser("No hay Expedientes cargados en el sistema");
		}
		return lista;

	}
	@javax.inject.Inject
	private DomainObjectContainer container;
}
