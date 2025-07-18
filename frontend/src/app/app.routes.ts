import { Routes } from '@angular/router';
import { Register } from '../app/register/register'
import { SignIn } from './sign-in/sign-in';

export const routes: Routes = [
    {component: Register, path:'register'},
    {component: SignIn, path:'sign-in'}
];
