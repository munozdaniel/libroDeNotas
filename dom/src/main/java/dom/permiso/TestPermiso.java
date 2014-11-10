package dom.permiso;

import java.util.Set;

import org.reflections.Reflections;


public class TestPermiso {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Reflections reflections = new Reflections("dom.rol.Rol");

		 Set<Class<? extends Object>> allClasses = 
		     reflections.getSubTypesOf(Object.class);
		 
		 System.out.println("CLase "+ allClasses.size());
//		List<String> retorno = new ArrayList<String>();
// 
//		List<Class<?>> clases = ClassFinder.find("dom.rol");
//		 for(int i=0; i<clases.size();i++)
//			{
//				System.out.println("CLASES "+ clases.get(i).getSimpleName());
//				retorno.add(clases.get(i).getSimpleName());
//			}
//		 System.out.println("CLase "+ clases.get(0).getSimpleName());

	}

}
