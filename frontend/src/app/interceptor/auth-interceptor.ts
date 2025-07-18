import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../service/auth-service';
import { inject } from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
	const authService = inject(AuthService);
	const token = authService.getJwtToken();

	if(token && !req.url.includes('/auth/login') && !req.url.includes('/auth/register')) {
		req = req.clone({
			setHeaders: {
				Authorization: `Bearer ${token}`
			}
		});
  	}
  return next(req);
};
