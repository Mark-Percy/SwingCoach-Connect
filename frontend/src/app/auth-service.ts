import { Injectable } from '@angular/core';

import { environment } from '../environment/environment';
import { UserRegistration } from './interface/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authEndpoint = `${environment}/auth`;

  signUp(UserRegistration: UserRegistration) {
    console.log(UserRegistration)
  }
  
}
