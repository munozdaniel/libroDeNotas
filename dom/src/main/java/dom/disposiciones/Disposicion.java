package dom.disposiciones;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@ObjectType("DISPOSICION")
@Audited
 @AutoComplete(repository = DisposicionRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Disposicion extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Disposicion: " + this.getNro_Disposicion();
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////
	
	private int nro_Disposicion;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getNro_Disposicion() {
		return nro_Disposicion;
	}

	public void setNro_Disposicion(int nro_Disposicion) {
		this.nro_Disposicion = nro_Disposicion;
	}

}
