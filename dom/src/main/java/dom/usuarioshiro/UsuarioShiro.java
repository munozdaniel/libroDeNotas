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

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Title;

import dom.rol.Rol;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@ObjectType("UsuarioShiro")
@Bounded
public class UsuarioShiro {

	private String nick;

	@MemberOrder(sequence = "1")
	@Column(allowsNull = "false")
	@Title
	public String getNick() {
		return nick;
	}

	public void setNick(final String nick) {
		this.nick = nick;
	}

	private String password;

	@MemberOrder(sequence = "2")
	@Column(allowsNull = "false")
	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Join
	@Element(dependent = "false")
	private SortedSet<Rol> listaDeRoles = new TreeSet<Rol>();

	@MemberOrder(sequence = "3")
	@Render(org.apache.isis.applib.annotation.Render.Type.EAGERLY)
	public SortedSet<Rol> getListaDeRoles() {
		return listaDeRoles;
	}

	public void setRolesList(final SortedSet<Rol> listaDeRoles) {
		this.listaDeRoles = listaDeRoles;
	}

	@MemberOrder(sequence = "3")
	@Named("Agregar Rol")
	@DescribedAs("Agrega un Rol al Usuario.")
	public UsuarioShiro addRole(final @Named("Role") Rol rol) {

		listaDeRoles.add(rol);

		return this;
	}

	@MemberOrder(sequence = "5")
	@Named("Eliminar")
	public UsuarioShiro removeRole(final @Named("Rol") Rol rol) {

		getListaDeRoles().remove(rol);
		return this;
	}

	public SortedSet<Rol> choices0RemoveRole() {
		return getListaDeRoles();
	}

	@javax.inject.Inject
	DomainObjectContainer container;

}
