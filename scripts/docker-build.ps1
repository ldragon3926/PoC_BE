Param(
  [Parameter(Mandatory = $true)]
  [string]$ImageName,

  [Parameter(Mandatory = $true)]
  [string]$Version,

  [switch]$TagLatest
)

$ErrorActionPreference = "Stop"

if ($Version -notmatch '^\d+\.\d+\.\d+(-[0-9A-Za-z\.-]+)?$') {
  throw "Version must follow SemVer format, example: 1.0.0 or 1.0.0-rc.1"
}

$gitSha = (git rev-parse --short HEAD).Trim()
if (-not $gitSha) {
  throw "Cannot resolve git commit SHA."
}

$versionTag = "$ImageName`:$Version"
$shaTag = "$ImageName`:$Version-$gitSha"

Write-Host "Building $versionTag and $shaTag ..."
docker build -t $versionTag -t $shaTag .

if ($TagLatest) {
  $latestTag = "$ImageName`:latest"
  docker tag $versionTag $latestTag
  Write-Host "Tagged latest: $latestTag"
}

Write-Host "Done."
Write-Host "Created tags:"
Write-Host " - $versionTag"
Write-Host " - $shaTag"
if ($TagLatest) {
  Write-Host " - $ImageName`:latest"
}
