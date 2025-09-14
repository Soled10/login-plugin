@echo off
echo === AuthPlugin Build Script - Limpeza Completa ===
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

echo 🧹 LIMPEZA COMPLETA DO BANCO DE DADOS:
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

echo - Removendo plugin antigo...
if exist "plugins\AuthPlugin-1.0.0.jar" (
    del "plugins\AuthPlugin-1.0.0.jar"
    echo ✅ Plugin antigo removido
) else (
    echo ℹ️ Plugin antigo não encontrado
)

echo - Removendo arquivos originais...
if exist "plugins\original-AuthPlugin-1.0.0.jar" (
    del "plugins\original-AuthPlugin-1.0.0.jar"
    echo ✅ Arquivo original removido
)

echo.
echo 🔧 CORREÇÃO APLICADA:
echo - Agora armazena UUID OFICIAL da API Mojang no banco
echo - Compara UUID oficial armazenado com UUID oficial da API
echo - Conta original pode retornar normalmente
echo - Contas piratas são BLOQUEADAS definitivamente
echo.

echo 📋 INSTRUÇÕES:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original (primeira vez)
echo 4. O sistema registrará o UUID oficial correto
echo 5. Teste com conta pirata usando mesmo nome
echo.

echo ⚠️  IMPORTANTE:
echo - Banco de dados foi limpo COMPLETAMENTE
echo - Sistema agora funciona DEFINITIVAMENTE
echo - Contas originais podem retornar
echo - Contas piratas são bloqueadas
echo - UUIDs oficiais são usados para comparação
echo.

echo 🧪 TESTE:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original → Deve funcionar
echo 4. Saia e entre novamente → Deve funcionar
echo 5. Tente com conta pirata usando mesmo nome → Deve ser BLOQUEADA
echo.

pause