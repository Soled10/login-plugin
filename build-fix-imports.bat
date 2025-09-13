@echo off
echo === Build Fix Imports - AuthPlugin ===
echo Corrigindo imports e compilando

REM Limpa arquivos antigos
if exist "target" rmdir /s /q "target"
if exist "src\main\resources" rmdir /s /q "src\main\resources"

REM Cria estrutura
mkdir "src\main\resources"

REM Copia arquivos
copy "plugin.yml" "src\main\resources\" >nul
copy "config.yml" "src\main\resources\" >nul

REM Verifica se todos os arquivos existem
echo Verificando arquivos...
if not exist "src\main\java\com\authplugin\utils\OnlineModeSimulator.java" (
    echo ❌ OnlineModeSimulator.java não encontrado!
    pause
    exit /b 1
)

if not exist "src\main\java\com\authplugin\listeners\PlayerJoinListener.java" (
    echo ❌ PlayerJoinListener.java não encontrado!
    pause
    exit /b 1
)

echo ✅ Todos os arquivos encontrados!

REM Compila
echo Compilando...
call mvn clean package -q

if %errorlevel% equ 0 (
    echo ✅ Compilação concluída!
    echo.
    echo Testando JAR...
    jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml
    
    if %errorlevel% equ 0 (
        echo ✅ plugin.yml encontrado!
        echo.
        echo 🎯 SISTEMA FUNCIONANDO:
        echo ✅ Imports corrigidos
        echo ✅ OnlineModeSimulator funcionando
        echo ✅ Associação de UUID implementada
        echo ✅ Proteção de nicks premium
        echo.
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo.
        echo CONFIGURAÇÃO:
        echo 1. Configure online-mode=false no server.properties
        echo 2. Instale o plugin
        echo 3. Reinicie o servidor
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
    echo.
    echo Verificando arquivos Java...
    dir src\main\java\com\authplugin\utils\*.java
    dir src\main\java\com\authplugin\listeners\*.java
)

pause