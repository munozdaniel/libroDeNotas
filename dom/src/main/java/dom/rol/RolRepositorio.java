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
package dom.rol;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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

@DomainService(menuOrder = "81", repositoryFor = Rol.class)
@Named("Rol")
public class RolRepositorio {

	public String getId() {
		return "rol";
	}

	public String iconName() {
		return "Tecnico";
	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "1")
	@Named("Lista de Roles")
	public List<Rol> listAll() {
		return container.allInstances(Rol.class);
	}

	@MemberOrder(sequence = "2")
	@Named("Crear Rol")
	@Hidden(where = Where.OBJECT_FORMS)
	public Rol addRol(final @Named("Nombre") String nombre,
			final @Named("Permiso") Permiso permiso) {
		final Rol rol = container.newTransientInstance(Rol.class);
		final SortedSet<Permiso> permissionsList = new TreeSet<Permiso>();
		if (permiso != null) {
			permissionsList.add(permiso);
			rol.setListaPermisos(permissionsList);
		}
		rol.setNombre(nombre.toUpperCase().trim());
		container.persistIfNotAlready(rol);
		return rol;
	}

	@Programmatic
	public Rol addRol(final @Named("Nombre") String nombre,
			final @Named("Permiso") List<Permiso> permisos) {
		final Rol rol = container.newTransientInstance(Rol.class);
		if (permisos != null) {
			SortedSet<Permiso> listaPermisos = new TreeSet<Permiso>(permisos);
			rol.setListaPermisos(listaPermisos);
		}
		rol.setNombre(nombre);
		container.persistIfNotAlready(rol);
		return rol;
	}

	@ActionSemantics(Of.NON_IDEMPOTENT)
	@MemberOrder(sequence = "4")
	@Named("Eliminar Rol")
	public String removeRol(@Named("Rol") Rol rol) {
		String roleName = rol.getNombre();
		container.remove(rol);
		return "El Rol " + roleName + " se ha eliminado correctamente.";
	}

	@javax.inject.Inject
	DomainObjectContainer container;
}
