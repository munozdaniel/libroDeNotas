package dom.dashboard;

import java.util.List;

import org.apache.isis.applib.AbstractViewModel;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import dom.expediente.Expediente;
import dom.expediente.ExpedienteRepositorio;
import dom.memo.Memo;
import dom.memo.MemoRepositorio;
import dom.nota.Nota;
import dom.nota.NotaRepositorio;
import dom.resoluciones.Resoluciones;
import dom.resoluciones.ResolucionesRepositorio;

public class NotaDashboard extends AbstractViewModel {
	public String title() {
		return "Inicio";
	}

	public String iconName() {
		return "Dashboard";
	}

	// //////////////////////////////////////
	// ViewModel contract
	// //////////////////////////////////////

	private String memento;

	@Override
	public String viewModelMemento() {
		return memento;
	}

	@Override
	public void viewModelInit(String memento) {
		this.memento = memento;
	}

	// //////////////////////////////////////
	// listar Notas
	// //////////////////////////////////////

	@Named("Notas")
	@Render(Type.EAGERLY)
	@Disabled
	@MemberOrder(sequence = "10")
	public List<Nota> getAllNotas() {
		return notaRepositorio.listar();
	}

	// //////////////////////////////////////
	// listar Memos
	// //////////////////////////////////////

	@Named("Memo")
	@Render(Type.EAGERLY)
	@Disabled
	@MemberOrder(sequence = "20")
	public List<Memo> getAllMemo() {
		return memoRepositorio.listar();
	}

	// //////////////////////////////////////
	// listar Resoluciones
	// //////////////////////////////////////

	@Named("Resoluciones")
	@Render(Type.EAGERLY)
	@Disabled
	@MemberOrder(sequence = "30")
	public List<Resoluciones> getAllResoluciones() {
		return resolucionesRepositorio.listar();
	}

	// //////////////////////////////////////
	// listar Expedientes
	// //////////////////////////////////////

	@Named("Expedientes")
	@Render(Type.EAGERLY)
	@Disabled
	@MemberOrder(sequence = "40")
	public List<Expediente> getAllExpedientes() {
		return expedienteRepositorio.listar();
	}

	// //////////////////////////////////////
	// injected services
	// //////////////////////////////////////

	@javax.inject.Inject
	private NotaRepositorio notaRepositorio;
	@javax.inject.Inject
	private MemoRepositorio memoRepositorio;
	@javax.inject.Inject
	private ResolucionesRepositorio resolucionesRepositorio;
	@javax.inject.Inject
	private ExpedienteRepositorio expedienteRepositorio;

}
