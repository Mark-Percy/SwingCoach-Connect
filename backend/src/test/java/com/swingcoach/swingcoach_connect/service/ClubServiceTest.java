package com.swingcoach.swingcoach_connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import com.swingcoach.swingcoach_connect.dto.club.CreateClubRequest;
import com.swingcoach.swingcoach_connect.dto.club.CreateClubResponse;
import com.swingcoach.swingcoach_connect.exception.UserNotFoundException;
import com.swingcoach.swingcoach_connect.model.Club;
import com.swingcoach.swingcoach_connect.model.ClubMembership;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.repository.ClubMembershipRepository;
import com.swingcoach.swingcoach_connect.repository.ClubRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ClubServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(ClubServiceTest.class);
	@Mock
	private ClubRepository clubRepository;

	@Mock
	private ClubMembershipRepository clubMembershipRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private ClubService clubService;

	private SecurityContext securityContext;
	private User authenticatedUser;
	private Authentication authentication;

	@Nested
	class CreateClub {
		private CreateClubRequest createClubRequest;

		@BeforeEach
		void setup() {
			createClubRequest = new CreateClubRequest(
				"Test Club",
				"club.email@email.com",
				"Test Company",
				"www.webUrL.com",
				"Test Street",
				"LT2 2TT",
				30
			);

			authenticatedUser = new User();
			authenticatedUser.setEmail("test@test.com");
			authenticatedUser.setFirstName("Test");
			authenticatedUser.setLastName("User");

			UserDetails userDetails= mock(UserDetails.class);
			lenient().when(userDetails.getUsername()).thenReturn(authenticatedUser.getEmail());

			authentication = mock(Authentication.class);
			lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
			when(authentication.isAuthenticated()).thenReturn(true);

			securityContext = mock(SecurityContext.class);
			when(securityContext.getAuthentication()).thenReturn(authentication);

			SecurityContextHolder.setContext(securityContext);
		}
	
		@Test
		void test_SuccessfulCreateClub() {

			logger.info("successful Create Club Test");
			when(clubRepository.findByNameAndStatus(createClubRequest.getName(), Club.ClubStatus.ACTIVE)).thenReturn(Optional.empty());
			when(userService.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));

			when(clubRepository.save(any(Club.class)))
            .thenAnswer(invocation -> {
                Club club = invocation.getArgument(0);
                club.setId(1L);
                return club;
            });
			
			when(clubMembershipRepository.save(any(ClubMembership.class)))
            .thenAnswer(invocation -> {
                ClubMembership clubMembership = invocation.getArgument(0);
                clubMembership.setId(1L);
                return clubMembership;
            });

			CreateClubResponse createClubResponse = clubService.createClub(createClubRequest);
			assertNotNull(createClubResponse, "createClubResponse should not be Null");
			assertEquals("Club: Test Club has been created, please wait for verification to process, which can be tracked within Your account.", createClubResponse.getMessage());
		}
	
		@Test
		void test_UnsuccessfulCreateClub_ActiveClubWithNameExists() {
			logger.info("Club with name already exists");
			Club club = new Club();
			club.setName(createClubRequest.getName());
			club.setStatus(Club.ClubStatus.ACTIVE);


			when(clubRepository.findByNameAndStatus(createClubRequest.getName(), Club.ClubStatus.ACTIVE)).thenReturn(Optional.of(club));

			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
				clubService.createClub(createClubRequest);
			});
			assertEquals("Club with that name already exists", exception.getMessage());

			verify(clubRepository, never()).save(any(Club.class));
			verify(clubMembershipRepository, never()).save(any(ClubMembership.class));
		}
		
		@Test
		void test_UnsuccessfulCreateClub_AuthenticatedUserNotFoundInDB() {
			logger.info("Authenticated User doesn't exist in db");
			//In reality this should never happen, as a user would not be able to login if they didn't exist in db

			when(clubRepository.findByNameAndStatus(createClubRequest.getName(), Club.ClubStatus.ACTIVE)).thenReturn(Optional.empty());
			when(userService.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.empty());

			UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
				clubService.createClub(createClubRequest);
			});
			assertNotNull(exception, "createClubResponse should not be Null");
			assertEquals("Issue with your account details and those stored", exception.getMessage());

			verify(clubRepository, never()).save(any(Club.class));
			verify(clubMembershipRepository, never()).save(any(ClubMembership.class));
		}
	}
}
