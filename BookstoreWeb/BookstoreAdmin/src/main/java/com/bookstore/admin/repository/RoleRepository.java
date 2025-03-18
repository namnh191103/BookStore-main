package com.bookstore.admin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

	
}
