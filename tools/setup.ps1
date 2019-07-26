# Stop when error occurs
# $ErrorActionPreference = "Stop"

$ChocoInstalled = $false

function local:Status([String] $Message){
  Write-Host $Message -ForegroundColor DarkMagenta
}

Status("1. Install nvm")
Install-Module -Name nvm

Status("1.1 Ensure nvm is in your current process")
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Status("2. Install Node")
Set-Location -Path (Get-Item -Path $PSScriptRoot).Parent.FullName
Install-NodeVersion
Set-NodeVersion -Version
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Status("3. Update npm to latest version")
npm install -g npm@latest

Status("4. Install dependencies")
node --version
npm --version
npm ci


