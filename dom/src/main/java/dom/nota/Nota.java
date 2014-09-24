package dom.nota;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.joda.time.LocalDate;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Uniques({ @javax.jdo.annotations.Unique(name = "nro_nota_must_be_unique", members = { "id_documento" }) })
@Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE destino.indexOf(:destino) >= 0 && (habilitado==true)"),
		@javax.jdo.annotations.Query(name = "autoComplete", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE destino.indexOf(:destino) >= 0"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota " + "WHERE  habilitado == true ORDER BY nro_nota DESC "),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota ORDER BY nro_nota DESC  "),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = " SELECT  "
				+ "FROM dom.nota.Nota " + "WHERE  (ultimo == true)  "),
		@javax.jdo.annotations.Query(name = "esNuevoAnio", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota " + "WHERE fecha == :fecha ORDER BY nro_nota DESC ") 
	 })
@ObjectType("NOTA")
@Audited
@AutoComplete(repository = NotaRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Nota extends Documento {
	// //////////////////////////////////////
	// Identificacion en la UI.
	// Aparece como item del menu
	// //////////////////////////////////////

	public String title() {
		return "NOTA Nº " + this.getNro_nota();
	}

	public String iconName() {
		if (this.getHabilitado())
			return "nota";
		else
			return "delete";
	}

	private int nro_nota;

	@Disabled
	@javax.jdo.annotations.Column(allowsNull = "true")
	// @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	@MemberOrder(sequence = "0")
	@Named("Nro")
	public int getNro_nota() {
		return nro_nota;
	}

	public void setNro_nota(final int nro_nota) {
		this.nro_nota = nro_nota;
	}

	private String destino;

	@Named("Destino")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "3")
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	@Named("Eliminar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public List<Nota> eliminar() {
		this.setHabilitado(false);
		return notaRepositorio.listar();
	}

	public boolean hideEliminar() {
		// TODO: return true if action is hidden, false if
		// visible
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	// @Named("Restaurar")
	// @DescribedAs("Necesario privilegios de Administrador.")
	// public Nota restaurar() {
	// this.setHabilitado(true);
	// return this;
	// }
	//
	// public boolean hideRestaurar() {
	// // TODO: return true if action is hidden, false if
	// // visible
	// if (this.container.getUser().isCurrentUser("root"))
	// return false;
	// else
	// return true;
	// }
	

	@javax.inject.Inject
	private NotaRepositorio notaRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;

	// //////////////////////////////////////
	// Implementando los metodos de comparable
	// //////////////////////////////////////

	// @Override
	// public int compareTo(Documento nota) {
	// return ObjectContracts.compare(this, nota, "nro_nota");
	// }
	

}
