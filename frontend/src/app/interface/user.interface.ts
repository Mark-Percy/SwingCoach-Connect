export interface User {
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    dateOfBirth: string;
}

export interface UserRegistration extends User {
    password: string;
}

export interface AuthResponse {
    message: string;
    token: string | null;
}

export interface UserSignIn {
    email: string;
    password: string;
}

