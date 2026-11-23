# Compila el proyecto en la carpeta "out". Requiere JDK 17 o superior.
$ErrorActionPreference = "Stop"
$raiz = $PSScriptRoot
$salida = Join-Path $raiz "out"
if (Test-Path $salida) { Remove-Item -Recurse -Force $salida }
$fuentes = Get-ChildItem -Recurse (Join-Path $raiz "src") -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 --release 17 -d $salida $fuentes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Compilado en $salida"
