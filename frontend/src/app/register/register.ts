import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-register',
  standalone:true,
  imports: [ReactiveFormsModule],
  templateUrl: './register.html'
})
export class Register {
  
  registerForm: FormGroup;
  disableSubmit: boolean = false;
  
  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: [''],
      dateOfBirth: [''],
    },{
      validators: Register.passwordMatchValidator
    });
  }

  static passwordMatchValidator: ValidatorFn = (control: AbstractControl): { [key: string]: boolean } | null => {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword || !password.value || !confirmPassword.value) {
      return null;
    }

    if (password.value === confirmPassword.value) {
      confirmPassword.setErrors(null);
      return null;
    } else {
      confirmPassword.setErrors({ passwordsMismatch: true });
      return { passwordsMismatch: true };
    }
  };

  hasError(controlName: string, errorName: string): boolean {
    const control = this.registerForm.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched));
  }


  submitRegistration() {
    if(this.registerForm.valid) {
      this.disableSubmit = true;
      this.authService.signUp(this.registerForm.value);
    }
  }
}
