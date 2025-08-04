package com.swingcoach.swingcoach_connect.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.swingcoach.swingcoach_connect.dto.club.CreateClubRequest;
import com.swingcoach.swingcoach_connect.dto.club.CreateClubResponse;
import com.swingcoach.swingcoach_connect.exception.UserNotFoundException;
import com.swingcoach.swingcoach_connect.service.ClubService;
import com.swingcoach.swingcoach_connect.service.security.CustomUserDetailsService;
import com.swingcoach.swingcoach_connect.service.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = ClubController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
public class ClubControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ClubService clubService;

	@MockitoBean
	private JwtService jwtService;
	
	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	private CreateClubRequest validCreateClubRequest;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		validCreateClubRequest = new CreateClubRequest("Test Club", "club.email@test.com", "company name", "www.club.com", "9 fake avenue", "Lt2 3DA", 30);
	}

	@Test
	void test_SuccessCreateClub() throws Exception{
		when(clubService.createClub(validCreateClubRequest)).thenReturn(new CreateClubResponse("Club: Test Club has been created, please wait for verification to process, which can be tracked within Your account."));
		
		mockMvc.perform(post("/api/club/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validCreateClubRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("Club: Test Club has been created, please wait for verification to process, which can be tracked within Your account."));
	}

	@Test
	void test_UnsuccessfulCreateClub_ClubAlreadyExists() throws Exception{
		when(clubService.createClub(validCreateClubRequest)).thenThrow(new IllegalArgumentException("Club with that name already exists"));
		
		mockMvc.perform(post("/api/club/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validCreateClubRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Club with that name already exists"));
	}
	
	@Test
	void test_UnsuccessfulCreateClub_AuthenticatedUserNotFoundInDB() throws Exception{
		when(clubService.createClub(validCreateClubRequest)).thenThrow(new UserNotFoundException("Issue with your account details and those stored"));
		
		mockMvc.perform(post("/api/club/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validCreateClubRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Issue with your account details and those stored"));
	}
	
	@Test
	void test_UnsuccessfulCreateClub_UnauthenticatedUserNotFoundInDB() throws Exception{
		when(clubService.createClub(validCreateClubRequest)).thenThrow(new UserNotFoundException("User not authenticated to create a club"));
		
		mockMvc.perform(post("/api/club/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validCreateClubRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("User not authenticated to create a club"));
	}
}
