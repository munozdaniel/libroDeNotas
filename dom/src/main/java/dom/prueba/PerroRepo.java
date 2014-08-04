package dom.prueba;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

@Named("PERRO")

public class PerroRepo {
	public PerroRepo() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "perro";
	}

	public String iconName() {
		return "expediente";
	}

	public Perro nuevo(@Optional Zoologico zoo)
	{
		Perro unA = this.container.newTransientInstance(Perro.class);
		unA.addToZoologico(zoo);
		this.container.persistIfNotAlready(unA);
		this.container.flush();
		return (Perro) unA;
	}
//	public List<Zoologico> choices0Nuevo() {
//		return zoo.listar(); // TODO: return list of choices for property
//	}
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject

	public Zoologico borrar(Zoologico zoologico) {
		// TODO Auto-generated method stub
		this.container.remove(zoologico.getIAnimal());
		this.container.flush();
		return zoologico;
	}
}
