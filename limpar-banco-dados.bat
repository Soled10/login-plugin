@echo off
echo === Limpeza do Banco de Dados ===
echo.

echo Removendo banco de dados antigo...
if exist "plugins\AuthPlugin\auth.db" (
    del "plugins\AuthPlugin\auth.db"
    echo ✅ Banco de dados removido
) else (
    echo ℹ️ Banco de dados não encontrado
)

echo.
echo Removendo pasta do plugin...
if exist "plugins\AuthPlugin" (
    rmdir /s /q "plugins\AuthPlugin"
    echo ✅ Pasta do plugin removida
) else (
    echo ℹ️ Pasta do plugin não encontrada
)

echo.
echo ✅ Limpeza concluída!
echo.
echo 📋 PRÓXIMOS PASSOS:
echo 1. Copie o novo plugin para plugins/
echo 2. Reinicie o servidor
echo 3. Entre com sua conta original (primeira vez)
echo 4. O sistema registrará o UUID offline correto
echo.

pause