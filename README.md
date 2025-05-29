# Rwanda Government ERP System

Enterprise Resource Planning (ERP) system for the Government of Rwanda to manage employee information and payroll processing.

## Features

- Employee Management
- Employment Management
- Deduction Management
- Payroll Processing
- Payslip Generation and Approval
- Email Notifications

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Installation

1. Clone the repository
2. Configure the database connection in `src/main/resources/application.properties`
3. Run the application using Maven:

```bash
mvn spring:boot run
```

The application will start and display the URLs for accessing the API and Swagger UI.

## API Documentation

The API documentation is available through Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Default Users

The system is initialized with the following default users:

1. **Admin User**
   - Email: admin@gov.rw
   - Password: admin123
   - Roles: ADMIN, MANAGER, EMPLOYEE

2. **Manager User**
   - Email: manager@gov.rw
   - Password: manager123
   - Roles: MANAGER, EMPLOYEE

3. **Employee User**
   - Email: employee@gov.rw
   - Password: employee123
   - Roles: EMPLOYEE

## Testing the APIs

### Authentication

1. **Login**
   - Endpoint: `POST /api/auth/login`
   - Request Body:
     ```json
     {
       "email": "admin@gov.rw",
       "password": "admin123"
     }
     ```
   - Response: JWT token to be used for subsequent requests

2. **Register a New Employee**
   - Endpoint: `POST /api/auth/register`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body:
     ```json
     {
       "firstName": "John",
       "lastName": "Doe",
       "email": "john.doe@gov.rw",
       "password": "password123",
       "mobile": "+250700000003",
       "dateOfBirth": "1990-01-01",
       "roles": ["ROLE_EMPLOYEE"],
       "department": "IT",
       "position": "Developer",
       "baseSalary": 75000,
       "joiningDate": "2023-01-01"
     }
     ```

### Employee Management

1. **Get All Employees**
   - Endpoint: `GET /api/employees`
   - Authorization: Bearer Token (Admin or Manager)

2. **Get Employee by ID**
   - Endpoint: `GET /api/employees/{id}`
   - Authorization: Bearer Token (Admin or Manager)

3. **Update Employee**
   - Endpoint: `PUT /api/employees/{id}`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body: Similar to registration but with updated fields

4. **Delete Employee**
   - Endpoint: `DELETE /api/employees/{id}`
   - Authorization: Bearer Token (Admin or Manager)

### Employment Management

1. **Get All Employments**
   - Endpoint: `GET /api/employments`
   - Authorization: Bearer Token (Admin or Manager)

2. **Get Employment by ID**
   - Endpoint: `GET /api/employments/{id}`
   - Authorization: Bearer Token (Admin or Manager)

3. **Create New Employment**
   - Endpoint: `POST /api/employments`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body:
     ```json
     {
       "employee": {
         "id": 1
       },
       "department": "Finance",
       "position": "Accountant",
       "baseSalary": 70000,
       "status": "ACTIVE",
       "joiningDate": "2023-01-01"
     }
     ```

4. **Update Employment**
   - Endpoint: `PUT /api/employments/{id}`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body: Similar to creation but with updated fields

### Deduction Management

1. **Get All Deductions**
   - Endpoint: `GET /api/deductions`
   - Authorization: Bearer Token (Admin or Manager)

2. **Get Deduction by ID**
   - Endpoint: `GET /api/deductions/{id}`
   - Authorization: Bearer Token (Admin or Manager)

3. **Create New Deduction**
   - Endpoint: `POST /api/deductions`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body:
     ```json
     {
       "deductionName": "Special Tax",
       "percentage": 2.5,
       "active": true
     }
     ```

4. **Update Deduction**
   - Endpoint: `PUT /api/deductions/{id}`
   - Authorization: Bearer Token (Admin or Manager)
   - Request Body: Similar to creation but with updated fields

### Payroll Processing

1. **Process Payslips for a Month**
   - Endpoint: `POST /api/payslips/process`
   - Authorization: Bearer Token (Manager)
   - Request Body:
     ```json
     {
       "month": 1,
       "year": 2025
     }
     ```

2. **Approve Payslips for a Month**
   - Endpoint: `POST /api/payslips/approve`
   - Authorization: Bearer Token (Admin)
   - Request Body:
     ```json
     {
       "month": 1,
       "year": 2025
     }
     ```
   - Note: You can also approve a specific employee's payslip by adding `"employeeId": 1` to the request body

3. **Get Payslips for a Month**
   - Endpoint: `GET /api/payslips/month/{month}/year/{year}`
   - Authorization: Bearer Token (Admin or Manager)

4. **Get Employee's Payslips**
   - Endpoint: `GET /api/payslips/employee/{employeeId}`
   - Authorization: Bearer Token (Admin, Manager, or the Employee themselves)

## Testing Flow

1. Login as Manager (manager@gov.rw)
2. Process payslips for a specific month and year
3. Login as Admin (admin@gov.rw)
4. Approve the processed payslips
5. Login as Employee (employee@gov.rw)
6. View your payslip for the processed month

## Notes

- Email notifications are sent when payslips are approved
- The system prevents duplicate payroll generation for the same employee in the same month/year
- Deductions are calculated based on the base salary according to the specified percentages# then
# then
