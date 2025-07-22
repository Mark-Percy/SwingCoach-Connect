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


@DataJpaTest
@ActiveProfiles("test")
public class ClubRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(ClubRepositoryTest.class);

	@Autowired
	private ClubRepository clubRepository;

	private Club club;

	@BeforeEach
	void setupClub() {
		club = new Club();
		club.setName("test Club");
		club.setNumberOFBays(4);
		club.setCompanyName("test Company");
		club.setClubStatus(Club.ClubStatus.PENDING_VERIFICATION);
		club.setAddressPostCode("L23 0dws");
		club.setAddressStreet("ddsafdsa");
	}

	@Test
	void testSavingAndReturnClub() {
		Club savedClub = clubRepository.save(club);
		assertNotNull(savedClub.getId(), "Id should be generated");
		Long id = savedClub.getId();

		Optional<Club> retrievedClubOptional = clubRepository.findById(id);

		assertTrue(retrievedClubOptional.isPresent(), "Club should be retrieved");
		Club retrievedClub = retrievedClubOptional.get();
		assertEquals(club.getName(), retrievedClub.getName());
		assertEquals(club.getNumberOFBays(), retrievedClub.getNumberOFBays());
		assertEquals(club.getCompanyName(), retrievedClub.getCompanyName());
		assertEquals(club.getClubStatus(), retrievedClub.getClubStatus());
		assertEquals(club.getAddressPostCode(), retrievedClub.getAddressPostCode());
		assertEquals(club.getAddressStreet(), retrievedClub.getAddressStreet());
	}
}
