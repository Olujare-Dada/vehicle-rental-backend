# Configuration Guide

## Environment Variables Setup

### 1. Create a .env file in the project root:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://d2c2i1h5pdvs73d7t7ug-a.oregon-postgres.render.com:5432/vehiclemanagementdb
DATABASE_USERNAME=admin
DATABASE_PASSWORD=rfVvQsjzmBXlheqUBk2LWIS5cwfaW5Yl

# Email Configuration
MAIL_USERNAME=kalelofearth95@gmail.com
MAIL_PASSWORD=mdcr gnqd paxu bypj

# Flask Service Configuration
FLASK_BASE_URL=http://localhost:5000

# JWT Configuration
JWT_SECRET=2?Q:mba;7mvN[^0=Ixw54z@YOiySC!TqGF)-20tOL`*qs^AlK_|gYoS;ah0r2Oj
```

### 2. For Production/Deployment:

Set these environment variables in your deployment platform:

```bash
export DATABASE_URL="jdbc:postgresql://d2c2i1h5pdvs73d7t7ug-a.oregon-postgres.render.com:5432/vehiclemanagementdb"
export DATABASE_USERNAME="admin"
export DATABASE_PASSWORD="your-new-secure-password"
export MAIL_USERNAME="kalelofearth95@gmail.com"
export MAIL_PASSWORD="your-email-password"
export FLASK_BASE_URL="http://localhost:5000"
```

### 3. For Windows PowerShell:

```powershell
$env:DATABASE_URL="jdbc:postgresql://d2c2i1h5pdvs73d7t7ug-a.oregon-postgres.render.com:5432/vehiclemanagementdb"
$env:DATABASE_USERNAME="admin"
$env:DATABASE_PASSWORD="your-new-secure-password"
$env:MAIL_USERNAME="kalelofearth95@gmail.com"
$env:MAIL_PASSWORD="your-email-password"
$env:FLASK_BASE_URL="http://localhost:5000"
```

## Security Notes

⚠️ **IMPORTANT:**
- Never commit the .env file to version control
- Change your database password immediately after setting up environment variables
- Use strong, unique passwords for production
- Consider using a secrets management service for production deployments

## Application Properties

The application now uses pure environment variables:
- **No fallback values** - environment variables must be set
- Uses `spring-dotenv` dependency to automatically read `.env` files
- Application will fail to start if required environment variables are missing
- This ensures secure configuration management across all environments 