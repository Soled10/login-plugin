@echo off
echo === Build Corrigido - AuthPlugin ===
echo Sistema que verifica se conta existe na API Mojang

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
        echo 🎯 SISTEMA CORRIGIDO:
        echo ✅ Verifica se conta existe na API Mojang
        echo ✅ Se existir = PREMIUM (independente do UUID)
        echo ✅ Se não existir = PIRATA
        echo ✅ UUIDs diferentes são normais com online-mode=false
        echo ✅ Proteção de nicks premium
        echo.
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo.
        echo CONFIGURAÇÃO:
        echo 1. Configure online-mode=false no server.properties
        echo 2. Instale o plugin
        echo 3. Reinicie o servidor
        echo.
        echo COMO FUNCIONA AGORA:
        echo - Consulta API Mojang para verificar se conta existe
        echo - Se existir = PREMIUM (entra automaticamente)
        echo - Se não existir = PIRATA (precisa registrar)
        echo - UUIDs diferentes são normais e esperados
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause