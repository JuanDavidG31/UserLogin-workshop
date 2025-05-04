package co.edu.unbosque.UserLoginBack.dto;

import java.util.Objects;

public class UserDTO {
	private long id;
	private String user;
	private String password;
	private String name;
	private String cedula;
	private String coutry;
	private String address;

	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public UserDTO(String user, String password, String name, String cedula, String coutry, String address) {
		this();
		this.user = user;
		this.password = password;
		this.name = name;
		this.cedula = cedula;
		this.coutry = coutry;
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	@Override
	public int hashCode() {
		return Objects.hash(id, password, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(user, other.user);
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", cedula="
				+ cedula + ", coutry=" + coutry + ", address=" + address + "]";
	}

}