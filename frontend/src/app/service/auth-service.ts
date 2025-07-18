import { Injectable } from '@angular/core';

import { environment } from '../../environment/environment';
import { AuthResponse, UserRegistration, UserSignIn } from '../interface/user.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root',
})
export class AuthService {

	private authEndpoint = `${environment.apiUrl}/auth`;

	constructor (private http: HttpClient) {}

	signUp (signUpPayload: UserRegistration) {
		return this.http.post<AuthResponse>(`${this.authEndpoint}/register`, signUpPayload);
	}

	signIn (signInPayload: UserSignIn) {
		return this.http.post<AuthResponse>(`${this.authEndpoint}/sign-in`, signInPayload);		
	}

	getJwtToken () {
		return sessionStorage.getItem('jwtToken');
	}

	setJwtToken (token: string) {
		sessionStorage.setItem('jwtToken', token);	
	}
}
