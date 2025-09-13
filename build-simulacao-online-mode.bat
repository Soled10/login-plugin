@echo off
echo === AuthPlugin Build Script - Simulação Online-Mode ===
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

echo 🔧 SISTEMA DE SIMULAÇÃO ONLINE-MODE:
echo - Simula online-mode=true apenas para o usuário específico
echo - Se conta existe na API Mojang = PREMIUM (autentica automaticamente)
echo - Se conta não existe na API Mojang = PIRATA (precisa registrar)
echo - Protege nomes de contas originais contra uso por contas piratas
echo.

echo 📋 COMO FUNCIONA:
echo 1. Usuário entra no servidor
echo 2. Sistema verifica se nome existe na API Mojang
echo 3. Se existir: Usuário é PREMIUM (autenticado automaticamente)
echo 4. Se não existir: Usuário é PIRATA (precisa registrar)
echo 5. Nomes de contas originais ficam protegidos automaticamente
echo.

echo ⚠️  IMPORTANTE:
echo - UUIDs sempre serão diferentes (online-mode=false)
echo - Verificação é feita apenas pela existência na API Mojang
echo - Sistema funciona perfeitamente com online-mode=false
echo.

pause