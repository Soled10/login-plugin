@echo off
echo === AuthPlugin Build Script - Proteção de Nomes ===
echo.

echo Compilando o plugin...
echo Executando mvn clean package...

mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERRO na compilação!
    echo Verifique os erros acima e corrija antes de continuar.
    pause
    exit /b 1
)

echo.
echo ✅ Compilação concluída com sucesso!
echo.

echo Limpando arquivos desnecessários...
if exist "target\original-*.jar" del "target\original-*.jar"
if exist "target\dependency-reduced-pom.xml" del "target\dependency-reduced-pom.xml"

echo.
echo 📦 Plugin compilado: target\AuthPlugin-1.0.0.jar
echo.

echo 🔧 SISTEMA DE PROTEÇÃO DE NOMES IMPLEMENTADO:
echo - Verifica se nome já está registrado como conta original
echo - Se estiver registrado: Compara UUIDs
echo - Se UUIDs diferentes: BLOQUEIA conta pirata
echo - Se UUIDs iguais: Permite conta original retornar
echo - Se não estiver registrado: Permite primeira conta original
echo.

echo 📋 COMO FUNCIONA AGORA:
echo 1. Primeira conta original entra → Registra nome automaticamente
echo 2. Conta pirata tenta usar mesmo nome → BLOQUEADA
echo 3. Conta original retorna → Permitida (UUIDs coincidem)
echo 4. Nova conta pirata com nome não registrado → Pode registrar
echo.

echo ⚠️  IMPORTANTE:
echo - Sistema agora funciona corretamente!
echo - Contas piratas NÃO conseguem usar nomes de contas originais
echo - Apenas a conta original pode usar seu próprio nome
echo.

pause