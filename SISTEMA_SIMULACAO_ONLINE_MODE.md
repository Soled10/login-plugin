# 🎮 Sistema de Simulação Online-Mode

## 📋 Como Funciona

O sistema simula `online-mode=true` **apenas para usuários específicos**, não globalmente. Isso permite que o servidor funcione em `online-mode=false` mas ainda identifique contas originais (premium) vs contas piratas.

### 🔄 Fluxo de Autenticação

1. **Usuário entra no servidor** (online-mode=false)
2. **Sistema verifica se o nome existe na API Mojang**
   - Se existir → Usuário é **PREMIUM** (autenticado automaticamente)
   - Se não existir → Usuário é **PIRATA** (precisa registrar)
3. **Proteção de nomes**: Nomes de contas originais ficam protegidos automaticamente

### ✅ Contas Originais (Premium)

- **Verificação**: Nome existe na API Mojang
- **Resultado**: Autenticado automaticamente
- **UUID**: Associa o UUID oficial da API
- **Proteção**: Nome fica protegido contra uso por contas piratas

### ❌ Contas Piratas

- **Verificação**: Nome não existe na API Mojang
- **Resultado**: Precisa registrar com `/register <senha> <confirmar_senha>`
- **Login**: Usa `/login <senha>` para autenticar
- **Proteção**: Não pode usar nomes de contas originais já registradas

## 🛡️ Sistema de Proteção

### Proteção de Nomes Originais

1. **Primeira entrada**: Conta original registra o nome automaticamente
2. **Tentativas subsequentes**: Contas piratas são bloqueadas se tentarem usar o nome
3. **Mensagem de erro**: "Este nick pertence a uma conta PREMIUM!"

### Exemplo de Funcionamento

```
1. LukinhaPvP (conta original) entra → ✅ Autenticado automaticamente
2. LukinhaPvP (conta pirata) tenta entrar → ❌ BLOQUEADO
3. Player123 (conta pirata) entra → ✅ Pode registrar normalmente
```

## 📊 Logs do Sistema

### Conta Original (Primeira vez)
```
[INFO] 🔍 Simulando online-mode=true para: LukinhaPvP
[INFO] Resposta da API Mojang: 200 para LukinhaPvP
[INFO] ✅ Simulando online-mode=true para usuário: LukinhaPvP
[INFO] ✅ Conta existe na API Mojang - USUÁRIO É PREMIUM
[INFO] UUID atual (offline): d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID oficial (API): e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] Associando UUID oficial ao jogador: LukinhaPvP
```

### Conta Pirata (Nome já registrado)
```
[INFO] 🔍 Simulando online-mode=true para: LukinhaPvP
[INFO] Resposta da API Mojang: 200 para LukinhaPvP
[INFO] ✅ Simulando online-mode=true para usuário: LukinhaPvP
[INFO] ✅ Conta existe na API Mojang - USUÁRIO É PREMIUM
[INFO] UUID atual (offline): a1b2c3d4-e5f6-7890-abcd-ef1234567890
[INFO] UUID oficial (API): e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] ❌ Conta pirata tentando usar nome de conta original: LukinhaPvP
```

### Conta Pirata (Nome não registrado)
```
[INFO] 🔍 Simulando online-mode=true para: Player123
[INFO] Resposta da API Mojang: 204 para Player123
[INFO] ❌ Conta 'Player123' não existe na API Mojang - FALHA no online-mode
```

## 🔧 Vantagens do Sistema

1. **Flexibilidade**: Servidor pode funcionar em online-mode=false
2. **Segurança**: Contas originais são identificadas automaticamente
3. **Proteção**: Nomes de contas originais ficam protegidos
4. **Compatibilidade**: Funciona com qualquer cliente Minecraft
5. **Simplicidade**: Usuários não precisam fazer nada especial

## 🚀 Instalação

1. **Compile o plugin**:
   ```bash
   mvn clean package
   ```

2. **Copie para o servidor**:
   ```bash
   cp target/AuthPlugin-1.0.0.jar /caminho/do/servidor/plugins/
   ```

3. **Reinicie o servidor**

4. **Teste**:
   - Entre com uma conta original → Deve ser autenticado automaticamente
   - Entre com uma conta pirata → Deve pedir para registrar
   - Tente usar nome de conta original com conta pirata → Deve ser bloqueado

## ⚠️ Importante

- **UUIDs sempre serão diferentes** (online-mode=false)
- **Verificação é feita apenas pela existência na API Mojang**
- **Sistema funciona perfeitamente com online-mode=false**
- **Primeira conta original que entrar será registrada automaticamente**
- **Contas piratas subsequentes com o mesmo nome serão bloqueadas**

## 🎯 Resultado Final

- ✅ **Contas originais**: Funcionam normalmente na primeira entrada
- ❌ **Contas piratas com nomes registrados**: São bloqueadas
- ✅ **Contas piratas com nomes não registrados**: Funcionam normalmente
- 🔒 **Proteção**: Nomes de contas originais ficam protegidos automaticamente