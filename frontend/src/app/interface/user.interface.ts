export interface User {
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    dateOfBirth: string;
}

export interface UserRegistration extends User {
    password: string;
    confirmPassord: string;
}

