package com.swingcoach.swingcoach_connect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swingcoach.swingcoach_connect.dto.club.CreateClubRequest;
import com.swingcoach.swingcoach_connect.dto.club.CreateClubResponse;
import com.swingcoach.swingcoach_connect.exception.UserNotFoundException;
import com.swingcoach.swingcoach_connect.service.ClubService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/club")
public class ClubController {

	private final ClubService clubService;
	ClubController (ClubService clubService) {
		this.clubService = clubService;
	}

	@PostMapping("/create")
	public ResponseEntity<CreateClubResponse> createClub(@Valid @RequestBody CreateClubRequest request) {
		try {
			CreateClubResponse response = this.clubService.createClub(request);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(new CreateClubResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(new CreateClubResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (IllegalStateException e) {
			return new ResponseEntity<>(new CreateClubResponse("You are not authorized for this."), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<>(new CreateClubResponse("Unknown Error occured."), HttpStatus.BAD_REQUEST);
		}
	}
}
