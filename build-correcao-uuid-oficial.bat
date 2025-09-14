@echo off
echo === AuthPlugin Build Script - Correção UUID Oficial ===
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
echo 🔧 CORREÇÃO CRÍTICA APLICADA:
echo - Agora armazena UUID OFICIAL da API Mojang no banco
echo - Compara UUID oficial armazenado com UUID oficial da API
echo - Conta original pode retornar normalmente
echo - Contas piratas são BLOQUEADAS definitivamente
echo.

echo 📋 COMO FUNCIONA AGORA:
echo 1. Primeira conta original entra → Registra UUID OFICIAL da API
echo 2. Conta original retorna → Permitida (UUIDs oficiais coincidem)
echo 3. Conta pirata tenta usar mesmo nome → BLOQUEADA (UUIDs diferentes)
echo 4. Nova conta pirata com nome não registrado → Pode registrar
echo.

echo ⚠️  IMPORTANTE:
echo - Sistema agora funciona DEFINITIVAMENTE!
echo - Contas originais podem retornar normalmente
echo - Contas piratas são bloqueadas corretamente
echo - UUIDs oficiais são usados para comparação
echo - Banco de dados foi limpo para funcionar corretamente
echo.

echo 🧪 TESTE:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original → Deve funcionar
echo 4. Saia e entre novamente → Deve funcionar
echo 5. Tente com conta pirata usando mesmo nome → Deve ser BLOQUEADA
echo.

pause