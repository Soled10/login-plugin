# 🎯 AuthPlugin - Sistema Simplificado

## 📋 Descrição

Sistema de autenticação simplificado que armazena apenas **nome de usuário** e **senha** no banco de dados, sem verificações de contas originais ou armazenamento de UUIDs oficiais.

## 🗄️ Banco de Dados

### Tabela `players`
```sql
CREATE TABLE players (
    player_name VARCHAR(16) PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(32) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Campos:**
- `player_name`: Nome do jogador (chave primária)
- `password`: Senha criptografada (SHA-256 + salt)
- `salt`: Salt aleatório para criptografia
- `registered_at`: Data de registro

## 🔧 Funcionalidades

### ✅ Comandos Disponíveis

1. **`/register <senha> <confirmar_senha>`**
   - Registra uma nova conta
   - Valida se as senhas coincidem
   - Autentica automaticamente após registro

2. **`/login <senha>`**
   - Faz login na conta
   - Verifica senha no banco de dados
   - Remove restrições após login

3. **`/changepassword <senha_atual> <nova_senha>`**
   - Altera a senha da conta
   - Verifica senha atual antes de alterar

4. **`/auth <subcomando>`**
   - Comando principal com subcomandos
   - `/auth register`, `/auth login`, `/auth changepassword`, `/auth help`

### 🔒 Sistema de Segurança

- **Criptografia**: SHA-256 com salt aleatório
- **Validação**: Senhas de 6-32 caracteres
- **Restrições**: Jogadores não autenticados ficam limitados
- **Banco de dados**: SQLite para persistência

## 🎮 Como Funciona

### 1. **Entrada no Servidor**
- Jogador entra no servidor
- Sistema aplica restrições (modo adventure, lentidão, cegueira)
- Verifica se o nome está registrado no banco

### 2. **Registro de Nova Conta**
```
/register minhasenha123 minhasenha123
```
- Valida se as senhas coincidem
- Verifica se o nome já está registrado
- Criptografa a senha com salt
- Salva no banco de dados
- Autentica automaticamente

### 3. **Login em Conta Existente**
```
/login minhasenha123
```
- Verifica se o nome está registrado
- Compara senha criptografada
- Autentica se a senha estiver correta
- Remove restrições

### 4. **Alteração de Senha**
```
/changepassword senhaantiga novasenha123
```
- Verifica se está autenticado
- Valida senha atual
- Criptografa nova senha
- Atualiza no banco de dados

## 📊 Logs do Sistema

### Registro Bem-sucedido
```
[INFO] Conta registrada com sucesso!
[INFO] Você foi autenticado automaticamente.
```

### Login Bem-sucedido
```
[INFO] Login realizado com sucesso!
```

### Erro de Senha
```
[ERROR] Senha incorreta!
```

### Nome Já Registrado
```
[ERROR] Este nome já está registrado! Use /login para fazer login.
```

## 🚀 Instalação

### 1. **Compilar o Plugin**
```bash
.\build-simples.bat
```

### 2. **Instalar no Servidor**
```bash
# Copiar para pasta plugins
cp target/AuthPlugin-1.0.0.jar plugins/

# Reiniciar servidor
```

### 3. **Testar o Sistema**
```bash
# Entrar no servidor
# Usar comando de registro
/register minhasenha123 minhasenha123

# Sair e entrar novamente
# Usar comando de login
/login minhasenha123
```

## 🎯 Vantagens do Sistema Simplificado

### ✅ **Simplicidade**
- Apenas nome e senha no banco
- Sem verificações complexas
- Código mais limpo e fácil de manter

### ✅ **Compatibilidade**
- Funciona com qualquer tipo de conta
- Não depende de APIs externas
- Funciona em servidores offline

### ✅ **Performance**
- Menos consultas ao banco
- Sem chamadas para APIs externas
- Resposta mais rápida

### ✅ **Confiabilidade**
- Menos pontos de falha
- Sistema mais estável
- Fácil de debugar

## 🔧 Configuração

### Banco de Dados
- **Tipo**: SQLite
- **Localização**: `plugins/AuthPlugin/auth.db`
- **Criação**: Automática na primeira execução

### Criptografia
- **Algoritmo**: SHA-256
- **Salt**: 16 bytes aleatórios
- **Formato**: Hexadecimal

### Validações
- **Senha mínima**: 6 caracteres
- **Senha máxima**: 32 caracteres
- **Nome único**: Não permite duplicatas

## 🧪 Testes

### Teste 1: Registro
1. Entrar no servidor
2. Usar `/register teste123 teste123`
3. Verificar se foi autenticado automaticamente

### Teste 2: Login
1. Sair do servidor
2. Entrar novamente
3. Usar `/login teste123`
4. Verificar se foi autenticado

### Teste 3: Alteração de Senha
1. Estar autenticado
2. Usar `/changepassword teste123 novasenha456`
3. Sair e entrar novamente
4. Usar `/login novasenha456`
5. Verificar se funcionou

## ⚠️ Limitações

- **Sem proteção de nomes**: Qualquer um pode usar qualquer nome
- **Sem verificação de contas originais**: Não diferencia contas premium
- **Sem backup automático**: Banco de dados não é copiado automaticamente

## 🎉 Conclusão

O sistema simplificado oferece:
- **Funcionalidade básica** de autenticação
- **Simplicidade** no código e uso
- **Confiabilidade** e estabilidade
- **Compatibilidade** com qualquer servidor

Perfeito para servidores que precisam apenas de um sistema básico de login/registro sem complexidades adicionais.