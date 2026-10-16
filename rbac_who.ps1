$ProgressPreference='SilentlyContinue'
function Get-Tok($u){ return (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body (@{username=$u;password='123456'}|ConvertTo-Json -Compress) -TimeoutSec 8).data.token }
$ad=Get-Tok 'admin'; $cu=Get-Tok 'user01'; $ag=Get-Tok 'agent01'
foreach($x in @(@('admin',$ad),@('user01',$cu),@('agent01',$ag))){
  $n=$x[0]; $t=$x[1]
  Write-Output ("--- "+$n+" ---")
  & curl.exe -s -H "Authorization: Bearer $t" "http://localhost:8080/api/debug/whoami"
  Write-Output ""
}