# WorkWise - HR Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![H2 Database](https://img.shields.io/badge/H2-Database-yellow.svg)](https://www.h2database.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-blue.svg)](https://www.thymeleaf.org/)
[![Maven](https://img.shields.io/badge/Maven-Build%20Tool-red.svg)](https://maven.apache.org/)

## 📋 Overview

WorkWise is a comprehensive Human Resources Management System built with Spring Boot. It provides a complete solution for managing employees, departments, user accounts, and salary calculations with role-based access control.

## ✨ Features

### 🔐 Authentication & Authorization
- **Multi-role System**: ADMIN, HR, and EMPLOYEE roles
- **Session-based Authentication**: Secure login/logout functionality
- **Role-based Access Control**: Different features available based on user role
- **Password Management**: Temporary password system with reset functionality

### 👥 Employee Management
- **Employee Profiles**: Complete employee information including personal details, contact info, and professional data
- **Department Assignment**: Organize employees by departments
- **Profile Management**: Employees can view and edit their own profiles
- **Professional Details**: LinkedIn profiles, bio, hire dates, and more

### 🏢 Department Management
- **Department Overview**: View all departments with employee counts
- **Employee Distribution**: See which employees belong to each department
- **Department Operations**: Add, edit, and manage departments

### 💰 Financial Management
- **Salary Calculations**: Automated salary processing with tax calculations
- **Tax Management**: Configurable tax rates and calculations
- **Salary History**: Track salary changes over time
- **Financial Reports**: Generate reports for HR and management

### 👤 User Management (Admin)
- **User Creation**: Create new user accounts with role assignment
- **Account Management**: Enable/disable accounts, reset passwords
- **Role Management**: Change user roles and permissions
- **Employee Linking**: Connect user accounts to employee profiles

### 📊 Dashboard & Analytics
- **Admin Dashboard**: Overview of all employees and system statistics
- **HR Dashboard**: Department-specific views and employee management
- **Employee Dashboard**: Personal information and salary details

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.3
- **Database**: H2 In-Memory Database
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 17
- **Security**: Session-based authentication
- **UI**: Bootstrap (responsive design)

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Galustjans-Artjoms/XProject1.git
   cd XProject1
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - Main application: http://localhost:8083
   - H2 Database Console: http://localhost:8083/h2-console
     - JDBC URL: `jdbc:h2:mem:workwisedb`
     - Username: `sa`
     - Password: (leave empty)

### Default Login Credentials

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin` | ADMIN | Full system access |
| `john.smith` | `password` | EMPLOYEE | Employee access |
| `sarah.johnson` | `password` | HR | HR management access |

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/ludusimperius/workwise/
│   │   ├── controllers/          # REST controllers
│   │   ├── models/              # Entity classes
│   │   ├── repositories/        # Data access layer
│   │   └── WorkWiseApplication.java
│   └── resources/
│       ├── templates/           # Thymeleaf templates
│       ├── static/              # CSS, JS, images
│       ├── application.properties
│       └── data.sql            # Initial data
└── test/                       # Unit tests
```

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. Key configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:workwisedb
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

### Server Configuration
- **Port**: 8083
- **Context Path**: /
- **Session Timeout**: Default Spring Boot settings

## 🎯 Key Features Explained

### Role-Based Access Control
- **ADMIN**: Full system access, user management, employee management
- **HR**: Employee management, department views, salary calculations
- **EMPLOYEE**: Personal profile management, salary viewing

### Employee Management
- Complete employee profiles with personal and professional information
- Department assignment and management
- Salary and tax calculations
- Profile editing capabilities

### Financial System
- Automated salary calculations
- Tax rate management
- Salary history tracking
- Financial reporting

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

## 📝 API Endpoints

### Authentication
- `GET /login` - Login page
- `POST /login` - Authenticate user
- `GET /logout` - Logout user

### Admin Endpoints
- `GET /admin` - Admin dashboard
- `GET /admin/users` - User management
- `GET /admin/employees` - Employee management
- `GET /admin/departments` - Department management

### Employee Endpoints
- `GET /profile` - Employee profile
- `GET /profile/edit` - Edit profile
- `GET /salary` - Salary information

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Artjoms Galustjans**
- GitHub: [@Galustjans-Artjoms](https://github.com/Galustjans-Artjoms)
- LinkedIn: [Artjoms Galustjans](https://linkedin.com/in/artjoms-galustjans)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- H2 Database for the lightweight database solution
- Bootstrap for the responsive UI components
- All contributors and testers

## 📞 Support

If you have any questions or need support, please:
- Open an issue on GitHub
- Contact the author directly
- Check the documentation

---

**WorkWise** - Making HR management simple and efficient! 🚀 