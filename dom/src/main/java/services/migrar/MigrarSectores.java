package services.migrar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

public class MigrarSectores {
	@Programmatic
	public List<Sector> migrar() {
		Connection con = Conexion.GetConnection();
		List<Sector> lista = new ArrayList<Sector>();
		Sector unSector = null;
		boolean disposicion = false;
		boolean resolucion = false;
		boolean expediente = false;
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
				unSector = new Sector();
				unSector = sectores.agregar(rs.getString("nombre_sector"),
						rs.getString("responsable"), disposicion, expediente,
						resolucion);
				lista.add(unSector);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	@Inject
	private SectorRepositorio sectores;
}
