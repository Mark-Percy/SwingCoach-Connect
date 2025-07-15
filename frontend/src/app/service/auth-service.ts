import { Injectable } from '@angular/core';

import { environment } from '../../environment/environment';
import { AuthResponse, UserRegistration } from '../interface/user.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private authEndpoint = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  signUp(backendPayload: UserRegistration) {
    return this.http.post<AuthResponse>(`${this.authEndpoint}/register`, backendPayload)
  }
}
