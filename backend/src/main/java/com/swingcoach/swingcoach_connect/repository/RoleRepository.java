package com.swingcoach.swingcoach_connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swingcoach.swingcoach_connect.model.Role;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Integer>{

	Optional<Role> findByName(String name);
}