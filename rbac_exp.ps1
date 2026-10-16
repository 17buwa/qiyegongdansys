$ProgressPreference='SilentlyContinue'
function Get-Tok($u){ return (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body (@{username=$u;password='123456'}|ConvertTo-Json -Compress) -TimeoutSec 8).data.token }
$cu=Get-Tok 'user01'; $ad=Get-Tok 'admin'
Write-Output '-- customer -> /api/debug/admin (hasRole ADMIN) --'
& curl.exe -s -H "Authorization: Bearer $cu" "http://localhost:8080/api/debug/admin"
Write-Output ""
Write-Output '-- admin -> /api/debug/deny (denyAll) --'
& curl.exe -s -H "Authorization: Bearer $ad" "http://localhost:8080/api/debug/deny"
Write-Output ""