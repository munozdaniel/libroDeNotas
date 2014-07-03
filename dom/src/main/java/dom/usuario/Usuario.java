package dom.usuario;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.util.ObjectContracts;

import dom.persona.Persona;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Usuario_apellido_must_be_unique", members = {
		"creadoPor", "apellido" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletePorApellido", language = "JDOQL", value = "SELECT "
				+ "FROM dom.usuario.Usuario "
				+ "WHERE creadoPor == :creadoPor && "
				+ "apellido.indexOf(:apellido) >= 0"),
		@javax.jdo.annotations.Query(name = "eliminarUsuarioFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.usuario.Usuario "
				+ "WHERE creadoPor == :creadoPor "
				+ "   && habilitado == false"),
		@javax.jdo.annotations.Query(name = "eliminarUsuarioTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.usuario.Usuario "
				+ "WHERE creadoPor == :creadoPor " + "   && habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarPorApellido", language = "JDOQL", value = "SELECT "
				+ "FROM dom.usuario.Usuario "
				+ "WHERE creadoPor == :creadoPor "
				+ "   && apellido.indexOf(:apellido) >= 0"),
		@javax.jdo.annotations.Query(name = "getUsuario", language = "JDOQL", value = "SELECT FROM dom.usuario.Usuario WHERE creadoPor == :creadoPor") })
@ObjectType("USUARIO")
@Audited
@AutoComplete(repository = UsuarioRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Usuario extends Persona implements Comparable<Persona> {

	// //////////////////////////////////////
	// Identificacion en la UI
	// //////////////////////////////////////

	public String title() {
		return this.getApellido() + " " + this.getNombre();
	}

	public String iconName() {
		return "Usuario";
	}

	// //////////////////////////////////////
	// Borrar Usuario
	// //////////////////////////////////////
	/**
	 * Método que utilizo para deshabilitar un Usuario.
	 * 
	 * @return la propiedad habilitado en false.
	 */
	@Named("Eliminar Usuario")
	@PublishedAction
	@Bulk
	@MemberOrder(name = "accionEliminar", sequence = "10")
	public List<Usuario> eliminar() {
		if (getEstaHabilitado() == true) {
			setHabilitado(false);
			container.isPersistent(this);
			container.warnUser("Eliminado " + container.titleOf(this));
		}
		return null;
	}

	// //////////////////////////////////////
	// CompareTo
	// //////////////////////////////////////
	/**
	 * Implementa Comparable<Usuario> Necesario para ordenar por apellido la
	 * clase Usuario.
	 */
	@Override
	public int compareTo(final Persona persona) {
		return ObjectContracts.compare(this, persona, "apellido");
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;

}