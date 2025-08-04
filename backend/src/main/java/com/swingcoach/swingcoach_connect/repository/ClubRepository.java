package com.swingcoach.swingcoach_connect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swingcoach.swingcoach_connect.model.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>{
    public Optional<Club> findByName(String name);
    public Optional<Club> findByNameAndStatus(String name, Club.ClubStatus status);
	
}