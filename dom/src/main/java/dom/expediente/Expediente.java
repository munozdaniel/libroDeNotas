package dom.expediente;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "Tecnico_apellido_must_be_unique", members = {
		"nro_expediente"}) })
@ObjectType("EXPEDIENTE")
@Audited
// @AutoComplete(repository=TecnicoRepositorio.class, action="autoComplete") //
@Bookmarkable
public class Expediente extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Expediente: " + this.getNro_expediente();
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	private int nro_expediente;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getNro_expediente() {
		return nro_expediente;
	}

	public void setNro_expediente(int nro_expediente) {
		this.nro_expediente = nro_expediente;
	}

	private String expte_cod_empresa;

	@MemberOrder(sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getExpte_cod_empresa() {
		return expte_cod_empresa;
	}

	public void setExpte_cod_empresa(String expte_cod_empresa) {
		this.expte_cod_empresa = expte_cod_empresa;
	}

	private int expte_cod_numero;

	@MemberOrder(sequence = "30")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getExpte_cod_numero() {
		return expte_cod_numero;
	}

	public void setExpte_cod_numero(int expte_cod_numero) {
		this.expte_cod_numero = expte_cod_numero;
	}

	private String expte_cod_letra;

	@MemberOrder(sequence = "40")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getExpte_cod_letra() {
		return expte_cod_letra;
	}

	public void setExpte_cod_letra(String expte_cod_letra) {
		this.expte_cod_letra = expte_cod_letra;
	}

	private int expte_cod_anio;

	@MemberOrder(sequence = "50")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public int getExpte_cod_anio() {
		return expte_cod_anio;
	}

	public void setExpte_cod_anio(int expte_cod_anio) {
		this.expte_cod_anio = expte_cod_anio;
	}

}
