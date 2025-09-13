# 🔐 Sistema de Associação de UUID - AuthPlugin

## 🎯 Como Funciona Agora

### ✅ Processo Completo:

1. **Jogador entra no servidor** (com `online-mode=false`)
2. **Plugin verifica se o nick é de conta original** via API Mojang
3. **Se for conta original**:
   - Simula `online-mode=true` para aquele usuário
   - Se passar, **associa o UUID oficial da API Mojang**
   - Salva o UUID oficial no banco de dados
   - Libera o usuário para o servidor
4. **Se não for conta original**:
   - Considera como conta pirata
   - Precisa se registrar

## 📋 Logs do Sistema

### Conta Premium (com associação de UUID):
```
[INFO] 🔍 Verificando conta original para: LukinhaPvP (uuid-local)
[INFO] 🔍 Simulando online-mode=true para: LukinhaPvP
[INFO] ✅ Conta 'LukinhaPvP' passou na verificação online-mode
[INFO] UUID atual: d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID oficial: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] Associando UUID oficial ao jogador: LukinhaPvP
[INFO] ✅ Usuário passou na verificação online-mode - PREMIUM: LukinhaPvP
[INFO] ✅ UUID oficial associado ao jogador LukinhaPvP: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
[INFO] 🔗 UUID oficial associado: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
```

### Conta Pirata:
```
[INFO] 🔍 Verificando conta original para: PlayerName (uuid-local)
[INFO] 🔍 Simulando online-mode=true para: PlayerName
[INFO] ❌ Conta 'PlayerName' não existe na API Mojang - FALHA no online-mode
[INFO] ❌ Usuário falhou na verificação online-mode - PIRATA: PlayerName
[INFO] 🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.
```

## 🔗 Associação de UUID

### O que acontece:
1. **UUID Local**: `d7690935-9d5b-322e-ba79-2ff1cf1edd47` (gerado pelo servidor offline)
2. **UUID Oficial**: `e23d896e-7606-41c1-86f4-66bfd6a9e0a8` (da API Mojang)
3. **Associação**: O UUID oficial é associado ao jogador e salvo no banco

### Vantagens:
- **Proteção precisa**: Nicks são protegidos com UUID oficial
- **Identificação única**: Cada conta premium tem seu UUID oficial
- **Compatibilidade**: Funciona com `online-mode=false`

## 🛡️ Proteção de Nicks

### Como funciona:
1. **Conta premium entra** → UUID oficial é salvo no banco
2. **Conta pirata tenta usar mesmo nick** → Plugin verifica no banco
3. **Se UUID oficial existe** → Kicka a conta pirata
4. **Se UUID oficial não existe** → Permite o uso do nick

## 🚀 Vantagens do Sistema

1. **Simulação real** - Simula `online-mode=true` por usuário
2. **Associação de UUID** - Usa UUID oficial da API Mojang
3. **Proteção precisa** - Nicks protegidos com UUID oficial
4. **Compatibilidade** - Funciona com `online-mode=false`
5. **Identificação única** - Cada conta premium tem UUID oficial

## 🔧 Configuração

### server.properties
```properties
online-mode=false
```

### Por que online-mode=false?
- Permite contas piratas entrarem
- O plugin simula `online-mode=true` para contas premium
- Associa UUID oficial da API Mojang

## 🎯 Teste

1. **Sua conta premium** - Deve aparecer "✅ UUID oficial associado"
2. **Conta pirata** - Deve aparecer "❌ Usuário falhou na verificação"
3. **Nick premium com conta pirata** - Deve ser kickado

## 🚀 Instalação

1. **Compile o plugin**:
   ```cmd
   build-uuid-association.bat
   ```

2. **Configure o servidor**:
   - `online-mode=false` no `server.properties`

3. **Instale o plugin**:
   - Copie `target/AuthPlugin-1.0.0.jar` para `plugins/`

4. **Reinicie o servidor**

## 🔍 Verificação de Funcionamento

### Logs Esperados para Sua Conta:
```
[INFO] 🔍 Verificando conta original para: LukinhaPvP (seu-uuid-local)
[INFO] 🔍 Simulando online-mode=true para: LukinhaPvP
[INFO] ✅ Conta 'LukinhaPvP' passou na verificação online-mode
[INFO] UUID atual: d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID oficial: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] Associando UUID oficial ao jogador: LukinhaPvP
[INFO] ✅ UUID oficial associado ao jogador LukinhaPvP: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
[INFO] 🔗 UUID oficial associado: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
```

### Se Ainda Não Funcionar:
1. Verifique se a conta existe na API Mojang
2. Teste com outro nome de conta premium
3. Verifique logs do servidor para erros

## ⚠️ Importante

- **API Mojang** - Pode ter rate limits
- **Conexão** - Precisa de internet para verificar
- **Timeout** - 5 segundos para cada verificação
- **UUID Oficial** - É salvo no banco de dados
- **Proteção** - Nicks são protegidos com UUID oficial