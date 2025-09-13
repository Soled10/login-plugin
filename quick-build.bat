@echo off
echo === Quick Build - AuthPlugin ===
echo Compilando e testando...

REM Limpa arquivos antigos
if exist "target" rmdir /s /q "target"

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
        echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        echo Copie para plugins/ e reinicie o servidor.
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
)

pause