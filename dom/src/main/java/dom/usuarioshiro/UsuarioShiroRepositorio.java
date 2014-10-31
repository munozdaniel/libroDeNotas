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
package dom.usuarioshiro;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import dom.permiso.Permiso;
import dom.rol.Rol;

@DomainService(menuOrder = "80", repositoryFor = UsuarioShiro.class)
@Named("Configuracion")
public class UsuarioShiroRepositorio {

	public String getId() {
		return "usuarioshiro";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Programmatic
	@PostConstruct
	public void init() {
		List<UsuarioShiro> usuarios = listAll();
		if (usuarios.isEmpty()) {
			Permiso permiso = new Permiso();
			Rol rol = new Rol();
			SortedSet<Permiso> permisos = new TreeSet<Permiso>();

			permiso.setNombre("ADMIN");
			permiso.setPath("*");
			permisos.add(permiso);
			rol.setNombre("ADMINISTRADOR");
			rol.setListaPermisos(permisos);

			addUsuarioShiro("sven", "pass", rol);
		}
	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "1")
	@Named("Ver todos")
	public List<UsuarioShiro> listAll() {
		return container.allInstances(UsuarioShiro.class);
	}

	@MemberOrder(sequence = "2")
	@Named("Crear Usuario")
	@Hidden(where = Where.OBJECT_FORMS)
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

	@Programmatic
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

	@ActionSemantics(Of.NON_IDEMPOTENT)
	@MemberOrder(sequence = "4")
	@Named("Eliminar Usuario")
	public String removeUsuarioShiro(@Named("Usuario") UsuarioShiro usuarioShiro) {
		String userName = usuarioShiro.getNick();
		container.remove(usuarioShiro);
		return "El usuario de sistema " + userName
				+ " se ha eliminado correctamente.";
	}

	@javax.inject.Inject
	DomainObjectContainer container;

}
