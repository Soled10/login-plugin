@echo off
echo === Build User Online Mode - AuthPlugin ===
echo Sistema que simula online-mode=true por usuário

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
        echo 🎯 SISTEMA DE VERIFICAÇÃO POR USUÁRIO:
        echo ✅ Simula online-mode=true para cada usuário
        echo ✅ Verifica se conta existe na API Mojang
        echo ✅ Compara UUID da API com UUID do jogador
        echo ✅ Se UUIDs coincidem = PREMIUM
        echo ✅ Se UUIDs não coincidem = PIRATA
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
        echo - Para cada usuário, simula online-mode=true
        echo - Consulta API Mojang para obter UUID oficial
        echo - Compara com UUID do jogador
        echo - Se coincidir = PREMIUM (entra automaticamente)
        echo - Se não coincidir = PIRATA (precisa registrar)
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause