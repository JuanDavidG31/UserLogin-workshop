package co.edu.unbosque.UserLoginBack.dto;

import java.util.Objects;

import co.edu.unbosque.UserLoginBack.model.User.Role;

public class UserDTO {
	private Long id;
	private String user;
	private String password;
	private String name;
	private String cedula;
	private String coutry;
	private String address;
	private Role role;

	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public UserDTO(String user, String password, String name, String cedula, String coutry, String address) {
		this.user = user;
		this.password = password;
		this.name = name;
		this.cedula = cedula;
		this.coutry = coutry;
		this.address = address;
	}

	public UserDTO(String user, String password, String name, String cedula, String coutry, String address, Role role) {
		this.user = user;
		this.password = password;
		this.name = name;
		this.cedula = cedula;
		this.coutry = coutry;
		this.address = address;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getCoutry() {
		return coutry;
	}

	public void setCoutry(String coutry) {
		this.coutry = coutry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, role, user);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad. Dos objetos UserDTO
	 * son iguales si tienen el mismo id, username, password y role.
	 *
	 * @param obj Objeto a comparar
	 * @return true si los objetos son iguales, false en caso contrario
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password) && role == other.role
				&& Objects.equals(user, other.user);
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", cedula="
				+ cedula + ", coutry=" + coutry + ", address=" + address + "]";
	}

}