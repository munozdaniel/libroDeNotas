package dom.memo;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Audited;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nro_memo_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "
				+ "WHERE destinoSector.indexOf(:destinoSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimoMemoTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo " + "WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarUltimoMemoFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo " + "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "buscarPorNroMemo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "
				+ "WHERE  "
				+ "nro_memo.indexOf(:nro_memo) >= 0"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "
				+ "WHERE  habilitado == true ORDER BY fecha DESC, nro_memo DESC"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo ORDER BY fecha DESC, nro_memo DESC"),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo " + "WHERE  (ultimo == true)"),

		@javax.jdo.annotations.Query(name = "filtrarCompleto", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "
				+ "WHERE  (:desde <= fecha && fecha<=:hasta)&&(sector==:origen && (destinoSector==:destino || otroDestino ==:otroDestino))  "
				+ "ORDER BY fecha DESC, nro_memo DESC "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_memo DESC ") })
@ObjectType("MEMO")
@Audited
@AutoComplete(repository = MemoRepositorio.class, action = "autoComplete")
@Bookmarkable
public class Memo extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "MEMO Nº " + this.getNro_memo();
	}

	public String iconName() {
		if (this.getHabilitado())
			return "memo";
		else
			return "delete";
	}

	private int nro_memo;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Disabled
	@Named("Nro")
	public int getNro_memo() {
		return nro_memo;
	}

	public void setNro_memo(int nro_memo) {
		this.nro_memo = nro_memo;
	}

	private Sector destinoSector;

	@Disabled
	// @Hidden(where = Where.ALL_TABLES, when = When.UNTIL_PERSISTED)
	@Named("Sector")
	@MemberOrder(name = "Destino", sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public Sector getDestinoSector() {
		return destinoSector;
	}

	public void setDestinoSector(Sector destino) {
		this.destinoSector = destino;
	}

	public List<Sector> choicesDestinoSector() {
		return sectorRepositorio.listar();
	}

	@CssClass("x-verde")
	@Named("Actualizar")
	public Memo updateDestinoSector(@Named("SECTOR") Sector sector) {
		this.setDestinoSector(sector);
		this.setOtroDestino("");
		return this;
	}

	public List<Sector> choices0UpdateDestinoSector() {
		List<Sector> lista = sectorRepositorio.listar();
		lista.remove(0);// debe ser 0
		return lista;
	}

	public boolean hideUpdateDestinoSector() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	private String otroDestino;

	@Named("Otro")
	@Disabled
	// @Hidden(where = Where.ALL_TABLES, when = When.UNTIL_PERSISTED)
	@javax.jdo.annotations.Column(allowsNull = "true")
	@MemberOrder(name = "Destino", sequence = "20")
	public String getOtroDestino() {
		return otroDestino;
	}

	public void setOtroDestino(String destino) {
		this.otroDestino = destino;
	}

	@CssClass("x-verde")
	@Named("Actualizar")
	public Memo updateOtroDestino(@Named("OTRO") String otro) {
		List<Sector> lista = sectorRepositorio.listar();
		this.setDestinoSector(lista.get(0));
		this.setOtroDestino(otro);
		return this;
	}

	public boolean hideUpdateOtroDestino() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		return true;
	}

	@Named("Eliminar")
	@DescribedAs("Necesario privilegios de Administrador.")
	public List<Memo> eliminar() {
		this.setHabilitado(false);
		return memoRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@Named("Para")
	@Mandatory
	public List<Sector> choicesSector() {
		List<Sector> lista = sectorRepositorio.listar();
		if (!lista.isEmpty())
			lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
		return lista;
	}

	@javax.inject.Inject
	private MemoRepositorio memoRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
}
