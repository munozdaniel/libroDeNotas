package dom.usuarioshiro;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.value.Date;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@ObjectType("usuarios")
@Bounded
public class Usuario {
	
	private int usuario_id;
	
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
    @PrimaryKey
	@MemberOrder(sequence = "0")
	@Column(allowsNull = "false")
	public int getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}
	
	private String usuario_nick;

	@MemberOrder(sequence = "10")
	@Column(allowsNull = "false")
	public String getUsuario_nick() {
		return usuario_nick;
	}

	public void setUsuario_nick(String usuario_nick) {
		this.usuario_nick = usuario_nick;
	}

	private String usuario_nombreCompleto;

	@MemberOrder(sequence = "20")
	@Column(allowsNull = "false")
	public String getUsuario_nombreCompleto() {
		return usuario_nombreCompleto;
	}

	public void setUsuario_nombreCompleto(String usuario_nombreCompleto) {
		this.usuario_nombreCompleto = usuario_nombreCompleto;
	}

	private String usuario_contrasenia;

	@MemberOrder(sequence = "33")
	@Column(allowsNull = "false")
	public String getUsuario_contrasenia() {
		return usuario_contrasenia;
	}

	public void setUsuario_contrasenia(String usuario_contrasenia) {
		this.usuario_contrasenia = usuario_contrasenia;
	}

	private int usuario_sector;

	@MemberOrder(sequence = "44")
	@Column(allowsNull = "false")
	@Hidden
	public int getUsuario_sector() {
		return usuario_sector;
	}

	public void setUsuario_sector(int usuario_sector) {
		this.usuario_sector = usuario_sector;
	}

	private String usuario_email;

	@MemberOrder(sequence = "55")
	@Column(allowsNull = "false")
	public String getUsuario_email() {
		return usuario_email;
	}

	public void setUsuario_email(String usuario_email) {
		this.usuario_email = usuario_email;
	}

	private int usuario_activo;

	@MemberOrder(sequence = "66")
	@Column(allowsNull = "false")
	public int getUsuario_activo() {
		return usuario_activo;
	}

	public void setUsuario_activo(int usuario_activo) {
		this.usuario_activo = usuario_activo;
	}

	private Date usuario_fechaCreacion;

	@MemberOrder(sequence = "77")
	@Column(allowsNull = "false")
	public Date getUsuario_fechaCreacion() {
		return usuario_fechaCreacion;
	}

	public void setUsuario_fechaCreacion(Date usuario_fechaCreacion) {
		this.usuario_fechaCreacion = usuario_fechaCreacion;
	}



}
