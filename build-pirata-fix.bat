@echo off
echo === AuthPlugin Build Script - Correção Conta Pirata ===
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

echo 🔧 CORREÇÃO APLICADA:
echo - Agora o sistema verifica se o nome já está registrado como conta original
echo - Se estiver registrado, bloqueia contas piratas que tentam usar o mesmo nome
echo - Se não estiver registrado, permite a primeira conta original usar o nome
echo.

echo 📋 INSTRUÇÕES:
echo 1. Copie o arquivo target\AuthPlugin-1.0.0.jar para a pasta plugins do servidor
echo 2. Reinicie o servidor
echo 3. Teste com uma conta original primeiro (deve funcionar)
echo 4. Depois teste com uma conta pirata usando o mesmo nome (deve ser bloqueada)
echo.

echo ⚠️  IMPORTANTE:
echo - A primeira conta original que entrar será registrada automaticamente
echo - Contas piratas subsequentes com o mesmo nome serão bloqueadas
echo - O sistema agora funciona corretamente com online-mode=false
echo.

pause