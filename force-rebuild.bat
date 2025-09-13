@echo off
echo === Force Rebuild - AuthPlugin ===
echo Forçando recompilação completa...

REM Para o servidor se estiver rodando
echo Parando servidor se estiver rodando...
taskkill /f /im java.exe >nul 2>&1

REM Remove pasta target completamente
echo Removendo arquivos antigos...
if exist "target" rmdir /s /q "target"
if exist "src\main\resources" rmdir /s /q "src\main\resources"

REM Cria estrutura de recursos
echo Criando estrutura de recursos...
mkdir "src\main\resources"

REM Copia arquivos de configuração
echo Copiando arquivos de configuração...
copy "plugin.yml" "src\main\resources\" >nul
copy "config.yml" "src\main\resources\" >nul

REM Compila o projeto
echo Compilando projeto...
call mvn clean package -q

if %errorlevel% equ 0 (
    echo ✅ Compilação concluída!
    echo.
    echo Testando JAR...
    jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml
    
    if %errorlevel% equ 0 (
        echo ✅ plugin.yml encontrado!
        echo.
        echo Verificando se mojangAPI está inicializado...
        jar -tf target\AuthPlugin-1.0.0.jar | findstr MojangAPI
        
        if %errorlevel% equ 0 (
            echo ✅ MojangAPI encontrado no JAR!
            echo.
            echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
            echo.
            echo PRÓXIMOS PASSOS:
            echo 1. Copie target\AuthPlugin-1.0.0.jar para plugins/
            echo 2. Remova qualquer arquivo AuthPlugin antigo da pasta plugins/
            echo 3. Reinicie o servidor
            echo 4. Teste com sua conta original
        ) else (
            echo ❌ MojangAPI NÃO encontrado no JAR!
        )
    ) else (
        echo ❌ plugin.yml NÃO encontrado!
    )
) else (
    echo ❌ Erro na compilação!
    echo Verifique os logs acima.
)

echo.
echo === Force Rebuild Concluído ===
pause