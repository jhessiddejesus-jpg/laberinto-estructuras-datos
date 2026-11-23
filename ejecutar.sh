#!/usr/bin/env sh
# Compila y ejecuta (Linux / macOS / Git Bash).
#   ./ejecutar.sh            → interfaz gráfica
#   ./ejecutar.sh consola    → corrida de verificación en la terminal
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 --release 17 -d out $(find src -name "*.java")
if [ "$1" = "consola" ]; then
  java -cp out laberinto.Main --consola
else
  java -cp out laberinto.Main
fi
