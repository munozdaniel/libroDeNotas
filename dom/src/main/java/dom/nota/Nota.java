package dom.nota;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Nota_nro_nota_must_be_unique", members = { "id_documento" }) })
@ObjectType("NOTA")
@Audited
@AutoComplete(repository = NotaRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Nota extends Documento {

	private int nro_nota;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "0")
	public int getNro_nota() {
		return nro_nota;
	}

	public void setNro_nota(int nro_nota) {
		this.nro_nota = nro_nota;
	}

	private String destino;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "10")
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

}
