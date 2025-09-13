@echo off
echo === AuthPlugin Build Script ===
echo Compilando o plugin...

REM Verifica se o Maven está instalado
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Erro: Maven não está instalado!
    echo Instale o Maven para continuar.
    pause
    exit /b 1
)

REM Limpa arquivos problemáticos
echo Limpando arquivos antigos...
if exist "target\original-*.jar" del "target\original-*.jar"
if exist "target\dependency-reduced-pom.xml" del "target\dependency-reduced-pom.xml"

REM Limpa e compila o projeto
echo Executando mvn clean package...
call mvn clean package -DskipTests

REM Verifica se a compilação foi bem-sucedida
if %errorlevel% equ 0 (
    echo.
    echo ✅ Compilação concluída com sucesso!
    echo.
    echo Arquivo JAR criado em: target/AuthPlugin-1.0.0.jar
    echo.
    echo Para instalar no servidor:
    echo 1. Copie o arquivo JAR para a pasta plugins/ do seu servidor
    echo 2. Configure online-mode=false no server.properties
    echo 3. Reinicie o servidor
    echo.
    echo IMPORTANTE: Se você tem uma conta original e está sendo pedido para registrar:
    echo - Verifique os logs do servidor para ver as mensagens de debug
    echo - O plugin agora tem logs detalhados para diagnosticar o problema
    echo - Se o servidor estiver em online-mode=true, contas originais são detectadas automaticamente
    echo.
    echo === Build Concluído ===
) else (
    echo.
    echo ❌ Erro na compilação!
    echo Verifique os logs acima para mais detalhes.
    pause
    exit /b 1
)

pause