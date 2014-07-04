package dom.memo;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.sector.Sector;
import dom.tecnico.TecnicoRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Tecnico_nro_memo_must_be_unique", members = { "nro_memo" }) })
@ObjectType("MEMOCONSECTOR")
@Audited
 @AutoComplete(repository=MemoRepositorio.class, action="autoComplete") //
@Bookmarkable
public class MemoConSector {

	private Sector destinoSector;

	@MemberOrder(name = "Sectores", sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public Sector getDestino() {
		return destinoSector;
	}

	public void setDestino(Sector destino) {
		this.destinoSector = destino;
	}
}
