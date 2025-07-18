import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpInterceptorFn, provideHttpClient, withInterceptors } from '@angular/common/http';

import { authInterceptor } from './auth-interceptor';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../service/auth-service';

describe('authInterceptor', () => {
	let http: HttpClient;
	let httpTestingController: HttpTestingController;
	let authService: AuthService;

	beforeEach(() => {
		TestBed.configureTestingModule({
		providers: [
				provideHttpClient(withInterceptors([authInterceptor])),
				provideHttpClientTesting(),
				AuthService
			]
		});

		http = TestBed.inject(HttpClient);
		httpTestingController = TestBed.inject(HttpTestingController);
		authService = TestBed.inject(AuthService);
  	});

	afterEach(() => {
		httpTestingController.verify();
	});

	it('should add Authorization header if token exists', () => {
		const dummyToken = 'mock-jwt-token';

		spyOn(authService, 'getJwtToken').and.returnValue(dummyToken);


		http.get('/api/protected').subscribe();

		const req = httpTestingController.expectOne('/api/protected');
		expect(req.request.headers.has('Authorization')).toBeTrue();
		expect(req.request.headers.get('Authorization')).toEqual(`Bearer ${dummyToken}`);
		req.flush({}); // Complete the request
	});

	it('should NOT add Authorization header if no token', () => {

		spyOn(authService, 'getJwtToken').and.returnValue(null);

		http.get('/api/protected').subscribe();

		const req = httpTestingController.expectOne('/api/protected');
		expect(req.request.headers.has('Authorization')).toBeFalse();
		req.flush({});
	});

	it('should NOT add Authorization header for login endpoint', () => {
		const dummyToken = 'mock-jwt-token';
		spyOn(authService, 'getJwtToken').and.returnValue(dummyToken);

		http.post('/api/auth/login', {}).subscribe();

		const req = httpTestingController.expectOne('/api/auth/login');
		expect(req.request.headers.has('Authorization')).toBeFalse();
		req.flush({});
	});

	it('should NOT add Authorization header for register endpoint', () => {
		const dummyToken = 'mock-jwt-token';
		spyOn(authService, 'getJwtToken').and.returnValue(dummyToken);

		http.post('/api/auth/register', {}).subscribe();

		const req = httpTestingController.expectOne('/api/auth/register');
		expect(req.request.headers.has('Authorization')).toBeFalse();
		req.flush({});
	});
});
