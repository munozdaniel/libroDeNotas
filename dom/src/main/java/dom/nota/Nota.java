package dom.nota;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nro_nota_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE destino.indexOf(:destino) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimaNotaTrue", language = "JDOQL", value = "SELECT MAX(nro_nota) "
				+ "FROM dom.nota.Nota " ),
		@javax.jdo.annotations.Query(name = "buscarUltimaNotaFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota " + "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "buscarPorNroNota", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  "
				+ "nro_nota.indexOf(:nro_nota) >= 0"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota " + "WHERE  habilitado == true"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota ") })
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
		return Nota.class.getName();
	}

	public String iconName() {
		return "Sector";
	}

	private int nro_nota;

	@Disabled
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "0")
	@Named("Nº")
	public int getNro_nota() {
		return nro_nota;
	}

	public void setNro_nota(int nro_nota) {
		this.nro_nota = nro_nota;
	}

	private String destino;

	@Named("Destino")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "40")
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}
	// //////////////////////////////////////
	// Implementando los metodos de comparable
	// //////////////////////////////////////

//	@Override
//	public int compareTo(Documento nota) {
//		return ObjectContracts.compare(this, nota, "nro_nota");
//	}
//	

	
}
