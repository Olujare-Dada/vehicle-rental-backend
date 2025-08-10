Write-Host "Creating .env file..." -ForegroundColor Green
Write-Host ""

# Database Configuration
"# Database Configuration" | Out-File -FilePath .env -Encoding UTF8
"DATABASE_URL=jdbc:postgresql://d2c2i1h5pdvs73d7t7ug-a.oregon-postgres.render.com:5432/vehiclemanagementdb" | Out-File -FilePath .env -Append -Encoding UTF8
"DATABASE_USERNAME=admin" | Out-File -FilePath .env -Append -Encoding UTF8
"DATABASE_PASSWORD=rfVvQsjzmBXlheqUBk2LWIS5cwfaW5Yl" | Out-File -FilePath .env -Append -Encoding UTF8
"" | Out-File -FilePath .env -Append -Encoding UTF8

# Email Configuration
"# Email Configuration" | Out-File -FilePath .env -Append -Encoding UTF8
"MAIL_USERNAME=kalelofearth95@gmail.com" | Out-File -FilePath .env -Append -Encoding UTF8
"MAIL_PASSWORD=mdcr gnqd paxu bypj" | Out-File -FilePath .env -Append -Encoding UTF8
"" | Out-File -FilePath .env -Append -Encoding UTF8

# Flask Service Configuration
"# Flask Service Configuration" | Out-File -FilePath .env -Append -Encoding UTF8
"FLASK_BASE_URL=http://localhost:5000" | Out-File -FilePath .env -Append -Encoding UTF8
"" | Out-File -FilePath .env -Append -Encoding UTF8

# JWT Configuration
"# JWT Configuration" | Out-File -FilePath .env -Append -Encoding UTF8
"JWT_SECRET=2?Q:mba;7mvN[^0=Ixw54z@YOiySC!TqGF)-20tOL`*qs^AlK_|gYoS;ah0r2Oj" | Out-File -FilePath .env -Append -Encoding UTF8

Write-Host ".env file created successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANT: Change your database password immediately!" -ForegroundColor Yellow
Write-Host "The .env file is now ignored by Git." -ForegroundColor Green
Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 