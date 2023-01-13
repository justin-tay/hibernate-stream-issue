package org.hibernate.bugs;

import java.util.Set;

import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "APP_USER")
@BatchSize(size = 10)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "userIdSeq")
	private Long id;
	
	private String username;
	
	@OneToMany(mappedBy = "owner")
	private Set<Appointment> appointments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}
	
	

}
