@echo off
echo === Limpeza do AuthPlugin ===
echo Removendo arquivos problemáticos...

REM Remove arquivos JAR antigos
if exist "target\original-AuthPlugin-1.0.0.jar" del "target\original-AuthPlugin-1.0.0.jar"
if exist "target\original-*.jar" del "target\original-*.jar"

REM Remove arquivos de dependências reduzidas
if exist "target\dependency-reduced-pom.xml" del "target\dependency-reduced-pom.xml"

REM Remove pasta target completamente
if exist "target" rmdir /s /q "target"

echo Limpeza concluída!
echo Agora execute: build.bat
pause