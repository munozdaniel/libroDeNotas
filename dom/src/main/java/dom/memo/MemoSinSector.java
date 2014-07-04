package dom.memo;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Tecnico_nro_memo_must_be_unique", members = { "nro_memo" }) })
@ObjectType("MEMOSINSECTOR")
@Audited
 @AutoComplete(repository=MemoRepositorio.class, action="autoComplete") //
@Bookmarkable
public class MemoSinSector {
	private String otroDestino;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name = "Sectores", sequence = "20")
	@Disabled
	public String getOtroDestino() {
		return otroDestino;
	}

	public void setOtroDestino(String destino) {
		this.otroDestino = destino;
	}
}
