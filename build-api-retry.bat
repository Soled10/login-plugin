@echo off
echo === Build API Retry - AuthPlugin ===
echo Sistema com retry para erros 503 da API Mojang

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
        echo 🎯 SISTEMA COM RETRY DE API:
        echo ✅ Trata erro 503 (Service Unavailable)
        echo ✅ Tenta novamente até 3 vezes
        echo ✅ Espera progressiva entre tentativas
        echo ✅ Logs detalhados para debug
        echo ✅ Timeout aumentado para 10 segundos
        echo ✅ Associação de UUID oficial
        echo.
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo.
        echo CONFIGURAÇÃO:
        echo 1. Configure online-mode=false no server.properties
        echo 2. Instale o plugin
        echo 3. Reinicie o servidor
        echo.
        echo COMO FUNCIONA:
        echo - Se API retornar 503, tenta novamente
        echo - Até 3 tentativas com espera progressiva
        echo - Logs detalhados para debug
        echo - Se conseguir UUID, associa ao jogador
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause