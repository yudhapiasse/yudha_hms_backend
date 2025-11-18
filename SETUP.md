# HMS Backend Setup Guide

## Prerequisites Installation

### 1. Install Java 21 LTS

**macOS (using Homebrew):**
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 21
brew install openjdk@21

# Set JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

**Windows:**
1. Download OpenJDK 21 from: https://adoptium.net/
2. Install and set JAVA_HOME environment variable:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Environment Variables → New System Variable
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x.x-hotspot`
   - Add to PATH: `%JAVA_HOME%\bin`

**Linux (Ubuntu/Debian):**
```bash
# Install Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# Verify installation
java -version
```

### 2. Install Maven

**macOS:**
```bash
brew install maven
mvn -version
```

**Windows:**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`

**Linux:**
```bash
sudo apt install maven
mvn -version
```

### 3. Install PostgreSQL 16

**macOS (Homebrew):**
```bash
brew install postgresql@16
brew services start postgresql@16

# Create database
psql postgres
CREATE DATABASE hms_dev;
CREATE USER hms_user WITH PASSWORD 'hms_password';
GRANT ALL PRIVILEGES ON DATABASE hms_dev TO hms_user;
\q
```

**macOS (Enterprise DB - if already installed):**
```bash
# Check if PostgreSQL is running
ps aux | grep postgres

# If not running, start it:
# Usually starts automatically, or use pgAdmin to start

# Add PostgreSQL to PATH (add to ~/.zshrc or ~/.bash_profile)
export PATH="/Library/PostgreSQL/16/bin:$PATH"
source ~/.zshrc

# Initialize HMS database
/Library/PostgreSQL/16/bin/psql -U postgres -f "database/00_init_database.sql"
# Enter postgres user password when prompted
```

**Windows:**
1. Download PostgreSQL 16 from: https://www.postgresql.org/download/windows/
2. Run installer and set password
3. Use pgAdmin to create database and user

**Linux:**
```bash
# Add PostgreSQL repository
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget -qO- https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo tee /etc/apt/trusted.gpg.d/pgdg.asc &>/dev/null
sudo apt update
sudo apt install postgresql-16 postgresql-contrib-16

# Start PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database
sudo -u postgres psql
CREATE DATABASE hms_dev;
CREATE USER hms_user WITH PASSWORD 'hms_password';
GRANT ALL PRIVILEGES ON DATABASE hms_dev TO hms_user;
\q
```

### 4. Install IntelliJ IDEA (Recommended)

**Download:**
- Community Edition (Free): https://www.jetbrains.com/idea/download/
- Ultimate Edition (Paid, better for Spring): https://www.jetbrains.com/idea/

**Import Project:**
1. Open IntelliJ IDEA
2. File → Open → Select `hms-backend` folder
3. Trust the project
4. IntelliJ will auto-detect Maven and download dependencies

## Verify Installation

Run these commands to verify everything is set up correctly:

```bash
# Check Java
java -version
# Expected: openjdk version "21.x.x"

# Check Maven
mvn -version
# Expected: Apache Maven 3.9.x

# Check PostgreSQL
psql --version
# Expected: psql (PostgreSQL) 16.x
```

## Database Setup

### 1. Initialize Database (First Time Only)

Navigate to project directory:
```bash
cd "/Volumes/Data001/Hospital MS Dev/hms-backend"
```

Run the initialization script:
```bash
# For Enterprise DB installation
/Library/PostgreSQL/16/bin/psql -U postgres -f database/00_init_database.sql

# For Homebrew installation (macOS)
psql -d postgres -f database/00_init_database.sql
```

This script will:
- Create `hms_dev` database
- Create `hms_user` with password `hms_password`
- Install required PostgreSQL extensions (uuid-ossp, pgcrypto, pg_trgm, unaccent)
- Create 10 schemas for modular architecture
- Grant necessary privileges

### 2. Run Flyway Migrations

Flyway migrations will run automatically when you start the application.
The migrations will create all tables and populate master data.

To manually run Flyway:
```bash
mvn flyway:migrate
```

To check migration status:
```bash
mvn flyway:info
```

## Build and Run

### 1. Build the project
```bash
mvn clean install
```

### 2. Run the application
```bash
mvn spring-boot:run
```

### 4. Verify application is running
Open browser and go to:
- Health Check: http://localhost:8080/actuator/health

You should see:
```json
{
  "status": "UP"
}
```

## Troubleshooting

### "JAVA_HOME not defined" error
Make sure JAVA_HOME is set:
```bash
# macOS/Linux
echo $JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Windows
echo %JAVA_HOME%
```

### "Cannot connect to database" error
1. Verify PostgreSQL is running
2. Check credentials in `src/main/resources/application-dev.yml`
3. Ensure database `hms_dev` exists

### "Port 8080 already in use" error
Either:
- Stop the process using port 8080
- Change port in `application.yml`: `server.port: 8081`

### Maven dependency download fails
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

## Next Steps

After successful setup:
1. ✅ Review the README.md file
2. ✅ Follow the HMS Development Guide (DocumentDev folder)
3. ✅ Start with Phase 2: Core Patient Management
4. ✅ Implement features incrementally following the guide

## IDE Configuration

### IntelliJ IDEA Settings

1. **Enable Lombok:**
   - File → Settings → Plugins → Install "Lombok"
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

2. **Code Style:**
   - File → Settings → Editor → Code Style → Java
   - Set indent: 4 spaces
   - Set continuation indent: 8 spaces

3. **Enable Live Reload:**
   - Lombok plugin installed
   - Build project automatically enabled

## Support

For issues or questions:
- Check the main README.md
- Review the HMS Development Guide
- Check Spring Boot documentation: https://spring.io/projects/spring-boot

---

**Good luck with your HMS development!** 🏥