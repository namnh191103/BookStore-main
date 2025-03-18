package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends IdBasedEntity {
	
	@Column(length = 40, nullable = false, unique = true)
	private String name;
	
	@Column(columnDefinition = "nvarchar(150)", nullable = false)
	private String descripton;
	
	public Role() {
	}
    
	
	public Role(Integer id) {
		super();
		this.id = id;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if(id == null) {
			if (other.id != null)
				return false;
		}else if(!id.equals(other.id))
			return false;
		return true;
			
	}


	@Override
	public String toString() {
		return this.name;
	}


	public Role(String name) {
		this.name = name;
	}
	
	public Role(String name, String descripton) {
		this.name = name;
		this.descripton = descripton;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescripton() {
		return descripton;
	}

	public void setDescripton(String descripton) {
		this.descripton = descripton;
	}

}
