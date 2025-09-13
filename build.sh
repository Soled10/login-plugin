#!/bin/bash

# Script de build para AuthPlugin
# Compila o plugin e prepara para instalação

echo "🔨 Compilando AuthPlugin..."

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado! Por favor instale o Maven primeiro."
    exit 1
fi

# Limpar e compilar
echo "🧹 Limpando projeto..."
mvn clean

echo "📦 Compilando e empacotando..."
mvn package

# Verificar se a compilação foi bem-sucedida
if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "📁 Arquivo gerado: target/AuthPlugin-1.0.0.jar"
    echo ""
    echo "📋 Próximos passos:"
    echo "1. Copie o arquivo JAR para a pasta plugins/ do seu servidor"
    echo "2. Configure online-mode=false no server.properties"
    echo "3. Reinicie o servidor"
    echo "4. Configure o plugin em plugins/AuthPlugin/config.yml"
    echo ""
    echo "🎉 Plugin pronto para uso!"
else
    echo "❌ Erro na compilação! Verifique os logs acima."
    exit 1
fi