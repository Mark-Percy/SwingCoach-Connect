package com.swingcoach.swingcoach_connect.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swingcoach.swingcoach_connect.dto.club.CreateClubRequest;
import com.swingcoach.swingcoach_connect.dto.club.CreateClubResponse;
import com.swingcoach.swingcoach_connect.exception.UserNotFoundException;
import com.swingcoach.swingcoach_connect.model.Club;
import com.swingcoach.swingcoach_connect.model.ClubMembership;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.model.ClubMembership.ClubRoleInClub;
import com.swingcoach.swingcoach_connect.repository.ClubMembershipRepository;
import com.swingcoach.swingcoach_connect.repository.ClubRepository;

@Service
public class ClubService {

	private final static Logger logger = LoggerFactory.getLogger(ClubService.class);

	private final ClubRepository clubRepository;
	private final ClubMembershipRepository clubMembershipRepository;
	private final UserService userService;

	ClubService(ClubRepository clubRepository, ClubMembershipRepository clubMembershipRepository, UserService userService){
		this.clubRepository = clubRepository;
		this.clubMembershipRepository = clubMembershipRepository;
		this.userService = userService;
	}

	@Transactional
	public CreateClubResponse createClub(CreateClubRequest request) throws IllegalStateException, IllegalArgumentException, UserNotFoundException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || !authentication.isAuthenticated()) {
			logger.info("Attempted create club while not logged in");
			throw new IllegalStateException("User not authenticated to create a club");
		}

		if(this.clubRepository.findByNameAndStatus(request.getName(), Club.ClubStatus.ACTIVE).isPresent()) {
			logger.info("Active club found with name " + request.getName());
			throw new IllegalArgumentException("Club with that name already exists");
		}

		// Get user to link to club
		String email = ((UserDetails) authentication.getPrincipal()).getUsername();
		User currentUser;
		Optional<User> currentUserOptional = userService.findByEmail(email);
		if(currentUserOptional.isPresent()) {
			currentUser = currentUserOptional.get();
		} else {
			logger.info("Authenticated user not found in database");
			throw new UserNotFoundException("Issue with your account details and those stored");
		}
		
		// Setup new Club
		logger.info("Setting up new Club");
		Club club = new Club();
		club.setName(request.getName());
		club.setCompanyName(request.getCompanyName());
		club.setAddressStreet(request.getAddressStreet());
		club.setAddressPostCode(request.getAddressPostcode());
		club.setEmail(request.getEmail());
		club.setNumberOFBays(request.getNumberOfBays());
		club.setWebsiteURL(request.getWebsiteUrl());
		club.setStatus(Club.ClubStatus.PENDING_VERIFICATION);

		Club savedClub = clubRepository.save(club);
		// Set up club membership for creator
		ClubMembership clubMembership = new ClubMembership();
		clubMembership.setClub(savedClub);
		clubMembership.setUser(currentUser);
		clubMembership.setIsBanned(false);
		clubMembership.setRoleInClub(ClubRoleInClub.CLUB_ADMIN);
		
		clubMembershipRepository.save(clubMembership);

		return new CreateClubResponse("Club: " + request.getName() + " has been created, please wait for verification to process, which can be tracked within Your account.");
	}
}
