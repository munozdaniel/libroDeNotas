package dom.usuarioshiro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.sql.DataSource;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.datanucleus.store.rdbms.datasource.dbcp.ConnectionFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.DriverManagerConnectionFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolableConnectionFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolingDataSource;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPoolFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.StackKeyedObjectPoolFactory;

@DomainService(menuOrder = "80", repositoryFor = Usuario.class)
@Named("Configuracion de Usuario")
public class UsuarioRepositorio {
	public String getId() {
		return "usuarios";
	}

	public String iconName() {
		return "Tecnico";
	}
//
//	@Programmatic
//	@PostConstruct
//	public void init() {
//		List<UsuarioShiro> usuarios = listAll();
//		if (usuarios.isEmpty()) {
//			Permiso permiso = new Permiso();
//			Rol rol = new Rol();
//			SortedSet<Permiso> permisos = new TreeSet<Permiso>();
//
//			permiso.setNombre("ADMIN");
//			permiso.setPath("*");
//			permisos.add(permiso);
//			rol.setNombre("ADMINISTRADOR");
//			rol.setListaPermisos(permisos);
//
//			// addUsuarioShiro("sven", "pass", rol);
//
//		}
//	}

	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "1")
	@Named("Ver todos")
	public List<Usuario> listAll() {
			PersistenceManagerFactory pm = this.conexion();
			PersistenceManager persistencia = pm.getPersistenceManager();
			Query q = persistencia.newQuery("javax.jdo.query.SQL",
					"SELECT * FROM usuarios");
			q.setResultClass(Usuario.class);
			List<Usuario> results = (List<Usuario>) (q.execute());
			return results;

	}
	private PersistenceManagerFactory conexion()
	{
		Properties properties = new Properties();
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", 
		    "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		properties.setProperty("javax.jdo.option.ConnectionURL","jdbc:mysql://192.168.42.14/gestionusuarios");
		properties.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
		properties.setProperty("javax.jdo.option.ConnectionUserName","root");
		properties.setProperty("javax.jdo.option.ConnectionPassword","infoimps");
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(properties);
		return pmf;
	}
	private PersistenceManagerFactory conectar() throws ClassNotFoundException {
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		// Create the actual pool of connections
		ObjectPool connectionPool = new GenericObjectPool(null);

		// Create the factory to be used by the pool to create the connections
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				"jdbc:mysql://192.168.42.14/gestionusuarios", "root",
				"infoimps");

		// Create a factory for caching the PreparedStatements
		KeyedObjectPoolFactory kpf = new StackKeyedObjectPoolFactory(null, 20);

		// Wrap the connections with pooled variants
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(
				connectionFactory, connectionPool, kpf, null, false, true);

		// Create the datasource
		DataSource ds = new PoolingDataSource(connectionPool);

		// Create our PMF
		Map<String, DataSource> properties = new HashMap<String, DataSource>();
		properties.put("javax.jdo.option.ConnectionFactory", ds);
		return JDOHelper.getPersistenceManagerFactory(properties);
	}
//
//	@javax.inject.Inject
//	private IsisJdoSupport isisJdoSupport;

}
