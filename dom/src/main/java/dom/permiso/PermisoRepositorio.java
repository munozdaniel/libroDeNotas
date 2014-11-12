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
package dom.permiso;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

@DomainService(menuOrder = "82", repositoryFor = Permiso.class)
@Named("Permisos")
public class PermisoRepositorio {

	public String getId() {
		return "Permiso";
	}

	public String iconName() {
		return "Tecnico";
	}

	@MemberOrder(sequence = "1")
	@Named("Nuevo Permiso")
	public Permiso addPermiso(
			final @Named("Nombre") String nombre,
			final @Optional @Named("Package") String pack,
			final @Named("Otro package?") boolean nuevo,
			final @Optional @Named("Nuevo Package") String nuevoPackage,
			@Optional @DescribedAs("Todas las clases: '*' ") @Named("Clase") String clase,
			@Optional @DescribedAs("Todos los Met/Atr: '*' ") @Named("Metodo/Atributo") String campo,
			final @Optional @Named("Permiso de Escritura") boolean escritura) {

		final Permiso permiso = container.newTransientInstance(Permiso.class);

		String paquete = "";
		if (pack == null)
			paquete = nuevoPackage;
		else
			paquete = pack;

		permiso.setNombre(nombre.toUpperCase().trim());
		if (clase == "" || clase == null)
			clase = "*";
		if (campo == "" || campo == null)
			campo = "*";
		String acceso = "*";
		if (!escritura)
			acceso = "r";
		String directorio = paquete + ":" + clase + ":" + campo + ":" + acceso;

		permiso.setPath(directorio);
		permiso.setNombre(nombre);
		container.persistIfNotAlready(permiso);
		return permiso;
	}

	public String validateAddPermiso(final String nombre, final String pack,
			final boolean nuevo, final String nuevoPackage, String clase,
			String campo, boolean escritura) {
		if (nuevo && (nuevoPackage == null || nuevoPackage == ""))
			return "Ingrese un nuevo Package.";
		if (!nuevo && (pack == null || pack == ""))
			return "Seleccione un Package.";
		return null;
	}

	public List<String> choices1AddPermiso(String nombre, String pack,
			boolean nuevo) {
		if (nuevo)
			return null;
		else {
			List<String> lista = new ArrayList<String>();
			for (Package p : Package.values()) {
				lista.add(p.toString());
			}
			return lista;
		}
	}

	public String default4AddPermiso() {
		return "*";
	}

	// public List<String> choices4AddPermiso(String nombre, String pack,
	// boolean nuevo, String nuevoPackage, String clase)
	// throws ClassNotFoundException {
	// String path;
	// if (nuevo)
	// path = nuevoPackage;
	// else
	// path = pack;
	// Class userClass = Class.forName("dom.rol.Rol");
	//if(esCampo)//Agregar campo booleano esCampo
	//  Field[] method = aClass.getFields();
	//else
	// Method[] method = userClass.getMethods();
	// List<String> retorno = new ArrayList<String>();
	//retorno.add("*");
	// for (int i = 0; i < method.length; i++) {
	// retorno.add(method[i].getName());
	// }
	// return retorno;
	// }

	public String default5AddPermiso() {
		return "*";
	}

	public boolean default6AddPermiso() {
		return true;
	}

	@ActionSemantics(Of.NON_IDEMPOTENT)
	@MemberOrder(sequence = "4")
	@Named("Eliminar Permiso")
	public String eliminar(@Named("Permiso") Permiso permiso) {
		String permissionDescription = permiso.getNombre();
		container.remove(permiso);
		return "El Permiso: " + permissionDescription
				+ " ha sido eliminado correctamente.";
	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "2")
	@Named("Todos los Permisos")
	public List<Permiso> listAll() {
		return container.allInstances(Permiso.class);
	}

	public enum Package {
		DISPOSICIONES("dom.disposicion"), DOCUMENTOS("dom.documento"), EXPEDIENTES(
				"dom.expediente"), INICIO("dom.inicio"), MEMO("dom.memo"), NOTA(
				"dom.nota"), PERMISO("dom.permiso"), RESOLUCIONES(
				"dom.resoluciones"), ROL("dom.rol"), SECTOR("dom.sector"), USUARIO(
				"dom.usuario"), SERVICES("dom.services"), ;

		private final String text;

		/**
		 * @param text
		 */
		private Package(final String text) {
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}
	}

//	public void prueba(final @Named("package") Package pack,
//			final @Named("Clases Reflection") String clases) {
//
//	}
//
//	public List<String> choices1Prueba(Package pack, String clases) {
//		Reflections reflections = new Reflections("dom.rol");
//
//		Set<Class<? extends Object>> allClasses = reflections
//				.getSubTypesOf(Object.class);
//
//		System.out.println("CLase " + allClasses.size());
//		List<String> lista = new ArrayList<String>();
//		for (Iterator<Class<? extends Object>> it = allClasses.iterator(); it
//				.hasNext();) {
//			lista.add(it.next().toString());
//		}
//		return lista;
//	}

	@javax.inject.Inject
	DomainObjectContainer container;

}
