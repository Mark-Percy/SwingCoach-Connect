import { Injectable } from '@angular/core';

import { environment } from '../../environment/environment';
import { AuthResponse, UserRegistration, UserSignIn } from '../interface/user.interface';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class AuthService {

	private authEndpoint = `${environment.apiUrl}/auth`;

	constructor (private http: HttpClient) {}

	signUp(signUpPayload: UserRegistration): Observable<AuthResponse> {
		return this.http.post<AuthResponse>(`${this.authEndpoint}/register`, signUpPayload);
	}

	signIn(signInPayload: UserSignIn): Observable<AuthResponse> {
		return this.http.post<AuthResponse>(`${this.authEndpoint}/sign-in`, signInPayload);		
	}

	getJwtToken(): string | null {
		return sessionStorage.getItem('jwtToken');
	}

	setJwtToken(token: string): void {
		sessionStorage.setItem('jwtToken', token);	
	}
}
