# Instruções de Compilação - AuthPlugin

## ✅ Erros Corrigidos

Os erros de compilação foram corrigidos:
1. **Import UUID faltando** - Adicionado `import java.util.UUID;` no PlayerJoinListener
2. **Método setCancelled inexistente** - Removido uso de `setCancelled()` no PlayerDeathEvent

## 🚀 Como Compilar

### Opção 1: Usando o script batch (Windows)
```cmd
build.bat
```

### Opção 2: Comando Maven direto
```cmd
mvn clean package
```

### Opção 3: Usando o Maven que você já tem
```cmd
.\apache-maven-3.9.6\bin\mvn.cmd clean package
```

## 📁 Arquivo Gerado

Após a compilação bem-sucedida, o arquivo JAR estará em:
```
target/AuthPlugin-1.0.0.jar
```

## 🔧 Instalação no Servidor

1. Copie `target/AuthPlugin-1.0.0.jar` para a pasta `plugins/` do seu servidor
2. Configure `online-mode=false` no `server.properties`
3. Reinicie o servidor

## 🐛 Debug de Contas Originais

Se você tem uma conta original e está sendo pedido para registrar:

1. **Verifique os logs do servidor** - Procure por mensagens como:
   - `"Iniciando verificação de conta original para: [seu_nome]"`
   - `"Servidor em online-mode: true/false"`
   - `"Conta original detectada: true/false"`

2. **Se o servidor estiver em online-mode=true** - Contas originais são detectadas automaticamente

3. **Se o servidor estiver em online-mode=false** - O plugin verifica via API da Mojang

## 📋 Funcionalidades do Plugin

- ✅ **Contas Originais**: Autenticação automática
- ✅ **Contas Piratas**: Sistema de registro/login
- ✅ **Proteção de Nicks**: Impede uso de nicks de contas originais
- ✅ **Comandos**: `/register`, `/login`, `/changepassword`, `/auth`
- ✅ **Restrições**: Jogadores não autenticados têm limitações
- ✅ **Logs Detalhados**: Para debug e diagnóstico

## 🔍 Comandos Disponíveis

- `/register <senha> <confirmar_senha>` - Registra conta pirata
- `/login <senha>` - Faz login
- `/changepassword <senha_atual> <nova_senha>` - Altera senha
- `/auth help` - Mostra ajuda

## ⚠️ Importante

- O plugin funciona com `online-mode=false`
- Contas originais são detectadas automaticamente
- Contas piratas precisam se registrar primeiro
- Sistema impede uso de nicks de contas originais por piratas