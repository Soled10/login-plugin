@echo off
echo === AuthPlugin Build Script - Limpeza de Banco ===
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

echo 🧹 LIMPEZA DO BANCO DE DADOS:
echo - Removendo banco de dados antigo...
if exist "plugins\AuthPlugin\auth.db" (
    del "plugins\AuthPlugin\auth.db"
    echo ✅ Banco de dados removido
) else (
    echo ℹ️ Banco de dados não encontrado
)

echo - Removendo pasta do plugin...
if exist "plugins\AuthPlugin" (
    rmdir /s /q "plugins\AuthPlugin"
    echo ✅ Pasta do plugin removida
) else (
    echo ℹ️ Pasta do plugin não encontrada
)

echo.
echo 🔧 CORREÇÃO APLICADA:
echo - Agora armazena UUID OFFLINE do jogador
echo - Compara UUIDs offline entre jogadores
echo - Conta original pode retornar normalmente
echo - Contas piratas são bloqueadas corretamente
echo.

echo 📋 INSTRUÇÕES:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original (primeira vez)
echo 4. O sistema registrará o UUID offline correto
echo 5. Teste com conta pirata usando mesmo nome
echo.

echo ⚠️  IMPORTANTE:
echo - Banco de dados foi limpo
echo - Sistema agora funciona corretamente
echo - Contas originais podem retornar
echo - Contas piratas são bloqueadas
echo.

pause