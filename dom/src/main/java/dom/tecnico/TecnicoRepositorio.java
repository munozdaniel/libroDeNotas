package dom.tecnico;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.query.QueryDefault;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@Named("TECNICO")
public class TecnicoRepositorio {

	public TecnicoRepositorio() {

	}

	// //////////////////////////////////////
	// Icono
	// //////////////////////////////////////

	public String getId() {
		return "tecnico";
	}

	public String iconName() {
		return "Tecnico";
	}

	// //////////////////////////////////////
	// Agregar Tecnico
	// //////////////////////////////////////
	
	@MemberOrder(sequence = "10")
	@Named("Agregar")
	public Tecnico addTecnico(final @Optional Sector sector,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Apellido") String apellido,
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Nombre") String nombre,
			final @Optional @RegEx(validation = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") @Named("E-mail") String email,
			final @Named("N° Computadoras") BigDecimal cantidadComputadora
			) {
		
		return nuevoTecnico(apellido, nombre, email,sector, this.currentUserName(),cantidadComputadora);
	}
	
	@Programmatic
	public Tecnico nuevoTecnico(final String apellido, final String nombre,
			final String email,final Sector sector,
			final String creadoPor,
			final BigDecimal cantidadComputadora) {
		final Tecnico unTecnico = container.newTransientInstance(Tecnico.class);
		unTecnico.setApellido(apellido.toUpperCase().trim());
		unTecnico.setNombre(nombre.toUpperCase().trim());
		unTecnico.setEmail(email);
		unTecnico.setHabilitado(true);
		unTecnico.setCreadoPor(creadoPor);
		if(sector!=null)
		{	
			unTecnico.setSector(sector);
			sector.add(unTecnico);
		}
		unTecnico.setCantidadComputadora(cantidadComputadora);
		container.persistIfNotAlready(unTecnico);
		container.flush();
		return unTecnico;

	}
	
	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////
	
	@Named("Sector")
	@DescribedAs("Buscar el Sector en mayuscula")
	public List<Sector> autoComplete0AddTecnico(final @MinLength(2) String search) {
		return sectorRepositorio.autoComplete(search);
	}
	
	// //////////////////////////////////////
	// Listar Tecnico
	// //////////////////////////////////////
	
	@MemberOrder(sequence="20")
	public List<Tecnico> listar()
	{
		final List<Tecnico> listaTecnicos = this.container.allMatches(
				new QueryDefault<Tecnico>(Tecnico.class, "eliminarTecnicoTrue","creadoPor",this.currentUserName()));
		if(listaTecnicos.isEmpty())
		{
			this.container.warnUser("No hay tecnicos cargados en el sistema");
		}
		return listaTecnicos;
				
	}
	
	
	// //////////////////////////////////////
	// Buscar Tecnico
	// //////////////////////////////////////
	
	@MemberOrder(sequence="30")
	public List<Tecnico> buscar(
			final @RegEx(validation = "[a-zA-Záéíóú]{2,15}(\\s[a-zA-Záéíóú]{2,15})*") @Named("Apellido") @MinLength(2) String apellidoUsuario)
	{
		final List<Tecnico> listarTecnicos = this.container.allMatches(
				new QueryDefault<Tecnico>(Tecnico.class, "buscarPorApellido", "creadoPor", this.currentUserName(), "apellido", apellidoUsuario.toUpperCase().trim()));
		if(listarTecnicos.isEmpty())
			this.container.warnUser("No se encontraron Tecnicos cargados en el sistema.");
		return listarTecnicos;
	}
	
	
    @Programmatic
    public List<Tecnico> autoComplete(final String apellido) {
        return container.allMatches(
                new QueryDefault<Tecnico>(Tecnico.class, 
                        "autoCompletarPorApellido", 
                        "creadoPor", currentUserName(), 
                        "apellido", apellido));
    }
	
	
	// //////////////////////////////////////
	// CurrentUserName
	// //////////////////////////////////////
	
	private String currentUserName() {
		return container.getUser().getName();
	}

	// //////////////////////////////////////
	// Injected Services
	// //////////////////////////////////////

	@javax.inject.Inject
	private DomainObjectContainer container;
	
    @javax.inject.Inject
    private SectorRepositorio sectorRepositorio;
}