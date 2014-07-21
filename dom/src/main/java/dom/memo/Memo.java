package dom.memo;

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
import dom.sector.Sector;

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
				+ "nro_nota.indexOf(:nro_nota) >= 0"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo " + "WHERE  habilitado == true"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.memo.Memo "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechaSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (fecha==:fecha && sector==:sector)"),
		@javax.jdo.annotations.Query(name = "filtrarPorFecha", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (fecha==:fecha)"),
		@javax.jdo.annotations.Query(name = "filtrarPorSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  (habilitado == true) && (sector==:sector)") })
@ObjectType("MEMO")
@Audited
@AutoComplete(repository = MemoRepositorio.class, action = "autoComplete")
//
@Bookmarkable
public class Memo extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "MEMO Nº " + this.getNro_memo();
	}

	public String iconName() {
		return "memo";
	}

	private int nro_memo;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Disabled
	public int getNro_memo() {
		return nro_memo;
	}

	public void setNro_memo(int nro_memo) {
		this.nro_memo = nro_memo;
	}

	// private ButtonGroup tipoMemo;
	//
	// @MemberOrder(name = "Sectores", sequence = "20")
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// public ButtonGroup getTipoMemo() {
	// return tipoMemo;
	// }

	private Sector destinoSector;
	@Named("Sector")
	@MemberOrder(name = "Destino", sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "false")
	public Sector getDestinoSector() {
		return destinoSector;
	}

	public void setDestinoSector(Sector destino) {
		this.destinoSector = destino;
	}

	// private String otroDestino;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// @MemberOrder(name = "Sectores", sequence = "20")
	// @Disabled
	// public String getOtroDestino() {
	// return otroDestino;
	// }
	//
	// public void setOtroDestino(String destino) {
	// this.otroDestino = destino;
	// }
	//
	// public void setTipoMemo(ButtonGroup tipoMemo) {
	// this.tipoMemo = tipoMemo;
	// }
	//
	// public void default0TipoMemo() {
	// JRadioButton rbtn1=new JRadioButton("Sector",true);
	// JRadioButton rbtn2=new JRadioButton("Otro",false);
	// this.tipoMemo.add(rbtn1);
	// this.tipoMemo.add(rbtn2);
	// }

	// public static enum Categoria {
	// Sector, Otro;
	// }

	// private Categoria tipoSector;
	//
	// @javax.jdo.annotations.Column(allowsNull = "false")
	// public Categoria getTipoSector() {
	// return tipoSector;
	// }
	//
	// public void setTipoSector(Categoria tipoSector) {
	// this.tipoSector = tipoSector;
	// }

	// public static class CategoriaSector {
	// private static final String NEW = "Sector";
	// private static final String INCOMPLETE = "Otro";
	// private static final String SUBMITTED = "Ninguno";
	//
	// public static final List<String> ALL =
	// Collections.unmodifiableList(Arrays.asList(NEW, INCOMPLETE, SUBMITTED));
	// public static class ChoicesSpecification implements Specification {
	// @Override
	// public String satisfies(Object obj) {
	// for (String str : ALL) {
	// if (str.equals(obj)) {
	// return null;
	// }
	// }
	// return "Must be one of " + ALL;
	// }
	// }
	// }
	// @Override
	// public int compareTo(Documento memo) {
	// return ObjectContracts.compare(this, memo, "nro_memo");
	// }
}
