@echo off
echo === Teste do JAR ===
echo Verificando se o plugin.yml está no JAR...

if not exist "target\AuthPlugin-1.0.0.jar" (
    echo ERRO: Arquivo JAR não encontrado!
    echo Execute build.bat primeiro.
    pause
    exit /b 1
)

echo Verificando conteúdo do JAR:
jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml

if %errorlevel% equ 0 (
    echo.
    echo ✅ plugin.yml encontrado no JAR!
    echo.
    echo Verificando config.yml:
    jar -tf target\AuthPlugin-1.0.0.jar | findstr config.yml
    
    if %errorlevel% equ 0 (
        echo ✅ config.yml encontrado no JAR!
    ) else (
        echo ❌ config.yml NÃO encontrado no JAR!
    )
) else (
    echo ❌ plugin.yml NÃO encontrado no JAR!
    echo.
    echo Listando todos os arquivos no JAR:
    jar -tf target\AuthPlugin-1.0.0.jar
)

echo.
echo === Teste Concluído ===
pause