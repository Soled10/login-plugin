@echo off
echo === Limpeza Completa do Banco de Dados ===
echo.

echo 🧹 Removendo banco de dados antigo...
if exist "plugins\AuthPlugin\auth.db" (
    del "plugins\AuthPlugin\auth.db"
    echo ✅ Banco de dados removido
) else (
    echo ℹ️ Banco de dados não encontrado
)

echo.
echo 🗂️ Removendo pasta do plugin...
if exist "plugins\AuthPlugin" (
    rmdir /s /q "plugins\AuthPlugin"
    echo ✅ Pasta do plugin removida
) else (
    echo ℹ️ Pasta do plugin não encontrado
)

echo.
echo 🗑️ Removendo plugin antigo...
if exist "plugins\AuthPlugin-1.0.0.jar" (
    del "plugins\AuthPlugin-1.0.0.jar"
    echo ✅ Plugin antigo removido
) else (
    echo ℹ️ Plugin antigo não encontrado
)

echo.
echo 🔍 Verificando se há outros arquivos do plugin...
if exist "plugins\original-AuthPlugin-1.0.0.jar" (
    del "plugins\original-AuthPlugin-1.0.0.jar"
    echo ✅ Arquivo original removido
)

echo.
echo ✅ Limpeza completa concluída!
echo.
echo 📋 PRÓXIMOS PASSOS:
echo 1. Copie o novo plugin para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original (primeira vez)
echo 4. O sistema registrará o UUID oficial correto
echo.

pause