package services.migrar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.joda.time.LocalDate;

import dom.disposiciones.Disposicion;
import dom.disposiciones.DisposicionRepositorio;
import dom.expediente.Expediente;
import dom.expediente.ExpedienteRepositorio;
import dom.memo.Memo;
import dom.memo.MemoRepositorio;
import dom.nota.Nota;
import dom.nota.NotaRepositorio;
import dom.resoluciones.Resoluciones;
import dom.resoluciones.ResolucionesRepositorio;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

/**
 * Permite generar una segunda conexion con el fin de obtener los datos de la
 * base de datos original (libro_notas) y volcarlos sobre la nueva base de datos
 * (libro). Se realiza de este modo por falta de tiempo, en el futuro la
 * conexion será a traves de JDO DataNucleus (Investigar). XML!!!
 * 
 * @author munoz
 * 
 */
public class MigrarServicio {
	
	public List<Sector> migrarSectores() {
		Connection con = Conexion.GetConnection();
		List<Sector> lista = new ArrayList<Sector>();
		Sector unSector = null;
		// if (con != null)
		// return "Se ha realizado una segunda conexion";
		// else
		// return "Hasta las bolas";
		boolean disposicion = false;
		boolean resolucion = false;
		boolean expediente = false;
		String responsable = "Sin Definir";
		try {
			PreparedStatement stmt = con
					.prepareStatement("Select * from sector");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// System.out.println("Salida:");//Ver en terminal.
				if (rs.getInt("disposicion") == 1)
					disposicion = true;
				if (rs.getInt("resolucion") == 1)
					resolucion = true;
				if (rs.getInt("expediente") == 1)
					expediente = true;
				if (rs.getString("responsable") == ""
						|| rs.getString("responsable") == null)
					responsable = "Sin Definir";
				else
					responsable = rs.getString("responsable");
				unSector = new Sector();
				unSector = sectores.agregar(rs.getString("nombre_sector"),
						responsable, disposicion, expediente, resolucion);
				lista.add(unSector);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	@javax.inject.Inject
	private DomainObjectContainer container;

	public List<Nota> migrarNotas() {
		Connection con = Conexion.GetConnection();
		List<Nota> lista = new ArrayList<Nota>();
		Nota nota = null;
		Sector sector = new Sector();
		try {
			PreparedStatement stmt = con
					.prepareStatement("Select * from documento as doc JOIN nota AS n ON doc.id_documento=n.id_documento AND fechacompleta like '%2014'");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idsector = rs.getInt("id_sector");
				if (idsector == -1)
					idsector = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next()) {
					sector = sectores.buscarPorNombre(rsector
							.getString("nombre_sector"));
				}
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());

				nota = notas.insertar(rs.getInt("nro_documento"), sector,
						rs.getString("destino"), rs.getString("descripcion"),
						rs.getInt("ultimo"), rs.getString("fecha"),
						rs.getInt("eliminado"), localdate);
				lista.add(nota);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

//	public List<Nota> buscarDateCompleta() {
//		Connection con = Conexion.GetConnection();
//		List<Nota> lista = new ArrayList<Nota>();
//		Sector sector = new Sector();
//		try {
//			PreparedStatement stmt = con
//					.prepareStatement("Select * from documento as doc JOIN nota AS n ON doc.id_documento=n.id_documento AND fechacompleta like '%2014'");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				Date date = rs.getDate("datecompleta");
//				final Nota unaNota = this.container
//						.newTransientInstance(Nota.class);
//				unaNota.setNro_nota(1);
//				sector = this.sectorRepositorio.buscarPorNombre("FARMACIA");
//				unaNota.setSector(sector);
//				unaNota.setDestino("AFUERA");
//				unaNota.setDescripcion("NO PROBLEMO".toUpperCase().trim());
//				unaNota.setCreadoPor("root");
//				unaNota.setAdjuntar(null);
//				unaNota.setUltimo(true);
//				unaNota.setUltimoDelAnio(false);
//				unaNota.setFecha(new LocalDate(date.getTime()));
//				unaNota.setHabilitado(true);
//				unaNota.setTipo(1);
//				LocalDateTime local = LocalDateTime.parse(date.toString());
//				unaNota.setTime(local);
////				LocalDate localdate = new LocalDate(date.getTime());
////				unaNota.setDatecompleta(localdate);
//				container.persistIfNotAlready(unaNota);
//				container.flush();
//				lista.add(unaNota);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lista;
//
//	}
//
//	public List<String> describir() {
//		Connection con = Conexion.GetConnection();
//		List<String> lista = new ArrayList<String>();
//		try {
//			PreparedStatement stmt = con
//					.prepareStatement("Select * from documento as doc JOIN nota AS n ON doc.id_documento=n.id_documento AND fechacompleta like '%2014'");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				Date date = rs.getDate("datecompleta");
//
//				lista.add(date.toString());
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lista;
//	}
//
//	public List<LocalDateTime> data() {
//		Connection con = Conexion.GetConnection();
//		List<LocalDateTime> lista = new ArrayList<LocalDateTime>();
//		try {
//			PreparedStatement stmt = con
//					.prepareStatement("Select * from documento as doc JOIN nota AS n ON doc.id_documento=n.id_documento AND fechacompleta like '%2014'");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				Date date = rs.getDate("datecompleta");
//				LocalDateTime local = LocalDateTime.parse(date.toString());
//				lista.add(local);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lista;
//	}

	@Inject
	private SectorRepositorio sectorRepositorio;

	public List<Memo> migrarMemos() {
		Connection con = Conexion.GetConnection();
		List<Memo> lista = new ArrayList<Memo>();
		Memo memo = null;
		Sector sector = new Sector();
		Sector sectord = new Sector();
		try {
			// memo sector
			PreparedStatement stmt = con
					.prepareStatement("SELECT * FROM documento d JOIN memos AS m ON d.id_documento = m.id_documento JOIN  memo_sector AS s ON s.id_documento = m.id_documento WHERE fechacompleta like '%2014';");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idsector = rs.getInt("id_sector");
				if (idsector == -1)
					idsector = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next())
					sector = sectores.buscarPorNombre(rsector
							.getString("nombre_sector"));

				int idsector2 = rs.getInt("id_sectord");// Cambiar nombre del
														// campo
				// en la base de datos.
				if (idsector2 == -1)
					idsector2 = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector2 + "");
				rsector = stmt.executeQuery();
				while (rsector.next()) {
					sectord = sectores.buscarPorNombre(rsector.getString(
							"nombre_sector").toUpperCase());
				}
				if (sectord == null)
					this.container.warnUser("SECTORD ;; "
							+ rs.getInt("id_sectord"));
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());
				memo = memos.insertar(rs.getInt("nro_documento"), 2, sector,
						rs.getString("descripcion"), rs.getInt("eliminado"),
						rs.getInt("ultimo"), sectord, "", localdate);

				lista.add(memo);

			}
			// Memo no Sector (Con destino)
			stmt = con
					.prepareStatement("SELECT * FROM documento d JOIN memos AS m ON d.id_documento = m.id_documento JOIN  memo_no_sector AS nos ON nos.id_documento = m.id_documento WHERE fechacompleta like '%2014';");
			rs = stmt.executeQuery();
			while (rs.next()) {

				int idsector = rs.getInt("id_sector");

				if (idsector == -1)
					idsector = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next()) {
					sector = sectores.buscarPorNombre(rsector.getString(
							"nombre_sector").toUpperCase());
				}
				if (sector == null) {
					this.container.warnUser("SECTORD ;; "
							+ rs.getInt("id_sectord"));
				}
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());
				memo = memos.insertar(rs.getInt("nro_documento"), 2, sector,
						rs.getString("descripcion"), rs.getInt("eliminado"),
						rs.getInt("ultimo"),this.sectorRepositorio.buscarPorNombre("OTRO SECTOR"), rs.getString("destino"),localdate);
				lista.add(memo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	

	public List<Resoluciones> migrarResoluciones() {
		Connection con = Conexion.GetConnection();
		List<Resoluciones> lista = new ArrayList<Resoluciones>();
		Resoluciones resoluciones = null;
		Sector sector = new Sector();
		try {
			PreparedStatement stmt = con
					.prepareStatement("Select * from documento as doc JOIN resolucion AS r ON doc.id_documento=r.id_documento AND fechacompleta like '%2014';");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idsector = rs.getInt("id_sector");
				if (idsector == -1 || idsector == 0)
					idsector = 1;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next())
					sector = sectores.buscarPorNombre(rsector
							.getString("nombre_sector"));
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());
				resoluciones = resolucionesRepo.insertar(
						rs.getInt("nro_documento"), 3,
						sector, rs.getString("descripcion"),
						rs.getInt("eliminado"), rs.getInt("ultimo"),localdate);
				lista.add(resoluciones);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	public List<Disposicion> migrarDisposiciones() {
		Connection con = Conexion.GetConnection();
		List<Disposicion> lista = new ArrayList<Disposicion>();
		Disposicion disposicion = null;
		Sector sector = new Sector();
		try {
			PreparedStatement stmt = con
					.prepareStatement("Select * from documento as doc JOIN disposicion AS d ON doc.id_documento=d.id_documento AND fechacompleta like '%2014';");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idsector = rs.getInt("id_sector");
				if (idsector == -1)
					idsector = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next())
					sector = sectores.buscarPorNombre(rsector
							.getString("nombre_sector"));
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());
				disposicion = disposicionesRepo.insertar(
						rs.getInt("nro_documento"), 3,
						sector, rs.getString("descripcion"),
						rs.getInt("eliminado"), rs.getInt("ultimo"),localdate);
				lista.add(disposicion);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	public List<Expediente> migrarExpedientes() {
		Connection con = Conexion.GetConnection();
		List<Expediente> lista = new ArrayList<Expediente>();
		Expediente expediente = null;
		Sector sector = new Sector();
		try {
			PreparedStatement stmt = con
					.prepareStatement("Select * from documento as doc JOIN expediente AS e ON doc.id_documento=e.id_documento WHERE `expte_cod_anio`=2014 AND fechacompleta like '%2014';");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int idsector = rs.getInt("id_sector");
				if (idsector == -1)
					idsector = 0;// Sin Definir
				stmt = con
						.prepareStatement("Select nombre_sector from sector where id_sector =?");
				stmt.setString(1, idsector + "");
				ResultSet rsector = stmt.executeQuery();
				while (rsector.next())
					sector = sectores.buscarPorNombre(rsector
							.getString("nombre_sector"));
				Date date = rs.getDate("datecompleta");
				LocalDate localdate = new LocalDate(date.getTime());
				expediente = expedientesRepo.insertar(
						rs.getInt("nro_documento"), 3,
						sector, rs.getString("descripcion"),
						rs.getInt("eliminado"), rs.getInt("ultimo"),
						rs.getString("expte_cod_empresa"),
						rs.getInt("expte_cod_numero"),
						rs.getInt("expte_cod_anio"),
						rs.getString("expte_cod_letra"),localdate);

				lista.add(expediente);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	@Inject
	private SectorRepositorio sectores;
	@Inject
	private NotaRepositorio notas;
	@Inject
	private MemoRepositorio memos;
	@Inject
	private ExpedienteRepositorio expedientesRepo;
	@Inject
	private ResolucionesRepositorio resolucionesRepo;
	@Inject
	private DisposicionRepositorio disposicionesRepo;
}
