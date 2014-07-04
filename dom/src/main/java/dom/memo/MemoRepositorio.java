package dom.memo;

import java.util.Formatter;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("MEMO")
public class MemoRepositorio {

	public MemoRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "memo";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Named("Enviar")
	@MemberOrder(sequence = "10")
	public Memo addMemo(
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("De:") Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Sector:") Sector destinoSector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Descripción:") String descripcion) {
		return this.nuevoMemo(sector, destinoSector, descripcion,
				this.currentUserName());

	}

	// public ButtonGroup default0AddMemo() {
	// ButtonGroup tipoMemo = new ButtonGroup();
	// JRadioButton rbtn1 = new JRadioButton("Sector", true);
	// JRadioButton rbtn2 = new JRadioButton("Otro", false);
	// tipoMemo.add(rbtn1);
	// tipoMemo.add(rbtn2);
	// return tipoMemo;
	// }

	@Programmatic
	private Memo nuevoMemo(final Sector sector, final Sector destinoSector,
			final String descripcion, final String creadoPor) {
		final Memo unMemo = this.container.newTransientInstance(Memo.class);
		int nro = recuperarNroMemo();
		nro += 1;
		formato = new Formatter();
		formato.format("%04d", nro);
		unMemo.setNro_memo(Integer.parseInt(000+formato.toString()));
		unMemo.setFecha(LocalDate.now());
		unMemo.setTipo(2);
		unMemo.setDescripcion(descripcion.toUpperCase().trim());
		unMemo.setHabilitado(true);
		unMemo.setCreadoPor(creadoPor);
		unMemo.setSector(sector);
		unMemo.setDestinoSector(destinoSector);
		container.persistIfNotAlready(unMemo);
		container.flush();
		return unMemo;
	}

	@Programmatic
	private int recuperarNroMemo() {
		final Memo memo = this.container.firstMatch(new QueryDefault<Memo>(
				Memo.class, "buscarUltimoMemoTrue"));
		if (memo == null)
			return 0;
		else
			return memo.getNro_memo();
	}

	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////

	// @Named("Sector")
	// @DescribedAs("Buscar el Sector en mayuscula")
	// public List<Sector> autoComplete0AddMemo(final @MinLength(2) String
	// search) {
	// return sectorRepositorio.autoComplete(search);
	// }
	@Named("Sector")
	public List<Sector> choices0AddMemo() {
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
	}
	@Named("Para")
	public List<Sector> choices1AddMemo() {
		return sectorRepositorio.listar(); // TODO: return list of choices for
											// property
	}
	
	 @Programmatic
	 public List<Memo> autoComplete(final String destino) {
	 return container.allMatches(new QueryDefault<Memo>(Memo.class,
	 "autoCompletarDestino", "destinoSector", destino));
	 }

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////

	 @MemberOrder(sequence = "20")
	 public List<Memo> listar() {
	 final List<Memo> listaMemo = this.container
	 .allMatches(new QueryDefault<Memo>(Memo.class,
	 "listarHabilitados"));
	 if (listaMemo.isEmpty()) {
	 this.container.warnUser("No hay tecnicos cargados en el sistema");
	 }
	 return listaMemo;
	
	 }

	// //////////////////////////////////////
	// CurrentUserName
	// //////////////////////////////////////

	private String currentUserName() {
		return container.getUser().getName();
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	private Formatter formato;

}
