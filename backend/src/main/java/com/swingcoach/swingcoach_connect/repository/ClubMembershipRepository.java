package com.swingcoach.swingcoach_connect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swingcoach.swingcoach_connect.model.ClubMembership;

@Repository
public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long>{

    
}
