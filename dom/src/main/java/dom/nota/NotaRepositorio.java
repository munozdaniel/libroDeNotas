package dom.nota;

import java.util.List;

import javax.jdo.identity.ObjectIdentity;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;

import dom.sector.Sector;

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

	// //////////////////////////////////////
	// Insertar un Sector.
	// //////////////////////////////////////
	@Named("Agregar")
	@MemberOrder(sequence = "10")
	public String addNota() {
//		return nuevaNota(sector, destino, descripcion);
		return "";
	}

	/**
	 * Buscar
	 * 
	 * @param nombreSector
	 * @return
	 */


	// @Programmatic
	// public Nota nuevaNota(final Sector sector, final String destino,
	// final String descripcion) {
	// final Nota unaNota = this.container
	// .newTransientInstance(Nota.class);
	//
	// unSector.setNombreSector(nombreSector.toUpperCase().trim());
	// unSector.setHabilitado(true);
	// unSector.setCreadoPor(creadoPor);
	// this.container.persistIfNotAlready(unSector);
	// this.container.flush();
	// return unSector;
	//
	// }

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
}
