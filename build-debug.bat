@echo off
echo === Build Debug - AuthPlugin ===
echo Compilando com logs detalhados

REM Limpa arquivos antigos
if exist "target" rmdir /s /q "target"
if exist "src\main\resources" rmdir /s /q "src\main\resources"

REM Cria estrutura
mkdir "src\main\resources"

REM Copia arquivos
copy "plugin.yml" "src\main\resources\" >nul
copy "config.yml" "src\main\resources\" >nul

REM Lista arquivos Java
echo Arquivos Java encontrados:
dir src\main\java\com\authplugin\utils\*.java /b
dir src\main\java\com\authplugin\listeners\*.java /b

echo.
echo Compilando com logs detalhados...
call mvn clean compile -X

if %errorlevel% equ 0 (
    echo.
    echo ✅ Compilação concluída!
    echo.
    echo Fazendo package...
    call mvn package -q
    
    if %errorlevel% equ 0 (
        echo ✅ Package concluído!
        echo.
        echo Testando JAR...
        jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml
        
        if %errorlevel% equ 0 (
            echo ✅ plugin.yml encontrado!
            echo.
            echo Arquivo pronto: target\AuthPlugin-1.0.0.jar
        ) else (
            echo ❌ plugin.yml NÃO encontrado!
        )
    ) else (
        echo ❌ Erro no package!
    )
) else (
    echo ❌ Erro na compilação!
)

pause