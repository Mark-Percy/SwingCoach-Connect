import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../service/auth-service';

@Component({
	selector: 'app-sign-in',
	imports: [ReactiveFormsModule],
	templateUrl: './sign-in.html',
})
export class SignIn {

	public signInForm: FormGroup;

	message: string = '';
	isLoading = false;

	constructor (private fb: FormBuilder, private authService: AuthService) {
		this.signInForm = this.fb.group({
			email: ['', [Validators.email, Validators.required]],
			password: ['', [Validators.required, Validators.minLength(8)]],
		});
	}

	signIn(): void {
		if (this.signInForm.valid) {
			this.isLoading = true;
			this.authService.signIn(this.signInForm.value).subscribe({
				next: resp => {
					if (resp.token) this.authService.setJwtToken(resp.token);
					else this.message = 'Login Unsucessful';
					this.isLoading = false;
					console.log(this.isLoading)
				},
				error: error => {
					this.message = error.error.message;
					this.signInForm.markAllAsTouched();
					this.isLoading = false;
				}
			});
		}
	}
}
