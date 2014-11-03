package dom.usuarioshiro;

import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Date;

import dom.permiso.Permiso;
import dom.rol.Rol;
import dom.sector.SectorRepositorio;

@DomainService(menuOrder = "80", repositoryFor = Usuario.class)
@Named("Configuracion de Usuario")
public class UsuarioRepositorio {
	public String getId() {
		return "usuarios";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Programmatic
	@PostConstruct
	public void init() {
		List<Usuario> usuarios = listarUsuariosIsis();
		if (usuarios.isEmpty()) {
			List<Usuario> externos = this.listAllExternos();
			if (externos.isEmpty()) {
				// Crear usuario admin admin.
				SortedSet<Permiso> permisos = new TreeSet<Permiso>();
				Permiso permiso = new Permiso();
				permiso.setNombre("ADMIN");
				permiso.setPath("*");
				permisos.add(permiso);

				Rol rol = new Rol();
				rol.setNombre("ADMINISTRADOR");
				rol.setListaPermisos(permisos);

				addUsuario(1,
						"root",
						"Administrador Informatica",
						"infoimps",
						1,
						"noreply@admin.com",
						1,
						new Date(), rol);
			} else {
				// insertar todos.
				for (Usuario user : externos) {
					if (user.getUsuario_nick().compareTo("maltamirano") == 0) {
						SortedSet<Permiso> permisos = new TreeSet<Permiso>();
						Permiso permiso = new Permiso();
						permiso.setNombre("ADMIN");
						permiso.setPath("*");
						permisos.add(permiso);

						Rol rol = new Rol();
						rol.setNombre("ADMINISTRADOR");
						rol.setListaPermisos(permisos);

						addUsuario(user.getUsuario_id(),
								user.getUsuario_nick(),
								user.getUsuario_nombreCompleto(),
								user.getUsuario_contrasenia(),
								user.getUsuario_sector(),
								user.getUsuario_email(),
								user.getUsuario_activo(),
								user.getUsuario_fechaCreacion(), rol);
					}
					addUsuario(user.getUsuario_id(),
							user.getUsuario_nick(),
							user.getUsuario_nombreCompleto(),
							user.getUsuario_contrasenia(),
							user.getUsuario_sector(),
							user.getUsuario_email(),
							user.getUsuario_activo(),
							user.getUsuario_fechaCreacion(), null);
				}
			}

		}
	}

	private Usuario addUsuario(final int id, final String nick,
			final String nombrecompleto, final String contrasenia,
			final int sector, final String email, final int activo,
			final Date fechacreacion, final Rol rol) {
		Usuario usuario = container.newTransientInstance(Usuario.class);
		usuario.setUsuario_id(id);
		usuario.setUsuario_nick(nick);
		usuario.setUsuario_nombreCompleto(nombrecompleto);
		// FIXME: Cifrar contraseña.
		usuario.setUsuario_contrasenia(contrasenia);
		usuario.setUsuario_sector(sector);
		usuario.setUsuario_email(email);
		usuario.setUsuario_activo(activo);
		usuario.setUsuario_fechaCreacion(fechacreacion);

		final SortedSet<Rol> rolesList = new TreeSet<Rol>();
		if (rol != null) {
			rolesList.add(rol);
			usuario.setRolesList(rolesList);
		}

		container.persistIfNotAlready(usuario);
		container.flush();
		return usuario;
	}

	private static PersistenceManager persistencia;

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "1")
	@Named("Ver todos")
	public List<Usuario> listAllExternos() {
		PersistenceManagerFactory pm = this.conexion();
		persistencia = pm.getPersistenceManager();
		Query q = persistencia.newQuery("javax.jdo.query.SQL",
				"SELECT * FROM usuarios");
		q.setResultClass(Usuario.class);
		List<Usuario> results = (List<Usuario>) (q.execute());
		return results;

	}

	private PersistenceManagerFactory conexion() {
		Properties properties = new Properties();
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
				"org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		properties.setProperty("javax.jdo.option.ConnectionURL",
				"jdbc:mysql://192.168.42.14/gestionusuarios");
		properties.setProperty("javax.jdo.option.ConnectionDriverName",
				"com.mysql.jdbc.Driver");
		properties.setProperty("javax.jdo.option.ConnectionUserName", "root");
		properties.setProperty("javax.jdo.option.ConnectionPassword",
				"infoimps");
		PersistenceManagerFactory pmf = JDOHelper
				.getPersistenceManagerFactory(properties);
		return pmf;
	}
	@Programmatic
	public void actualizarUsuarios(final List<Usuario> lista) {
		List<Usuario> listalocal = this.listarUsuariosIsis();
		for (Usuario externo : lista) {
			for (Usuario local : listalocal) {
				if (externo.compareTo(local) == 1) {

					local.setUsuario_nick(externo.getUsuario_nick());
					local.setUsuario_nombreCompleto(externo
							.getUsuario_nombreCompleto());
					local.setUsuario_contrasenia(externo
							.getUsuario_contrasenia());
					local.setUsuario_sector(externo.getUsuario_sector());
					local.setUsuario_email(externo.getUsuario_email());
					local.setUsuario_activo(externo.getUsuario_activo());
					local.setUsuario_fechaCreacion(externo
							.getUsuario_fechaCreacion());
					container.flush();
				}
			}
		}
	}

	private List<Usuario> listarUsuariosIsis() {
		return this.container.allMatches(new QueryDefault<Usuario>(
				Usuario.class, "listar"));
	}

	@Inject
	private DomainObjectContainer container;
	@Inject
	private SectorRepositorio sectorRepositorio;
	//
	// @javax.inject.Inject
	// private IsisJdoSupport isisJdoSupport;

}
