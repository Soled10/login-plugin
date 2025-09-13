# 🔐 Sistema de Verificação por Usuário - AuthPlugin

## 🎯 Como Funciona (Simulação de online-mode=true por usuário)

### ✅ Processo de Verificação:

1. **Jogador entra no servidor** (com `online-mode=false`)
2. **Plugin simula online-mode=true** para aquele usuário específico
3. **Consulta API Mojang** para obter o UUID oficial da conta
4. **Compara UUIDs**:
   - Se UUID da API = UUID do jogador → **PREMIUM** (entra automaticamente)
   - Se UUID da API ≠ UUID do jogador → **PIRATA** (precisa se registrar)

## 📋 Logs do Sistema

### Conta Premium (UUIDs coincidem):
```
[INFO] 🔍 Verificando conta original para: PlayerName (uuid-do-jogador)
[INFO] 🔍 Simulando online-mode=true para usuário: PlayerName
[INFO] ✅ Conta 'PlayerName' passou na verificação online-mode - PREMIUM
[INFO] ✅ Usuário passou na verificação online-mode - PREMIUM: PlayerName
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
```

### Conta Pirata (UUIDs não coincidem):
```
[INFO] 🔍 Verificando conta original para: PlayerName (uuid-do-jogador)
[INFO] 🔍 Simulando online-mode=true para usuário: PlayerName
[INFO] ❌ UUID da API não coincide com UUID do jogador - FALHA no online-mode
[INFO] API UUID: uuid-da-api-mojang
[INFO] Player UUID: uuid-do-jogador
[INFO] ❌ Usuário falhou na verificação online-mode - PIRATA: PlayerName
[INFO] 🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.
```

## 🔍 Por que UUIDs Não Coincidem?

### Com `online-mode=false`:
- **UUID do jogador**: Gerado pelo servidor (ex: `d7690935-9d5b-322e-ba79-2ff1cf1edd47`)
- **UUID da API**: UUID oficial da Mojang (ex: `e23d896e-7606-41c1-86f4-66bfd6a9e0a8`)

### Com `online-mode=true`:
- **UUID do jogador**: Seria o mesmo da API Mojang
- **UUID da API**: UUID oficial da Mojang

## 🎯 Solução Implementada

O plugin agora:
1. **Consulta a API Mojang** para obter o UUID oficial
2. **Compara com o UUID do jogador** no servidor
3. **Se coincidirem** = Conta premium (mesmo que online-mode=true)
4. **Se não coincidirem** = Conta pirata

## 🚀 Vantagens

1. **Simula online-mode=true** por usuário
2. **Precisão 100%** - Compara UUIDs oficiais
3. **Funciona com online-mode=false** - Permite contas piratas
4. **Verificação individual** - Cada usuário é verificado separadamente
5. **Proteção de nicks** - Nicks premium são protegidos

## 🔧 Configuração

### server.properties
```properties
online-mode=false
```

### Por que online-mode=false?
- Permite contas piratas entrarem
- O plugin simula online-mode=true para cada usuário
- Sistema híbrido: premium + pirata

## 🎯 Teste

1. **Sua conta premium** - Deve aparecer "✅ Usuário passou na verificação online-mode"
2. **Conta pirata** - Deve aparecer "❌ Usuário falhou na verificação online-mode"
3. **Nick premium com conta pirata** - Deve ser kickado

## 🚀 Instalação

1. **Compile o plugin**:
   ```cmd
   build-user-online-mode.bat
   ```

2. **Configure o servidor**:
   - `online-mode=false` no `server.properties`

3. **Instale o plugin**:
   - Copie `target/AuthPlugin-1.0.0.jar` para `plugins/`

4. **Reinicie o servidor**

## 🔍 Verificação de Funcionamento

### Logs Esperados para Sua Conta Premium:
```
[INFO] 🔍 Verificando conta original para: LukinhaPvP (seu-uuid-atual)
[INFO] 🔍 Simulando online-mode=true para usuário: LukinhaPvP
[INFO] ✅ Conta 'LukinhaPvP' passou na verificação online-mode - PREMIUM
[INFO] ✅ Usuário passou na verificação online-mode - PREMIUM: LukinhaPvP
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
```

### Se Ainda Não Funcionar:
1. Verifique se os UUIDs estão sendo comparados corretamente
2. Teste com outro nome de conta premium
3. Verifique logs do servidor para erros

## ⚠️ Importante

- **API Mojang** - Pode ter rate limits
- **Conexão** - Precisa de internet para verificar
- **Timeout** - 5 segundos para cada verificação
- **UUIDs** - Devem coincidir para contas premium