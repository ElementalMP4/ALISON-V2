package main.java.de.voidtech.alison.ephemeral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ignoreduser")
public class IgnoredUser {
	
	@Id
	@Column
	private String userID;
	
	@Deprecated
	IgnoredUser() {
	}
	
	public IgnoredUser(String userID) {
		this.userID = userID;
	}

}
