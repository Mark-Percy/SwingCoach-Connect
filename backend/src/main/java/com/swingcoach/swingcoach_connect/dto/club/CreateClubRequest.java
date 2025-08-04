package com.swingcoach.swingcoach_connect.dto.club;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClubRequest {
	@NotBlank(message = "Name must be filled")
	private String name;

	@Email(message = "Email must be valid")
	private String email;

	@NotBlank(message = "Must have a company name")
	private String companyName;

	private String websiteUrl;

	@NotBlank(message = "Must have a valid address")
	private String addressStreet;

	@NotBlank(message = "Must have a valid Postcode")
	private String addressPostcode;

	@NotNull(message = "Must have bays for lessons")
	private Integer numberOfBays;
}
