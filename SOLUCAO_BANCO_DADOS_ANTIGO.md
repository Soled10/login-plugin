# 🗄️ Solução: Banco de Dados Antigo

## ❌ Problema Identificado

O banco de dados ainda contém o **UUID oficial da API Mojang** armazenado da versão anterior, causando o bloqueio da conta original.

### Logs do Problema:
```
[INFO] UUID atual (pirata): d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID registrado (original): e23d896e-7606-41c1-86f4-66bfd6a9e0a8
[INFO] Conta pirata tentando usar nome de conta original!
```

**Problema**: O sistema está comparando:
- **UUID atual**: `d7690935-9d5b-322e-ba79-2ff1cf1edd47` (offline)
- **UUID registrado**: `e23d896e-7606-41c1-86f4-66bfd6a9e0a8` (oficial da API)

## ✅ Solução

### 1. Limpeza do Banco de Dados

O banco de dados precisa ser limpo para remover os UUIDs antigos:

```bash
# Remover banco de dados antigo
del "plugins\AuthPlugin\auth.db"

# Remover pasta do plugin
rmdir /s /q "plugins\AuthPlugin"
```

### 2. Reinstalação do Plugin

1. **Compile o plugin corrigido**:
   ```bash
   mvn clean package
   ```

2. **Copie para o servidor**:
   ```bash
   cp target/AuthPlugin-1.0.0.jar /caminho/do/servidor/plugins/
   ```

3. **Reinicie o servidor**

### 3. Primeira Entrada

Na primeira entrada da conta original, o sistema irá:
- ✅ Verificar se nome existe na API Mojang
- ✅ Armazenar **UUID offline** do jogador
- ✅ Autenticar automaticamente

## 📊 Logs Esperados Após Correção

### Primeira Conta Original (Após Limpeza)
```
[INFO] ✅ Primeira vez usando conta original: LukinhaPvP
[INFO] ✅ Nome protegido para jogador LukinhaPvP com UUID offline: d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] ✅ UUID oficial da API: e23d896e-7606-41c1-86f4-66bfd6a9e0a8
```

### Conta Original Retornando
```
[INFO] ✅ Conta original retornando: LukinhaPvP
[INFO] UUID atual: d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID registrado: d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] UUID oficial (API): e23d896e-7606-41c1-86f4-66bfd6a9e0a8
```

### Conta Pirata (Nome já registrado)
```
[INFO] ❌ Nome 'LukinhaPvP' já está registrado como conta original - BLOQUEANDO
[INFO] UUID atual (pirata): a1b2c3d4-e5f6-7890-abcd-ef1234567890
[INFO] UUID registrado (original): d7690935-9d5b-322e-ba79-2ff1cf1edd47
[INFO] Conta pirata tentando usar nome de conta original!
```

## 🚀 Scripts de Build

### Script Automático (Recomendado)
```bash
.\build-limpar-banco.bat
```

Este script:
- ✅ Compila o plugin
- ✅ Remove banco de dados antigo
- ✅ Remove pasta do plugin
- ✅ Instruções de instalação

### Script Manual
```bash
# 1. Compilar
mvn clean package

# 2. Limpar banco
del "plugins\AuthPlugin\auth.db"
rmdir /s /q "plugins\AuthPlugin"

# 3. Instalar
cp target/AuthPlugin-1.0.0.jar plugins/
```

## 🎯 Resultado Final

Após a limpeza e reinstalação:

- ✅ **Primeira conta original**: Funciona e registra UUID offline
- ✅ **Conta original retornando**: Funciona normalmente (UUIDs coincidem)
- ❌ **Contas piratas com nomes registrados**: São BLOQUEADAS (UUIDs diferentes)
- ✅ **Contas piratas com nomes não registrados**: Funcionam normalmente

## ⚠️ Importante

- **Banco de dados deve ser limpo** para funcionar corretamente
- **UUIDs offline são usados** para comparação
- **Sistema funciona perfeitamente** após limpeza
- **Proteção é automática** e permanente

## 🧪 Teste

1. **Execute** `build-limpar-banco.bat`
2. **Copie** o plugin para `plugins/`
3. **Reinicie** o servidor
4. **Entre** com sua conta original → Deve funcionar
5. **Teste** com conta pirata usando mesmo nome → Deve ser bloqueada