package dom.prueba;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

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
		
		IAnimal ianimal = this.container.newTransientInstance(Perro.class);
		this.container.persistIfNotAlready(ianimal);
		this.container.flush();

		Zoologico unZoo = this.container.newTransientInstance(Zoologico.class);
		unZoo.modificarIAnimal(ianimal);

		this.container.persistIfNotAlready(unZoo);

		this.container.flush();
		return unZoo;
	}
	
	public Zoologico cambiar(@Optional Zoologico zoo)
	{
		IAnimal delfin = new Delfin();
		zoo.modificarIAnimal(new Perro());
		return zoo;
	}
	@Programmatic
	public Zoologico borrar(@Optional Zoologico zoo)
	{
		this.container.remove(zoo.getIAnimal());
		zoo.setIAnimal(null);
		this.container.flush();
		return zoo;
	}
	public List<Zoologico> choice0Cambiar()
	{
		return this.listar();
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
