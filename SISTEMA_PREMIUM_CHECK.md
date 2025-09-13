# 🔐 Sistema de Verificação Premium - AuthPlugin

## 🎯 Como Funciona Agora

### ✅ Verificação via API Mojang
O plugin agora verifica se uma conta é premium consultando a API da Mojang, simulando o comportamento do `online-mode=true`.

### 🔍 Processo de Verificação:

1. **Jogador entra no servidor** (com `online-mode=false`)
2. **Plugin consulta API Mojang** para verificar se a conta existe
3. **Se a conta existir** = PREMIUM (autentica automaticamente)
4. **Se a conta não existir** = PIRATA (precisa se registrar)

## 📋 Logs do Sistema

### Conta Premium:
```
[INFO] 🔍 Verificando conta original para: PlayerName (uuid)
[INFO] 🔍 Verificando se 'PlayerName' é conta premium...
[INFO] ✅ Conta 'PlayerName' encontrada na API Mojang - PREMIUM
[INFO] ✅ Conta PREMIUM detectada via API Mojang: PlayerName
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
```

### Conta Pirata:
```
[INFO] 🔍 Verificando conta original para: PlayerName (uuid)
[INFO] 🔍 Verificando se 'PlayerName' é conta premium...
[INFO] ❌ Conta 'PlayerName' não encontrada na API Mojang - PIRATA
[INFO] ❌ Conta PIRATA detectada (não encontrada na API): PlayerName
[INFO] 🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.
```

## 🚀 Vantagens do Novo Sistema

1. **Precisão 100%** - Consulta diretamente a API da Mojang
2. **Simula online-mode=true** - Mesmo comportamento que servidor premium
3. **Funciona com online-mode=false** - Permite contas piratas
4. **Verificação assíncrona** - Não trava o servidor
5. **Proteção de nicks** - Nicks premium são protegidos

## 🔧 Configuração

### server.properties
```properties
online-mode=false
```

### Por que online-mode=false?
- Permite contas piratas entrarem
- O plugin verifica via API se é premium
- Sistema híbrido: premium + pirata

## 🎯 Teste

1. **Sua conta premium** - Deve aparecer "✅ Conta PREMIUM detectada!"
2. **Conta pirata** - Deve aparecer "❌ Conta PIRATA detectada"
3. **Nick premium com conta pirata** - Deve ser kickado

## 🚀 Instalação

1. **Compile o plugin**:
   ```cmd
   build-premium-check.bat
   ```

2. **Configure o servidor**:
   - `online-mode=false` no `server.properties`

3. **Instale o plugin**:
   - Copie `target/AuthPlugin-1.0.0.jar` para `plugins/`

4. **Reinicie o servidor**

## 🔍 Verificação de Funcionamento

### Logs Esperados para Conta Premium:
```
[INFO] 🔍 Verificando conta original para: LukinhaPvP (uuid)
[INFO] 🔍 Verificando se 'LukinhaPvP' é conta premium...
[INFO] ✅ Conta 'LukinhaPvP' encontrada na API Mojang - PREMIUM
[INFO] ✅ Conta PREMIUM detectada via API Mojang: LukinhaPvP
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
```

### Se Ainda Não Funcionar:
1. Verifique se sua conta existe na API Mojang
2. Teste com outro nome de conta premium
3. Verifique logs do servidor para erros

## ⚠️ Importante

- **API Mojang** - Pode ter rate limits
- **Conexão** - Precisa de internet para verificar
- **Timeout** - 5 segundos para cada verificação
- **Cache** - Nomes premium são salvos no banco de dados