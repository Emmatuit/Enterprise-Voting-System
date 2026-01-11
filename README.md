# 🏛️ Enterprise Voting System

> **Policy-Driven, Multi-Tenant Voting Platform**

## 🚀 Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 14+
- Maven 3.6+

### Setup
```bash
# Clone and run
git clone <repo-url>
cd enterprise-voting-system

# Configure database
createdb voting_db
createuser voting_admin
psql voting_db -c "GRANT ALL PRIVILEGES ON DATABASE voting_db TO voting_admin;"

# Update application.yml with your database credentials

# Build and run
mvn clean package
java -jar target/voting-system-1.0.0.jar

# Access Swagger UI: http://localhost:8080/api/swagger-ui.html
```

## 📋 Features

✅ **Multi-Tenant** - Each organization operates independently  
✅ **Policy-Driven** - Custom validation rules per organization  
✅ **Secure** - JWT authentication, OTP verification, rate limiting  
✅ **Auditable** - Complete audit trail for all actions  
✅ **Scalable** - Supports 10k+ voter records  

## 🏗️ Architecture

```
Organization → Identity Policy → Voter Registry → Election → Candidates → Votes
```

### Core Concepts:
- **Organization**: Voting entity (University, Company, NGO)
- **Identity Policy**: Defines how voters are validated (matric/email/phone + OTP)
- **Voter Registry**: List of eligible voters
- **Election**: Voting event with candidates

## 🔐 API Workflow

### 1. Admin Setup
```bash
# 1. Register admin
POST /api/auth/admin/register
{"username":"admin","password":"pass","email":"admin@org.edu"}

# 2. Login
POST /api/auth/admin/login
{"username":"admin","password":"pass"}
# Returns JWT token
```

### 2. Organization Setup
```bash
# 3. Create organization
POST /api/admin/organizations
{"name":"University of Tech","contactEmail":"elections@utech.edu"}

# 4. Set identity policy
POST /api/admin/identity-policy
{
  "organizationId":1,
  "identifierFields":["matric_number","email"],
  "otpChannel":"EMAIL"
}
```

### 3. Voter Management
```bash
# 5. Upload voters CSV
POST /api/admin/voter-registry/upload
file=voters.csv, organizationId=1

# CSV format:
matric_number,email,phone,full_name
STU001,john@uni.edu,,John Doe
STU002,jane@uni.edu,,Jane Smith
```

### 4. Election Setup
```bash
# 6. Create election
POST /api/admin/elections
{
  "organizationId":1,
  "title":"SUG Election",
  "startTime":"2024-03-01T08:00:00",
  "endTime":"2024-03-01T18:00:00"
}

# 7. Add candidates
POST /api/admin/elections/1/candidates
{"name":"John Doe","position":"President"}

# 8. Activate election
POST /api/admin/elections/1/activate
```

### 5. Voter Verification & Voting
```bash
# 9. Voter verification (Public)
POST /api/voters/verify
{"organizationId":1,"matricNumber":"STU001","email":"john@uni.edu"}
# Sends OTP to email

# 10. Confirm OTP
POST /api/voters/confirm-otp
{"identifier":"john@uni.edu","otpCode":"123456"}

# 11. Cast vote
POST /api/votes
{"electionId":1,"candidateId":1,"voterRegistryId":1}
```

## 📊 Key APIs

| Category | Base URL | Key Endpoints |
|----------|----------|---------------|
| **Authentication** | `/api/auth` | `/login`, `/register`, `/logout` |
| **Organization** | `/api/admin/organizations` | CRUD operations |
| **Identity Policy** | `/api/admin/identity-policy` | Configure validation rules |
| **Voter Registry** | `/api/admin/voter-registry` | CSV upload, voter management |
| **Election** | `/api/admin/elections` | Create, manage elections |
| **Candidates** | `/api/admin/elections/{id}/candidates` | Add/remove candidates |
| **Voter Verification** | `/api/voters` | Verify, OTP (Public) |
| **Voting** | `/api/votes` | Cast vote, view results |

## 🔧 Configuration

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/voting_db
    username: voting_admin
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

### Environment Variables
```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret
```

## 🛡️ Security

### Rate Limits
- **Authentication APIs**: 10 requests/minute
- **Voting APIs**: 5 requests/minute  
- **Admin APIs**: 30 requests/minute
- **Public APIs**: 100 requests/minute

### Headers in Response
```
X-RateLimit-Limit: 100/minute
X-RateLimit-Remaining: 85
X-RateLimit-Reset: 1648735200000
```

### JWT Token
- Required for secured endpoints
- Include: `Authorization: Bearer <token>`
- Valid for 24 hours

## 🚨 Error Handling

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register"
}
```

## 🐳 Docker Deployment

```bash
# Using Docker Compose
docker-compose up --build

# Access at: http://localhost:8080/api/swagger-ui.html
```

## 📞 Support

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health
- **API Docs**: http://localhost:8080/api/v3/api-docs

---

**Built with Spring Boot, PostgreSQL, and JWT Authentication**
