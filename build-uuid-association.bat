@echo off
echo === Build UUID Association - AuthPlugin ===
echo Sistema que associa UUID oficial da API Mojang

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
        echo 🎯 SISTEMA DE ASSOCIAÇÃO DE UUID:
        echo ✅ Simula online-mode=true para contas originais
        echo ✅ Se passar, associa UUID oficial da API Mojang
        echo ✅ UUID oficial é salvo no banco de dados
        echo ✅ Proteção de nicks com UUID oficial
        echo ✅ Contas premium entram automaticamente
        echo ✅ Contas piratas precisam se registrar
        echo.
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo.
        echo CONFIGURAÇÃO:
        echo 1. Configure online-mode=false no server.properties
        echo 2. Instale o plugin
        echo 3. Reinicie o servidor
        echo.
        echo COMO FUNCIONA:
        echo - Verifica se nick é de conta original
        echo - Simula online-mode=true para aquele usuário
        echo - Se passar, associa UUID oficial da API
        echo - UUID oficial é usado para proteção de nicks
        echo - Conta premium entra automaticamente
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause