# SingCoach Connect
A Comprehensive Golf Lesson Booking System
- GolfLessonBooker is a full-stack web application designed to streamline the process of booking golf lessons for students, managing schedules for coaches and staff, and providing comprehensive administration tools for golf clubs and system administrators.

- Built with modern technologies and a focus on user experience, security, and scalability, this system aims to be the go-to platform for golf instruction management.

## 1. Project Overview
### GolfLessonBooker provides a robust platform for:

- Students: Discovering nearby golf clubs, browsing available lessons (individual & group), requesting bookings, managing their personal schedule, and tracking their progress/level per club.
- Coaches & Staff: Managing their personal availability, creating and updating lesson offerings, booking lessons for students (including guests), signing off on completed lessons, and assessing student levels.
- Club Administrators: Full management of their golf club's profile, bays, operating hours, lesson policies, and staff/teacher/student memberships within their club. They can also process requests from users to join their club.
- System Administrators: Global oversight of the platform, including approving new club creations, managing email templates, setting system-wide policies, and handling user appeals or club flags.

## 2. Key Features
### Frontend (Angular)
- Intuitive Landing Page: Location-based club search, featured clubs, system overview, and clear calls to action for login/registration.
- Secure Authentication: User-friendly Sign In/Register forms with real-time validation, password reset, and email verification.
- Role-Based Access: Differentiated user journeys for Students, Coaches, Staff, Club Admins, and System Admins.
- Dynamic Lesson Diary: Daily, weekly, and monthly views with color-coded slots for individual and group lessons, showing availability and status. Supports "double-stack" lessons.
- Flexible Booking Forms:
- Students can request individual or book group lessons, with club-defined criteria (level, age) enforcement.
- Coaches/Staff can book lessons for registered students or temporary guests.
- Comprehensive Club Management:
- Club creation forms (including bay setup: total bays, general lesson bays, custom bay names).
- Ability to manage club details, operating hours (including seasonal), policies (cancellation, age groups).
- Staff/Teacher management (inviting, role assignment, removal).
- Processing of club join requests.
- Personal Account Settings: Users can manage profile, password, notification preferences, and view their multi-club memberships with club-specific level ratings and standing (e.g., suspended/banned status with appeal process).
- System Admin Dashboard: Global view of clubs, users, pending approvals, appeals, and system-wide settings.

### Backend (Java/Spring Boot)
- RESTful API: Clean, well-defined endpoints for all frontend functionalities.
- Robust Security: JWT-based authentication and Spring Security for granular role-based authorization.
- Business Logic: Handles complex operations like lesson scheduling, bay allocation (general vs. specific), student eligibility checks, and club/user status management.
- Email Notifications: Automated emails for registration verification, password resets, booking confirmations, cancellations, and administrative alerts (e.g., new club requests).
- Data Persistence: Manages all data interactions with the MySQL database.

## 3. Technology Stack
- Frontend: Angular (TypeScript)
- Backend: Java 17+, Spring Boot
- Database: MySQL
- Containerization: Docker, Docker Compose
- Build Tools: Maven (for Spring Boot), npm/Yarn (for Angular)
- Version Control: Git / GitHub

## 4. Architecture
The application follows a mono-repository structure, containing both the Angular frontend and the Spring Boot backend within a single Git repository. This approach simplifies development, testing, and deployment by keeping related codebases together.
The backend is built as a layered monolithic application, separating concerns into Controller, Service, Repository, and Entity layers.

## 5. Getting Started
Follow these steps to get the GolfLessonBooker application up and running on your local machine.

### Prerequisites
- Git
- Docker Desktop (includes Docker Compose)
- Node.js & npm (for Angular CLI)
- Angular CLI (npm install -g @angular/cli)
- Java Development Kit (JDK) 17+
- Maven

### Local Setup
- Clone the Repository:
```
git clone https://github.com/your-username/golf-booking-app.git # Replace with your actual repo URL
```
```
cd golf-booking-app
```

- Database Setup (MySQL via Docker Compose):
  
  - Create a .env file in the docker/ directory:
```  
  MYSQL_ROOT_PASSWORD=your_mysql_root_password
  MYSQL_DATABASE=golf_booking_app
  MYSQL_USER=golf_user
  MYSQL_PASSWORD=golf_password
```

  - Start the MySQL database container:

```
  cd docker
  docker-compose up -d mysql
  cd ..
```

  - Verify MySQL is running: docker-compose ps (should show mysql container as Up).

- Backend Setup (Spring Boot):

  - Navigate to the backend directory:
  ```
  cd backend
  ```

  - Configure src/main/resources/application-dev.properties (or application.properties) for database and email:
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/golf_booking_app?useSSL=false&serverTimezone=UTC
  spring.datasource.username=golf_user
  spring.datasource.password=golf_password
  spring.jpa.hibernate.ddl-auto=update # For development, creates/updates schema
  spring.jpa.show-sql=true
  ```
  ```
  # Mailtrap/MailHog configuration for development emails
  spring.mail.host=smtp.mailtrap.io # or localhost for MailHog
  spring.mail.port=2525 # or 1025 for MailHog
  spring.mail.username=your_mailtrap_username # or leave empty for MailHog
  spring.mail.password=your_mailtrap_password # or leave empty for MailHog
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  ```
  - Build the backend:
  ```
  mvn clean install
  ```
  -Run the backend application:
  ```
  mvn  spring-boot:run
  ```
  - The backend should start on http://localhost:8080.

- Frontend Setup (Angular):
  - Navigate to the frontend directory:
  ```
  cd frontend
  ```
  - Install Angular dependencies:
  ```
  npm install
  ```
  - Configure proxy.conf.json (in frontend/ root) to proxy API calls to the backend:
  ```
  {
    "/api": {
      "target": "http://localhost:8080",
      "secure": false,
      "changeOrigin": true
    }
  }
  ```
  - Start the Angular development server:
  ```
  ng serve --proxy-config proxy.conf.json
  ```
  - The frontend should now be accessible at http://localhost:4200.

## 6. Development & Testing
- This project adheres to a Test-Driven Development (TDD) methodology, focusing on building features in vertical slices (implementing functionality across frontend, backend, and database for each small feature).
## Backend Testing:
-Unit Tests: For individual services and components (using JUnit, Mockito).
- Integration Tests: For API endpoints and database interactions (using Spring Boot Test).
- Email Testing: JavaMailSender is mocked in automated tests. For manual/integration testing, configure Spring Boot to use a Mail Trap service (e.g., Mailtrap.io or MailHog via Docker) to capture and inspect emails without sending them to real inboxes.
## Frontend Testing:
- Unit Tests: For Angular components, services, and pipes (using Karma, Jasmine).
- End-to-End Tests: (Future consideration) For full user journeys (e.g., using Cypress or Protractor).

## 7. License
- This project is licensed under the GNU General Public License v3.0 (GPLv3).
- This means you are free to use, modify, and distribute this software.
- See the LICENSE.md file in the root of this repository for full details.

## 8. Contributing
Contributions are welcome! If you'd like to contribute, please follow these steps:
- Fork the repository.
- Create a new branch (git checkout -b feature/your-feature-name).
- Implement your feature or bug fix, following the TDD approach.
- Ensure all tests pass.
- Commit your changes (git commit -m 'feat: Add new feature X').
- Push to your branch (git push origin feature/your-feature-name).
- Create a Pull Request, describing your changes in detail.

## 9. Contact & Support
For any questions, issues, or support, please open an issue on the GitHub repository.
