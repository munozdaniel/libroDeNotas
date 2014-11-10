/*
 * This is a software made for inventory control
 * 
 * Copyright (C) 2014, ProyectoTypes
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * 
 * 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package dom.usuario;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;

import dom.permiso.Permiso;
import dom.rol.Rol;
import dom.rol.RolRepositorio;

@DomainService(menuOrder = "80", repositoryFor = UsuarioShiro.class)
@Named("Usuarios")
public class UsuarioShiroRepositorio {

	public String getId() {
		return "usuarioshi";
	}

	public String iconName() {
		return "Tecnico";
	}

	private static String hash256(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data.getBytes());
		return bytesToHex(md.digest());
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(
					1));
		return result.toString();
	}

	@Programmatic
	@PostConstruct
	public void init() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		List<UsuarioShiro> usuarios = listAll();
		if (usuarios.isEmpty()) {
			// isisJdoSupport
			// .executeUpdate("delete from UsuarioShiro_listaDeRoles");
			// isisJdoSupport.executeUpdate("delete from Rol_listaPermisos");
			// isisJdoSupport.executeUpdate("delete from Permiso");
			// isisJdoSupport.executeUpdate("delete from Rol");
			// isisJdoSupport.executeUpdate("delete from UsuarioShiro");
			List<Usuario> externos = this.listAllExternos();
			for (Usuario user : externos) {

				byte[] decodedBytes = Base64.decodeBase64(user
						.getUsuario_contrasenia());
				if (user.getUsuario_nick().contentEquals("root")) {

					Permiso permiso = new Permiso();
					Rol rol = new Rol();
					SortedSet<Permiso> permisos = new TreeSet<Permiso>();

					permiso.setNombre("SUPERUSUARIO");
					permiso.setPath("*");
					permisos.add(permiso);
					rol.setNombre("SUPERUSUARIO");
					rol.setListaPermisos(permisos);
					addUsuarioShiro(user.getUsuario_nick(), hash256(new String(
							decodedBytes)), rol);// new
					// String(decodedBytes));user.getUsuario_contrasenia()

				} else {
					addUsuarioShiro(user.getUsuario_nick(), hash256(new String(
							decodedBytes)));
				}
			}
			this.createPermisosBasicos();
			this.createPermisosBasicosEscritura();

		} else {
			this.actualizarUserPass();
		}
	}

	@Inject
	private RolRepositorio rolRepositorio;

	private void createPermisosBasicos() {
		List<Permiso> permisos = new ArrayList<Permiso>();
		// read_dashboard,block_disposiciones,read_expediente,read_memo,read_nota,read_resoluciones,read_service

		Permiso permiso = new Permiso();

		permiso.setNombre("Disposiciones (LECTURA)");
		permiso.setPath("dom.documento:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Expedientes (Lectura)");
		permiso.setPath("dom.expediente:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Memo (Lectura)");
		permiso.setPath("dom.memo:Memo:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Nota (Lectura)");
		permiso.setPath("dom.nota:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Resoluciones (Lectura)");
		permiso.setPath("dom.resoluciones:*:*:r");
		permisos.add(permiso);
		
		permiso = new Permiso();
		permiso.setNombre("Disposiciones (Lectura)");
		permiso.setPath("dom.disposiciones:DisposicionRepositorio:*:*:r");
		permisos.add(permiso);
		
		permiso = new Permiso();
		permiso.setNombre("Servicio ");
		permiso.setPath("services:*:*:*");
		permisos.add(permiso);
		createPermisosRepo(permisos);

		rolRepositorio.addRol("IMPS (LECTURA)", permisos);
	}

	private void createPermisosBasicosEscritura() {
		List<Permiso> permisos = new ArrayList<Permiso>();
		// read_dashboard,block_disposiciones,read_expediente,read_memo,read_nota,read_resoluciones,read_service

		Permiso permiso = new Permiso();

		permiso.setNombre("Disposiciones (Escritura)");
		permiso.setPath("dom.documento:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Expedientes (Escritura)");
		permiso.setPath("dom.expediente:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Memo (Escritura)");
		permiso.setPath("dom.memo:Memo:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Nota (Escritura)");
		permiso.setPath("dom.nota:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Resoluciones (Escritura)");
		permiso.setPath("dom.resoluciones:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Servicio ");
		permiso.setPath("services:*:*:*");
		permisos.add(permiso);
		createPermisosRepo(permisos);
		rolRepositorio.addRol("IMPS (ADMINISTRADOR)", permisos);
	}


	private void createPermisosRepo(List<Permiso> permisos) {
//		List<Permiso> permisos = new ArrayList<Permiso>();
		// read_dashboard,block_disposiciones,read_expediente,read_memo,read_nota,read_resoluciones,read_service

		Permiso permiso = new Permiso();
		permiso.setNombre("Inicio");
		permiso.setPath("dom.inicio:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Documento (Lectura)");
		permiso.setPath("dom.documento:*:*:r");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Documento (Escritura)");
		permiso.setPath("dom.documento:*:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Disposiciones (Acciones)");
		permiso.setPath("dom.disposiciones:DisposicionRepositorio:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Expedientes (Acciones)");
		permiso.setPath("dom.expediente:ExpedienteRepositorio:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Memo (Acciones)");
		permiso.setPath("dom.memo:MemoRepositorio:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Nota (Acciones)");
		permiso.setPath("dom.nota:NotaRepositorio:*:*");
		permisos.add(permiso);

		permiso = new Permiso();
		permiso.setNombre("Resoluciones (Acciones)");
		permiso.setPath("dom.resoluciones:ResolucionesRepositorio:*:*");
		permisos.add(permiso);

		//		rolRepositorio.addRol("IMPS (ACCIONES)", permisos);
	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "1")
	@Named("Ver todos")
	public List<UsuarioShiro> listAll() {
		return container.allInstances(UsuarioShiro.class);
	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "10")
	@Named("Actualizar Contraseñas")
	public List<UsuarioShiro> actualizarUserPass()
			throws NoSuchAlgorithmException {
		List<UsuarioShiro> listashiro = this.listAll();
		List<Usuario> externos = this.listAllExternos();
		for (UsuarioShiro usuario : listashiro) {
			for (Usuario userExt : externos) {
				if (userExt.getUsuario_nick().contentEquals(usuario.getNick())) {
					byte[] decodedBytes = Base64.decodeBase64(userExt
							.getUsuario_contrasenia());
					String pass = hash256(new String(decodedBytes));
					usuario.setPassword(pass);
					this.container.flush();
				}
			}
		}
		return listashiro;

	}

	@MemberOrder(sequence = "10")
	@Named("Agregar Usuario")
	public UsuarioShiro addUsuarioShiro(final @Named("Nick") String nick,
			final @Named("Password") String password,
			final @Named("Rol") Rol rol) {
		final UsuarioShiro obj = container
				.newTransientInstance(UsuarioShiro.class);

		final SortedSet<Rol> rolesList = new TreeSet<Rol>();
		if (rol != null) {
			rolesList.add(rol);
			obj.setRolesList(rolesList);
		}
		obj.setNick(nick);
		obj.setPassword(password);
		container.persistIfNotAlready(obj);
		return obj;
	}

	private UsuarioShiro addUsuarioShiro(final @Named("Nick") String nick,
			final @Named("Password") String password) {
		final UsuarioShiro obj = container
				.newTransientInstance(UsuarioShiro.class);

		obj.setNick(nick);
		obj.setPassword(password);
		container.persistIfNotAlready(obj);
		return obj;
	}

	@Programmatic
	@MemberOrder(sequence = "10")
	@Named("Agregar Usuario 2")
	public UsuarioShiro addUsuarioShiro(final @Named("Nick") String nick,
			final @Named("Password") String password,
			final @Named("Rol") List<Rol> rol) {
		final UsuarioShiro obj = container
				.newTransientInstance(UsuarioShiro.class);

		if (!rol.isEmpty()) {
			SortedSet<Rol> listaDeRoles = new TreeSet<Rol>(rol);
			obj.setRolesList(listaDeRoles);
		}
		obj.setNick(nick);
		obj.setPassword(password);
		container.persistIfNotAlready(obj);
		return obj;
	}

	private static PersistenceManager persistencia;

	private List<Usuario> listAllExternos() {

		System.out.println("------------externos---------");
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

	@javax.inject.Inject
	private DomainObjectContainer container;

}
