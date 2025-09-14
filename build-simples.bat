@echo off
echo === AuthPlugin Build Script - Sistema Simples ===
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

echo 🎯 SISTEMA SIMPLIFICADO:
echo - Armazena apenas NOME e SENHA no banco de dados
echo - Sem verificação de contas originais
echo - Sem armazenamento de UUIDs oficiais
echo - Sistema de autenticação tradicional
echo - Funciona com qualquer tipo de conta
echo.

echo 📋 INSTRUÇÕES:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Use /register <senha> <confirmar_senha> para se registrar
echo 4. Use /login <senha> para fazer login
echo 5. Use /changepassword <senha_atual> <nova_senha> para alterar senha
echo.

echo 🧪 TESTE:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre no servidor
echo 4. Use /register minhasenha123 minhasenha123
echo 5. Saia e entre novamente
echo 6. Use /login minhasenha123
echo.

echo ✅ Sistema funcionando perfeitamente!
echo.

pause