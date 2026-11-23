# Compila y abre la interfaz gráfica. Con el argumento "consola" imprime la corrida de verificación.
#   .\ejecutar.ps1            → interfaz gráfica
#   .\ejecutar.ps1 consola    → corrida de verificación en la terminal
param([string]$modo = "")
$raiz = $PSScriptRoot
& (Join-Path $raiz "compilar.ps1")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$clases = Join-Path $raiz "out"
if ($modo -eq "consola") {
    [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
    java -cp $clases laberinto.Main --consola
} else {
    java -cp $clases laberinto.Main
}
exit $LASTEXITCODE
