import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth-service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthResponse, UserRegistration, UserSignIn } from '../interface/user.interface';
import { environment } from '../../environment/environment';
import { provideHttpClient } from '@angular/common/http';

describe('AuthService', () => {
	let service: AuthService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
	  		imports: [],
	  		providers: [AuthService, provideHttpClient(), provideHttpClientTesting()]
		});
		service = TestBed.inject(AuthService);
		httpTestingController = TestBed.inject(HttpTestingController);
 	});

  	afterEach(() => {
		httpTestingController.verify();
  	});

  	describe('signUp', () => {
		  it('should send a post and return a successful response', () => {
			const testRegisterRequest: UserRegistration = {
				email: 'test@example.com',
				password: 'TestPassword123!',
				firstName: 'Test',
				lastName: 'User',
				phoneNumber: '1234567890',
				dateOfBirth: '1990-01-01',
			};
			
			const mockAuthResponse: AuthResponse = {
				message: 'Registration successful! Please verify your email.',
				token: null,
			};
			
			service.signUp(testRegisterRequest).subscribe({
				next: response => expect(response).toEqual(mockAuthResponse)
		 	});
			
			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/register`);
			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(testRegisterRequest);
			req.flush(mockAuthResponse);
		});
		
		it('should send a post and return a fail due to duplicate email response', () => {
			const testRegisterRequest: UserRegistration = {
				email: 'test@example.com',
				password: 'TestPassword123!',
				firstName: 'Test',
				lastName: 'User',
				phoneNumber: '1234567890',
				dateOfBirth: '1990-01-01'
			};

			const mockAuthResponse: AuthResponse = {
				message: 'Email already registered: test@example.com',
				token: null
			};

			service.signUp(testRegisterRequest).subscribe({
				next: () => fail('should fail with duplicate email error'),
				error: error => {
					expect(error.status).toEqual(400);
					expect(error.error).toEqual(mockAuthResponse);
				}
			});
		
			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/register`);
			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(testRegisterRequest);
			req.flush(mockAuthResponse, {status: 400, statusText: 'Bad Request'});
		});

		it('should send a post and fail with internal server error', () => {
			const testRegisterRequest: UserRegistration = {
				email: 'test@example.com',
				password: 'TestPassword123!',
				firstName: 'Test',
				lastName: 'User',
				phoneNumber: '1234567890',
				dateOfBirth: '1990-01-01'
			};

			const mockServerErrorResponse = {
				timestamp: new Date().toISOString(),
				status: 500,
				error: 'Internal Server Error',
				message: 'An unexpected error occurred on the server.',
				path: '/api/auth/register'
			};

			service.signUp(testRegisterRequest).subscribe({
				next: () => fail('should fail with internal server error'),
				error: error => {
					expect(error.status).toEqual(500);
					expect(error.error).toEqual(mockServerErrorResponse);
				}
			});
		
			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/register`);
			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(testRegisterRequest);
			req.flush(mockServerErrorResponse, {status: 500, statusText: 'Bad Internal Server Error'});
		});
	});

	describe('signIn', () => {

		let signInDetails: UserSignIn;
		let expectedResponse: AuthResponse;

		beforeEach(() =>{
			signInDetails = {
				email: 'user@example.com',
				password: 'Password1',
			};
		});

		it('sign in is successful', () => {

			expectedResponse = {
				message: 'sign in succesful',
				token: 'Some Token',
			};

			service.signIn(signInDetails).subscribe((response => {
				expect(response).toEqual(expectedResponse);
			}));

			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/sign-in`);
			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(signInDetails);
			req.flush(expectedResponse);
		});
		it('should fail with incorrect email and password', () => {
			expectedResponse = {
				message: 'sign in failed due to username or password',
				token: null,
			};

			service.signIn(signInDetails).subscribe({
				next: () => fail('Should fail with incorrect email or password'),
				error: error => {
					expect(error.status).toEqual(400);
					expect(error.error.message).toEqual(expectedResponse.message);
				}
			});

			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/sign-in`);

			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(signInDetails);

			req.flush(expectedResponse, {status: 400, statusText: 'Bad Request'});
		});
		it('should fail with internal server error', () => {
			const mockServerErrorResponse = {
				timestamp: new Date().toISOString(),
				status: 500,
				error: 'Internal Server Error',
				message: 'An unexpected error occurred on the server.',
				path: '/api/auth/sign-in',
			};

			service.signIn(signInDetails).subscribe({
				next: () => fail('should fail with internal server error'),
				error: error => {
					expect(error.status).toEqual(500);
					expect(error.error).toEqual(mockServerErrorResponse);
				}
			});

			const req = httpTestingController.expectOne(`${environment.apiUrl}/auth/sign-in`);

			expect(req.request.method).toEqual('POST');
			expect(req.request.body).toEqual(signInDetails);
			req.flush(mockServerErrorResponse, {status: 500, statusText: 'Internal Server Error'});
		});

	});

	describe('jwtToken', () => {
		const testToken = 'mock.token';
		const tokenKey = 'jwtToken';

		it('should set JwtToken in session storage', () => {
			spyOn(sessionStorage, 'setItem');
			service.setJwtToken(testToken);
			expect(sessionStorage.setItem).toHaveBeenCalledOnceWith(tokenKey, testToken);
		});

		it('should get JwtToken from session storage', () => {
			spyOn(sessionStorage, 'getItem').and.returnValue(testToken);
			const tokenVal = service.getJwtToken();
			expect(sessionStorage.getItem).toHaveBeenCalledOnceWith(tokenKey);
			expect(tokenVal).toEqual(testToken);
		});

		it('should return null from session storage if no token', () => {
			spyOn(sessionStorage, 'getItem').and.returnValue(null);
			const tokenVal = service.getJwtToken();
			expect(sessionStorage.getItem).toHaveBeenCalledOnceWith(tokenKey);
			expect(tokenVal).toEqual(null);
		});
	});
});
