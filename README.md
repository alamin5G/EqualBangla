# Equal Bangladesh

A Spring Boot application for collecting, documenting, and preserving information about victims of state-led violence during the July-August 2024 Movement in Bangladesh.

## Project Overview

Equal Bangladesh is a platform that helps citizens share the stories of those affected by oppression. The application collects data about injured, missing, and deceased individuals to create a detailed historical record of the events.

## Features

- **User Authentication**: JWT-based authentication for admin users
- **Form Submission**: Three different forms for reporting victims (Death, Missing, Injured)
- **Email Verification**: OTP verification system for form submissions
- **Admin Dashboard**: Comprehensive admin panel for data management
- **Reporting**: Generate statistical reports, charts and export data in multiple formats (CSV, Excel, PDF)
- **Case Management**: Assign cases to admins, track status and create tasks
- **Database Backup**: Tools for database backup and restoration
- **Audit Logging**: Track all system activities and changes

## Technology Stack

- Java 17+
- Spring Boot
- Spring Security
- Thymeleaf
- MySQL Database
- JFreeChart (for report generation)
- Maven

## Project Structure

- **Controllers**: Handle HTTP requests and route them to appropriate services
- **Models**: Define data structures used throughout the application
- **Services**: Contain business logic and processes
- **Repositories**: Handle database operations
- **Configuration**: Configure application settings and beans
- **Security**: Manage authentication and authorization

## Setup Instructions

### Prerequisites

- JDK 17 or higher
- Maven
- MySQL 8.0

### Database Setup

```bash
# Run MySQL with Docker
docker run --detach --env MYSQL_ROOT_PASSWORD=252646 --env MYSQL_USER=root --env MYSQL_PASSWORD=252646 --env MYSQL_DATABASE=equal_bangladesh --name mysql --publish 3306:3306 mysql:8-oracle
```

### Building the Application

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

## Initial Admin Setup

The application automatically creates an initial admin user on startup:
- Username: alamin
- Password: alaminEqualBangladesh
- Email: alaminvai5g@gmail.com

## Key Features Explained

### User Flow

1. Users verify their email through OTP verification
2. They select the appropriate form (Death, Missing, or Injured)
3. After form submission, admins review and verify the information

### Admin Capabilities

- View and manage all submitted cases
- Generate statistical reports and visualizations
- Export data in various formats
- Manage other admin users (with proper authorization)
- Track system activities through audit logs
- Backup and restore database

### Security Features

- JWT-based authentication
- IP filtering for admin requests
- Role-based access control
- HTTP-only cookies for token storage

## File Structure

The project follows a standard Spring Boot architecture with controllers, services, and repositories organized by functionality.

## Contributing

Please read the contribution guidelines before submitting pull requests.

## License

[License information]