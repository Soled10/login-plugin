# 🔐 Sistema de Autenticação por UUID - AuthPlugin

## 🎯 Como Funciona

### ✅ Contas PREMIUM (Originais)
- **UUID v4** (modo online)
- **Autenticação automática** - Entram direto no servidor
- **Nicks protegidos** - Outros jogadores não podem usar
- **Mensagem**: "✅ Conta PREMIUM detectada! Você foi autenticado automaticamente."

### 🔓 Contas PIRATAS (Offline)
- **UUID v3** (modo offline)
- **Sistema de registro/login** - Precisam se registrar primeiro
- **Nicks livres** - Podem usar qualquer nick (exceto de contas premium)
- **Mensagem**: "🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar."

## 🛡️ Proteção de Nicks Premium

Se um jogador pirata tentar usar um nick de conta premium:
```
❌ Este nick pertence a uma conta PREMIUM!
Você não pode usar este nome.
🔓 Use outro nickname para jogar no servidor.
```

## 🚀 Configuração do Servidor

### server.properties
```properties
online-mode=false
```

### Por que online-mode=false?
- Permite contas piratas entrarem
- O plugin detecta automaticamente se é premium ou pirata pelo UUID
- Sistema mais eficiente que verificação via API

## 📋 Comandos Disponíveis

### Para Contas Piratas:
- `/register <senha> <confirmar_senha>` - Registra conta
- `/login <senha>` - Faz login
- `/changepassword <senha_atual> <nova_senha>` - Altera senha
- `/auth help` - Mostra ajuda

### Para Contas Premium:
- **Nenhum comando necessário** - Autenticação automática

## 🔍 Logs do Sistema

### Conta Premium:
```
[INFO] Verificando conta original para: PlayerName (uuid-v4)
[INFO] ✅ Conta ORIGINAL detectada (UUID online): PlayerName
[INFO] ✅ Conta PREMIUM detectada! Você foi autenticado automaticamente.
```

### Conta Pirata:
```
[INFO] Verificando conta original para: PlayerName (uuid-v3)
[INFO] ❌ Conta PIRATA detectada (UUID offline): PlayerName
[INFO] 🔓 Bem-vindo! Use /register <senha> <confirmar_senha> para se registrar.
```

## 🎯 Vantagens do Sistema

1. **Rápido** - Não precisa verificar API da Mojang
2. **Confiável** - UUID v4 vs v3 é 100% preciso
3. **Seguro** - Protege nicks de contas premium
4. **Simples** - Contas premium entram automaticamente
5. **Flexível** - Contas piratas podem se registrar

## 🚀 Instalação

1. **Compile o plugin**:
   ```cmd
   build-optimized.bat
   ```

2. **Configure o servidor**:
   - `online-mode=false` no `server.properties`

3. **Instale o plugin**:
   - Copie `target/AuthPlugin-1.0.0.jar` para `plugins/`

4. **Reinicie o servidor**

## ✅ Teste

1. **Conta Premium**: Entre com sua conta original - deve autenticar automaticamente
2. **Conta Pirata**: Entre com conta pirata - deve pedir para registrar
3. **Proteção**: Tente usar nick de conta premium com conta pirata - deve ser kickado

## 🔧 Troubleshooting

### Se conta premium não autenticar:
- Verifique se o UUID é v4 (premium)
- Verifique logs do servidor

### Se conta pirata não conseguir registrar:
- Verifique se o nick não pertence a conta premium
- Use outro nickname

### Se plugin não carregar:
- Verifique se `online-mode=false`
- Verifique se o JAR está correto