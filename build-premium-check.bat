@echo off
echo === Build Premium Check - AuthPlugin ===
echo Sistema de verificação via API Mojang (simula online-mode=true)

REM Limpa arquivos antigos
if exist "target" rmdir /s /q "target"
if exist "src\main\resources" rmdir /s /q "src\main\resources"

REM Cria estrutura
mkdir "src\main\resources"

REM Copia arquivos
copy "plugin.yml" "src\main\resources\" >nul
copy "config.yml" "src\main\resources\" >nul

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
        echo 🎯 SISTEMA DE VERIFICAÇÃO PREMIUM:
        echo ✅ Verifica via API Mojang se conta existe
        echo ✅ Simula online-mode=true para contas premium
        echo ✅ Contas premium entram automaticamente
        echo ✅ Contas piratas precisam se registrar
        echo ✅ Proteção de nicks premium
        echo.
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo.
        echo CONFIGURAÇÃO:
        echo 1. Configure online-mode=false no server.properties
        echo 2. Instale o plugin
        echo 3. Reinicie o servidor
        echo.
        echo COMO FUNCIONA:
        echo - Plugin verifica via API Mojang se conta existe
        echo - Se existir = PREMIUM (entra automaticamente)
        echo - Se não existir = PIRATA (precisa registrar)
        echo - Nicks de contas premium são protegidos
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause