package com.swingcoach.swingcoach_connect.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clubs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Club {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(unique = true, nullable = false)
	private String name;
	
	@Column(unique = true)
	private String email;
	
	@Column(name = "company_name", unique = true, nullable = false)
	private String companyName;
	
	@Column(name = "website_url", unique = true)
	private String websiteURL;
	
	@Column(name = "addressStreet", unique = true, nullable = false)
	private String addressStreet;
	
	@Column(name = "address_postcode", unique = true, nullable = false)
	private String addressPostCode;
	
	@Column(name = "number_of_bays", unique = true, nullable = false)
	private Integer numberOFBays;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClubStatus status;
		
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;


	public enum ClubStatus {
		ACTIVE, DEACTIVATED, PENDING_VERIFICATION, REJECTED
	}

}
