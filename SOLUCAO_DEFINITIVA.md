# 🔧 Solução Definitiva - Plugin.yml não encontrado

## ❌ Problema
O arquivo JAR não contém o `plugin.yml`, causando erro no servidor.

## ✅ Solução Completa

### Passo 1: Limpeza Total
```cmd
cleanup.bat
```

### Passo 2: Recompilar com Nova Configuração
```cmd
build.bat
```

### Passo 3: Verificar se Funcionou
```cmd
test-jar.bat
```

## 🔍 O que foi corrigido:

1. **POM.xml atualizado** - Configuração correta para incluir recursos
2. **plugin.yml movido** - Para `src/main/resources/`
3. **config.yml movido** - Para `src/main/resources/`
4. **Maven Resources Plugin** - Adicionado para copiar arquivos corretamente
5. **Script de teste** - Verifica se o JAR está correto

## 📁 Estrutura Correta

```
src/main/resources/
├── plugin.yml
└── config.yml
```

## 🚀 Comandos Rápidos

```cmd
# Solução completa
cleanup.bat && build.bat && test-jar.bat
```

## ✅ Verificação Final

Após executar os comandos, você deve ver:
- ✅ plugin.yml encontrado no JAR!
- ✅ config.yml encontrado no JAR!

## 🎯 Instalação

1. Copie `target/AuthPlugin-1.0.0.jar` para `plugins/`
2. Configure `online-mode=false` no `server.properties`
3. Reinicie o servidor

## 🐛 Se Ainda Der Erro

### Verificar Conteúdo do JAR
```cmd
jar -tf target\AuthPlugin-1.0.0.jar
```

### Verificar plugin.yml
```cmd
jar -xf target\AuthPlugin-1.0.0.jar plugin.yml
type plugin.yml
```

### Recompilar do Zero
```cmd
rmdir /s /q target
rmdir /s /q src\main\resources
mkdir src\main\resources
copy plugin.yml src\main\resources\
copy config.yml src\main\resources\
mvn clean package
```

## 📋 Checklist

- [ ] Executeu `cleanup.bat`
- [ ] Executeu `build.bat`
- [ ] Executeu `test-jar.bat`
- [ ] Viu "plugin.yml encontrado no JAR!"
- [ ] Copiou apenas `AuthPlugin-1.0.0.jar` para `plugins/`
- [ ] Servidor em `online-mode=false`