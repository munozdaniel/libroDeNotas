package dom.resoluciones;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name="Tecnico_nro_resolucion_must_be_unique", 
            members={"nro_resolucion"})
})
@ObjectType("RESOLUCIONES")
@Audited
// @AutoComplete(repository=TecnicoRepositorio.class, action="autoComplete") //
@Bookmarkable
public class Resoluciones extends Documento {

	private int nro_resolucion;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getNro_resolucion() {
		return nro_resolucion;
	}

	public void setNro_resolucion(int nro_resolucion) {
		this.nro_resolucion = nro_resolucion;
	}

}
