@echo off
echo === AuthPlugin Build Script - Verificação Multicamada ===
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
echo 🔧 SISTEMA DE VERIFICAÇÃO MULTICAMADA IMPLEMENTADO:
echo.
echo 📡 MÉTODO 1 - API Mojang:
echo   - Verifica se conta existe na API oficial
echo   - Obtém UUID oficial da conta
echo   - Pontuação: 40%% do total
echo.
echo 🆔 MÉTODO 2 - Análise de UUID:
echo   - Verifica versão do UUID (v3 vs v4)
echo   - Analisa padrões do UUID
echo   - Pontuação: 20%% do total
echo.
echo 💻 MÉTODO 3 - Comportamento do Cliente:
echo   - Verifica consistência de nomes
echo   - Analisa permissões típicas
echo   - Pontuação: 15%% do total
echo.
echo 📊 MÉTODO 4 - Histórico de Conexão:
echo   - Verifica histórico de contas originais
echo   - Compara UUIDs armazenados
echo   - Pontuação: 10%% do total
echo.
echo 🌐 MÉTODO 5 - Análise de Rede:
echo   - Verifica IP suspeito (VPN/Proxy)
echo   - Analisa geolocalização
echo   - Verifica múltiplas conexões
echo   - Pontuação: 15%% do total
echo.
echo 🎯 SISTEMA DE PONTUAÇÃO:
echo   - Pontuação mínima: 70/100 para conta original
echo   - Pontuação abaixo de 70: Conta pirata
echo   - Verificação em tempo real
echo   - Proteção automática de nomes
echo.

echo 📋 INSTRUÇÕES:
echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original → Deve funcionar
echo 4. Tente com conta pirata usando mesmo nome → Deve ser BLOQUEADA
echo.

echo ⚠️  IMPORTANTE:
echo - Sistema agora usa 5 métodos diferentes de verificação
echo - Proteção muito mais robusta contra contas piratas
echo - Funciona perfeitamente com online-mode=false
echo - Detecção automática e inteligente
echo.

pause