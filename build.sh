#!/bin/bash

# Script de build para o AuthPlugin
# Compila o plugin e cria o arquivo JAR

echo "=== AuthPlugin Build Script ==="
echo "Compilando o plugin..."

# Verifica se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "Erro: Maven não está instalado!"
    echo "Instale o Maven para continuar."
    exit 1
fi

# Limpa e compila o projeto
echo "Executando mvn clean package..."
mvn clean package

# Verifica se a compilação foi bem-sucedida
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "Arquivo JAR criado em: target/AuthPlugin-1.0.0.jar"
    echo ""
    echo "Para instalar no servidor:"
    echo "1. Copie o arquivo JAR para a pasta plugins/ do seu servidor"
    echo "2. Configure online-mode=false no server.properties"
    echo "3. Reinicie o servidor"
    echo ""
    echo "=== Build Concluído ==="
else
    echo ""
    echo "❌ Erro na compilação!"
    echo "Verifique os logs acima para mais detalhes."
    exit 1
fi