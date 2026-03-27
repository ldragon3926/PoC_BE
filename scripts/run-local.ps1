Param(
  [string]$EnvFile = ".env.local"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $EnvFile)) {
  Write-Host "Missing $EnvFile. Creating from .env.local.example..."
  Copy-Item ".env.local.example" $EnvFile
  Write-Host "Created $EnvFile. Update secrets if needed, then run again."
}

Get-Content $EnvFile | ForEach-Object {
  $line = $_.Trim()
  if (-not $line -or $line.StartsWith("#")) { return }
  $parts = $line -split "=", 2
  if ($parts.Count -eq 2) {
    $key = $parts[0].Trim()
    $value = $parts[1].Trim()
    [Environment]::SetEnvironmentVariable($key, $value, "Process")
  }
}

if (-not $env:SPRING_PROFILES_ACTIVE) {
  $env:SPRING_PROFILES_ACTIVE = "local"
}

Write-Host "Starting app with profile '$($env:SPRING_PROFILES_ACTIVE)'..."
.\mvnw.cmd spring-boot:run
