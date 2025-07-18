import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { SignIn } from './sign-in';
import { FormBuilder } from '@angular/forms';
import { AuthService } from '../service/auth-service';
import { By } from '@angular/platform-browser';
import { concatMap, delay, of, throwError, timer } from 'rxjs';

describe('SignIn', () => {
	let component: SignIn;
	let fixture: ComponentFixture<SignIn>;
	let authServiceMock: any;

	let emailInput: HTMLInputElement;
	let passwordInput: HTMLInputElement;
	let submitButton: HTMLElement;

  	beforeEach(async () => {
		authServiceMock = jasmine.createSpyObj(AuthService, ['signIn','setJwtToken']);

		
		await TestBed.configureTestingModule({
			imports: [SignIn],
			providers: [
				FormBuilder,
				{provide: AuthService, useValue: authServiceMock}
			],
		})
		.compileComponents();
		
		fixture = TestBed.createComponent(SignIn);
		component = fixture.componentInstance;
		fixture.detectChanges();
		emailInput = fixture.debugElement.query(By.css('[name="email"]')).nativeElement;
		passwordInput = fixture.debugElement.query(By.css('[name="password"]')).nativeElement;
		submitButton = fixture.debugElement.query(By.css('#submit')).nativeElement;
  	});

	it('should create with an empty form', () => {
		expect(component).toBeTruthy();
		expect(component.signInForm.invalid).toBeTrue();
		expect(emailInput.value).toEqual('');
		expect(passwordInput.value).toEqual('');
	});
	
	it('should allow data entry and allow submit, allow sign in and call to set jwt token', fakeAsync(() => {
		authServiceMock.signIn.and.returnValue(of({message: 'success', token: 'token'}).pipe(delay(100)));
		expect(component).toBeTruthy();
		expect(component.signInForm.invalid).toBeTrue();
		expect(emailInput.value).toEqual('');
		expect(passwordInput.value).toEqual('');

		emailInput.value = 'example@example.com';
		passwordInput.value = 'Password1';
		emailInput.dispatchEvent(new Event('input'));
		passwordInput.dispatchEvent(new Event('input'));

		fixture.detectChanges();

		expect(component.signInForm.valid).toBeTrue();
		expect(component.isLoading).toBeFalse();

		submitButton.click();
		fixture.detectChanges();
		expect(component.isLoading).toBeTrue();
		tick(100);
		expect(authServiceMock.signIn).toHaveBeenCalled();
		expect(authServiceMock.setJwtToken).toHaveBeenCalledOnceWith('token');
		expect(component.isLoading).toBeFalse();
	}));

	it('should allow data entry and allow submit, disallow sign in and not call to set jwt token', fakeAsync(() => {
		
		const backendErrorMessage = 'Invalid email or password';
		const httpErrorResponse = {
			status: 400,
			statusText: 'Bad Request',
			error: { message: backendErrorMessage }
		};
		
		authServiceMock.signIn.and.returnValue(
			timer(100).pipe(
			  concatMap(() => throwError(() => httpErrorResponse))
			)
		);

		expect(component).toBeTruthy();
		expect(component.signInForm.invalid).toBeTrue();
		expect(emailInput.value).toEqual('');
		expect(passwordInput.value).toEqual('');

		emailInput.value = 'example@example.com';
		passwordInput.value = 'Password1';
		emailInput.dispatchEvent(new Event('input'));
		passwordInput.dispatchEvent(new Event('input'));

		fixture.detectChanges();

		expect(component.signInForm.valid).toBeTrue();
		expect(component.isLoading).toBeFalse();

		submitButton.click();
		fixture.detectChanges();
		expect(component.isLoading).toBeTrue();
		tick(100);
		expect(authServiceMock.signIn).toHaveBeenCalled();
		expect(authServiceMock.setJwtToken).not.toHaveBeenCalledOnceWith('token');
		expect(component.isLoading).toBeFalse();
	}));

});
