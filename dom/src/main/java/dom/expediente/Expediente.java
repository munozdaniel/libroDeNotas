package dom.expediente;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Size;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Where;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nroExpediente_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE destinoSector.indexOf(:destinoSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimoExpedienteTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarUltimoExpedienteFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "buscarPorNroExpediente", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  "
				+ "nro_expediente.indexOf(:nro_expediente) >= 0 ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  habilitado == true ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente " + "WHERE  (ultimo == true)"),
		@javax.jdo.annotations.Query(name = "filtrarCompleto", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  :desde <= fecha && fecha<=:hasta && sector==:sector ORDER BY fecha DESC, nro_expediente DESC "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_expediente DESC ") })
@ObjectType("EXPEDIENTE")
@Audited
@AutoComplete(repository = ExpedienteRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Expediente extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Expediente Nº " + String.format("%03d",this.getNro_expediente());
	}

	public String iconName() {
		if (this.getHabilitado())
			return "expediente";
		else
			return "delete";
	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	private int nro_expediente;

	@Named("Nro")
	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Disabled
	@Hidden
	public int getNro_expediente() {
		return nro_expediente;
	}

	public void setNro_expediente(int nro_expediente) {
		this.nro_expediente = nro_expediente;
	}
	
	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;
	@Disabled
	@MemberOrder(sequence = "0")
	@Named("Nro")
	@NotPersisted
	public String getNro() {
		return 	String.format("%03d",this.getNro_expediente());
	}
	
	private String expte_cod_empresa;

	@Named("Empresa")
	@Hidden(where = Where.PARENTED_TABLES)
	@MemberOrder(sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getExpte_cod_empresa() {
		return expte_cod_empresa;
	}

	public void setExpte_cod_empresa(String expte_cod_empresa) {
		this.expte_cod_empresa = expte_cod_empresa;
	}

	private int expte_cod_numero;

	@Hidden
	@MemberOrder(sequence = "30")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Named("Numero")
	public int getExpte_cod_numero() {
		return expte_cod_numero;
	}

	public void setExpte_cod_numero(int expte_cod_numero) {
		this.expte_cod_numero = expte_cod_numero;
	}

	public enum Letras {
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
	}

	private String expte_cod_letra;

	@MemberOrder(sequence = "40")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MaxLength(100)
	@Size(max = 100)
	@Named("Inicial")
	@Hidden(where = Where.PARENTED_TABLES)
	public String getExpte_cod_letra() {
		return expte_cod_letra;
	}

	public void setExpte_cod_letra(String expte_cod_letra) {
		this.expte_cod_letra = expte_cod_letra;
	}

	private int expte_cod_anio;

	@Hidden(where = Where.PARENTED_TABLES)
	@MemberOrder(sequence = "50")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Named("Año")
	public int getExpte_cod_anio() {
		return expte_cod_anio;
	}

	public void setExpte_cod_anio(int expte_cod_anio) {
		this.expte_cod_anio = expte_cod_anio;
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarExpediente();
	}

	@Named("Eliminar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public List<Expediente> eliminar() {
		this.setHabilitado(false);
		return expedienteRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private ExpedienteRepositorio expedienteRepositorio;

}
