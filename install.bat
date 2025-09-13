@echo off
echo === Instalação Automática - AuthPlugin ===

REM Verifica se o JAR foi compilado
if not exist "target\AuthPlugin-1.0.0.jar" (
    echo ❌ JAR não encontrado! Execute force-rebuild.bat primeiro.
    pause
    exit /b 1
)

REM Pergunta onde está a pasta plugins
set /p PLUGINS_PATH="Digite o caminho da pasta plugins do seu servidor: "

REM Verifica se a pasta existe
if not exist "%PLUGINS_PATH%" (
    echo ❌ Pasta não encontrada: %PLUGINS_PATH%
    pause
    exit /b 1
)

REM Remove arquivos antigos do AuthPlugin
echo Removendo versões antigas...
del "%PLUGINS_PATH%\AuthPlugin*.jar" >nul 2>&1
del "%PLUGINS_PATH%\original-AuthPlugin*.jar" >nul 2>&1

REM Copia o novo JAR
echo Instalando novo plugin...
copy "target\AuthPlugin-1.0.0.jar" "%PLUGINS_PATH%\" >nul

if %errorlevel% equ 0 (
    echo ✅ Plugin instalado com sucesso!
    echo.
    echo Arquivo instalado: %PLUGINS_PATH%\AuthPlugin-1.0.0.jar
    echo.
    echo PRÓXIMOS PASSOS:
    echo 1. Configure online-mode=false no server.properties
    echo 2. Reinicie o servidor
    echo 3. Teste com sua conta original
    echo.
    echo O plugin deve detectar sua conta original automaticamente!
) else (
    echo ❌ Erro ao instalar o plugin!
)

pause