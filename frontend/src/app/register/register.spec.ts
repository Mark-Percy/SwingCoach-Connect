import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { Register } from './register';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../service/auth-service';
import { By } from '@angular/platform-browser';
import { delay, of, throwError } from 'rxjs';

describe('Register', () => {
	let component: Register;
	let fixture: ComponentFixture<Register>;
	let authServiceMock: any;

	beforeEach(async () => {

		authServiceMock = jasmine.createSpyObj('AuthService', ['signUp']);

		await TestBed.configureTestingModule({
			imports: [ReactiveFormsModule],
			providers: [
				FormBuilder,
				{ provide: AuthService, useValue: authServiceMock }
			]
		})
		.compileComponents();

		fixture = TestBed.createComponent(Register);
		component = fixture.componentInstance;
		fixture.detectChanges();

		authServiceMock.signUp.and.returnValue(of({ message: 'Success from mock' }));
	});

	it('form starts empty', () => {
		const email = fixture.debugElement.query(By.css('[name="email"]')).nativeElement;
		const password = fixture.debugElement.query(By.css('[name="password"]')).nativeElement;
		const confirmPassword = fixture.debugElement.query(By.css('[name="confirmPassword"]')).nativeElement;
		const phoneNumber = fixture.debugElement.query(By.css('[name="phoneNumber"]')).nativeElement;
		const dateOfBirth = fixture.debugElement.query(By.css('[name="dateOfBirth"]')).nativeElement;
		const firstName = fixture.debugElement.query(By.css('[name="firstName"]')).nativeElement;
		const lastName = fixture.debugElement.query(By.css('[name="lastName"]')).nativeElement;
		expect(component).toBeTruthy();
		expect(email.value).toEqual('');
		expect(password.value).toEqual('');
		expect(confirmPassword.value).toEqual('');
		expect(phoneNumber.value).toEqual('');
		expect(dateOfBirth.value).toEqual('');
		expect(firstName.value).toEqual('');
		expect(lastName.value).toEqual('');

		expect(component.registerForm.valid).toBeFalse();
		expect(component.registerForm.get('email')?.valid).toBeFalse();
		expect(component.registerForm.get('password')?.valid).toBeFalse();
		expect(component.registerForm.get('confirmPassword')?.valid).toBeFalse()
		expect(component.registerForm.get('phoneNumber')?.value).toEqual('');
		expect(component.registerForm.get('dateOfBirth')?.value).toEqual('');
		expect(component.registerForm.get('firstName')?.valid).toBeFalse();
		expect(component.registerForm.get('lastName')?.valid).toBeFalse();

		const submitButton = fixture.debugElement.query(By.css('button')).nativeElement;
		expect(submitButton.disabled).toBeFalse();
	});

	it('form can be filled and submitted',fakeAsync(() => {
		const submitButton = fixture.debugElement.query(By.css('button')).nativeElement;

		expect(submitButton.disabled).toBeFalse();
		component.registerForm.patchValue({
			firstName: 'Test',
			lastName: 'User',
			email: 'test@test.com',
			password: 'StrongPassword2213',
			confirmPassword: 'StrongPassword2213',
			dateOfBirth: '1998-09-01',
			phoneNumber: '21321443233',
		});

		fixture.detectChanges();
		expect(submitButton.disabled).toBeFalse();

		expect(component.registerForm.valid).toBeTrue();
		expect(component.registerForm.get('email')?.valid).toBeTrue();
		expect(component.registerForm.get('password')?.valid).toBeTrue();
		expect(component.registerForm.get('confirmPassword')?.valid).toBeTrue()
		expect(component.registerForm.get('phoneNumber')?.value).toEqual('21321443233');
		expect(component.registerForm.get('dateOfBirth')?.value).toEqual('1998-09-01');
		expect(component.registerForm.get('firstName')?.valid).toBeTrue();
		expect(component.registerForm.get('lastName')?.valid).toBeTrue();;

		const formElement: HTMLFormElement = fixture.debugElement.query(By.css('form')).nativeElement;
		formElement.dispatchEvent(new Event('submit'));
		fixture.detectChanges();

		tick();

		expect(submitButton.disabled).toBeTrue();

		const {confirmPassword, ...expectedPayload} = {...component.registerForm.value};
		expect(authServiceMock.signUp).toHaveBeenCalledOnceWith(expectedPayload);
	}));

	it('form can be filled invalid and submit fails',fakeAsync(() => {
		component.registerForm.patchValue({
			firstName: 'Test',
			lastName: 'User',
			email: 'test@test.com',
			password: 'StrongPassword2213',
			confirmPassword: 'StrongPassord2213',
			dateOfBirth: '1998-09-01',
			phoneNumber: '21321443233',
		});

		fixture.detectChanges();

		expect(component.registerForm.invalid).toBeTrue();
		expect(component.registerForm.get('email')?.valid).toBeTrue();
		expect(component.registerForm.get('password')?.valid).toBeTrue();
		expect(component.registerForm.get('confirmPassword')?.invalid).toBeTrue()
		expect(component.registerForm.get('phoneNumber')?.value).toEqual('21321443233');
		expect(component.registerForm.get('dateOfBirth')?.value).toEqual('1998-09-01');
		expect(component.registerForm.get('firstName')?.valid).toBeTrue();
		expect(component.registerForm.get('lastName')?.valid).toBeTrue();;

		const formElement: HTMLFormElement = fixture.debugElement.query(By.css('form')).nativeElement;
		formElement.dispatchEvent(new Event('submit'));
		fixture.detectChanges();

		const submitButton = fixture.debugElement.query(By.css('button')).nativeElement;
		expect(submitButton.disabled).toBeFalse();

		const {confirmPassword, ...expectedPayload} = {...component.registerForm.value};
		expect(authServiceMock.signUp).not.toHaveBeenCalled();
	}));

	it('should disable submit button while submitting', fakeAsync(() => {
		component.registerForm.patchValue({
			firstName: 'Test', lastName: 'User', email: 'loading@test.com',
			password: 'StrongPassword123!', confirmPassword: 'StrongPassword123!'
		});
		fixture.detectChanges();
		expect(component.registerForm.valid).toBeTrue();

		authServiceMock.signUp.and.returnValue(of({ message: 'Success' }).pipe(delay(100)));

		const submitButton: HTMLButtonElement = fixture.debugElement.query(By.css('button[type="submit"]')).nativeElement;

		submitButton.click();
		fixture.detectChanges();

		expect(submitButton.disabled).toBeTrue();

		tick(50);
		fixture.detectChanges();
		expect(submitButton.disabled).toBeTrue();

		tick(50);
		fixture.detectChanges();

		expect(authServiceMock.signUp).toHaveBeenCalledTimes(1);
	}));

	it('should handle submission error from authService', fakeAsync(() => {
		component.registerForm.patchValue({
			firstName: 'Test', lastName: 'User', email: 'error@test.com',
			password: 'StrongPassword123!', confirmPassword: 'StrongPassword123!'
		});
		fixture.detectChanges();
		expect(component.registerForm.valid).toBeTrue();

		const backendErrorMessage = 'Email already registered from backend.';
		authServiceMock.signUp.and.returnValue(throwError(() => ({ error: { message: backendErrorMessage } })));

		const formElement: HTMLFormElement = fixture.debugElement.query(By.css('form')).nativeElement;
		formElement.dispatchEvent(new Event('submit'));
		fixture.detectChanges();

		tick();

		expect(authServiceMock.signUp).toHaveBeenCalledTimes(1);
	}));
});
