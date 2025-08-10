@echo off
echo Creating .env file...
echo.

echo # Database Configuration > .env
echo DATABASE_URL=jdbc:postgresql://d2c2i1h5pdvs73d7t7ug-a.oregon-postgres.render.com:5432/vehiclemanagementdb >> .env
echo DATABASE_USERNAME=admin >> .env
echo DATABASE_PASSWORD=rfVvQsjzmBXlheqUBk2LWIS5cwfaW5Yl >> .env
echo. >> .env
echo # Email Configuration >> .env
echo MAIL_USERNAME=kalelofearth95@gmail.com >> .env
echo MAIL_PASSWORD=mdcr gnqd paxu bypj >> .env
echo. >> .env
echo # Flask Service Configuration >> .env
echo FLASK_BASE_URL=http://localhost:5000 >> .env
echo. >> .env
echo # JWT Configuration >> .env
echo JWT_SECRET=2?Q:mba;7mvN[^0=Ixw54z@YOiySC!TqGF)-20tOL`*qs^AlK_|gYoS;ah0r2Oj >> .env

echo .env file created successfully!
echo.
echo IMPORTANT: Change your database password immediately!
echo The .env file is now ignored by Git.
pause 