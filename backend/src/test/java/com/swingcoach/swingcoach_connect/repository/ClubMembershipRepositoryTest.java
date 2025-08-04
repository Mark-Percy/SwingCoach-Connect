package com.swingcoach.swingcoach_connect.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.swingcoach.swingcoach_connect.model.Club;
import com.swingcoach.swingcoach_connect.model.ClubMembership;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.model.Club.ClubStatus;
import com.swingcoach.swingcoach_connect.model.ClubMembership.ClubRoleInClub;
import com.swingcoach.swingcoach_connect.model.User.AccountStatus;

@DataJpaTest
@ActiveProfiles("test")
public class ClubMembershipRepositoryTest {

	private final static Logger logger = LoggerFactory.getLogger(ClubMembershipRepositoryTest.class);

	@Autowired
	private ClubMembershipRepository clubMembershipRepository;
	
	@Autowired
	private ClubRepository clubRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	User testUser;
	Club testClub;
	ClubMembership clubMembership;

	@BeforeEach
	void setup() {
		logger.info("Setting up test User");
		testUser = new User();
		testUser.setEmail("test@test.com");
		testUser.setFirstName("test");
		testUser.setLastName("test");
		testUser.setPasswordHash("test");
		testUser.setAccountStatus(AccountStatus.ACTIVE);
		testUser.setIsEmailVerified(true);
		logger.info("Setting up test club");
		testClub = new Club();
		testClub.setCompanyName("testClub");
		testClub.setName("testClub");
		testClub.setAddressPostCode("testClub");
		testClub.setAddressStreet("testClub");
		testClub.setNumberOFBays(4);
		testClub.setStatus(ClubStatus.PENDING_VERIFICATION);
		logger.info("Test data finished");
	}
	
	@Test
	void testAddAndRetrieveUser() {
		User savedUser = userRepository.save(testUser);
		Club savedClub = clubRepository.save(testClub);
		clubMembership = new ClubMembership();
		clubMembership.setClub(savedClub);
		clubMembership.setUser(savedUser);
		clubMembership.setRoleInClub(ClubRoleInClub.CLUB_ADMIN);
		ClubMembership savedMember = clubMembershipRepository.save(clubMembership);

		assertNotNull(savedMember.getId(), "Id should be generated");

		Long id = savedMember.getId();

		Optional<ClubMembership> retrievedClubMembershipOptional = clubMembershipRepository.findById(id);
		assertTrue(retrievedClubMembershipOptional.isPresent(), "Membership should be retrieved");
		
		ClubMembership retrievedClubMembership = retrievedClubMembershipOptional.get();
		assertEquals(clubMembership.getClub(), retrievedClubMembership.getClub(), "Club should be the same");
		assertEquals(clubMembership.getUser(), retrievedClubMembership.getUser(), "User should be the same");
		assertEquals(clubMembership.getRoleInClub(), retrievedClubMembership.getRoleInClub(), "Role should be the same");
	}
}
