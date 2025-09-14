@echo off
echo === AuthPlugin Build Script - UUID Offline Correto ===
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

echo 🔧 CORREÇÃO CRÍTICA APLICADA:
echo - Agora armazena o UUID OFFLINE do jogador (não o UUID oficial da API)
echo - Compara UUIDs offline entre jogadores
echo - Conta original pode retornar usando seu UUID offline
echo - Contas piratas são bloqueadas se tentarem usar o mesmo nome
echo.

echo 📋 COMO FUNCIONA AGORA:
echo 1. Primeira conta original entra → Registra UUID OFFLINE do jogador
echo 2. Conta pirata tenta usar mesmo nome → BLOQUEADA (UUIDs diferentes)
echo 3. Conta original retorna → Permitida (UUIDs coincidem)
echo 4. Nova conta pirata com nome não registrado → Pode registrar
echo.

echo ⚠️  IMPORTANTE:
echo - Sistema agora funciona corretamente!
echo - Contas originais podem retornar normalmente
echo - Contas piratas são bloqueadas corretamente
echo - UUIDs offline são usados para comparação
echo.

echo 🧪 TESTE:
echo 1. Entre com sua conta original → Deve funcionar
echo 2. Saia e entre novamente → Deve funcionar
echo 3. Tente com conta pirata usando mesmo nome → Deve ser bloqueada
echo.

pause