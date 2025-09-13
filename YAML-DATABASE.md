# 📄 Sistema de Banco de Dados YAML - AuthPlugin

## 🎯 Visão Geral

O **AuthPlugin** utiliza arquivos **YAML** como banco de dados, oferecendo:
- ✅ **Dados legíveis** - Você pode visualizar e editar manualmente
- ✅ **Backup simples** - Basta copiar os arquivos .yml
- ✅ **Performance excelente** - Carregamento rápido e baixo uso de memória
- ✅ **Portabilidade** - Funciona em qualquer sistema operacional
- ✅ **Sem dependências externas** - Usa apenas recursos nativos do Bukkit

## 📁 Arquivos Gerados

O plugin cria automaticamente dois arquivos principais:

### 1. `players.yml` - Dados dos Jogadores
Contém todas as informações dos jogadores registrados:
```yaml
# Jogador premium (Minecraft original)
notch:
  password: $2a$10$N9qo8uLOickgx2ZMRZoMye.IjPeGRnL...
  uuid: 069a79f4-44e9-4726-a5be-fca90e38aaf5
  premium: true
  last-login: 1694647200000
  registration-date: 1694640000000
  last-ip: 192.168.1.100
  login-attempts: 0

# Jogador pirata
steve_pirata:
  password: $2a$10$abcdefghijklmnopqrstuvwxyz123456...
  uuid: null
  premium: false
  last-login: 1694647100000
  registration-date: 1694640000000
  last-ip: 192.168.1.101
  login-attempts: 0
```

### 2. `sessions.yml` - Sessões Ativas
Controla as sessões de jogadores logados:
```yaml
# Sessão ativa de jogador premium
notch:
  uuid: 069a79f4-44e9-4726-a5be-fca90e38aaf5
  ip-address: 192.168.1.100
  login-time: 1694647200000
  expires-at: 1694647500000

# Sessão ativa de jogador pirata
steve_pirata:
  uuid: null
  ip-address: 192.168.1.101
  login-time: 1694647100000
  expires-at: 1694647400000
```

## 🔧 Campos Explicados

### Arquivo `players.yml`:
| Campo | Descrição | Exemplo |
|-------|-----------|---------|
| `password` | Hash BCrypt da senha | `$2a$10$N9qo8uLO...` |
| `uuid` | UUID do jogador (null para piratas) | `069a79f4-44e9-4726...` |
| `premium` | Se é conta premium | `true` / `false` |
| `last-login` | Último login (timestamp) | `1694647200000` |
| `registration-date` | Data de registro (timestamp) | `1694640000000` |
| `last-ip` | Último IP usado | `192.168.1.100` |
| `login-attempts` | Tentativas de login incorretas | `0` |

### Arquivo `sessions.yml`:
| Campo | Descrição | Exemplo |
|-------|-----------|---------|
| `uuid` | UUID do jogador | `069a79f4-44e9-4726...` |
| `ip-address` | IP da sessão | `192.168.1.100` |
| `login-time` | Quando fez login (timestamp) | `1694647200000` |
| `expires-at` | Quando expira (timestamp) | `1694647500000` |

## 🛠️ Gerenciamento Manual

### ✅ **Editar Dados Manualmente**
Você pode editar os arquivos YAML diretamente:
1. Pare o servidor
2. Edite os arquivos com qualquer editor de texto
3. Inicie o servidor

### 🗑️ **Remover Jogador**
Para remover um jogador completamente:
1. Remova a seção do jogador de `players.yml`
2. Remova a seção do jogador de `sessions.yml` (se existir)
3. Use `/authreload` no servidor

### 🔄 **Backup e Restauração**
```bash
# Fazer backup
cp plugins/AuthPlugin/players.yml backup/players_backup.yml
cp plugins/AuthPlugin/sessions.yml backup/sessions_backup.yml

# Restaurar backup
cp backup/players_backup.yml plugins/AuthPlugin/players.yml
cp backup/sessions_backup.yml plugins/AuthPlugin/sessions.yml
```

### 🔧 **Resetar Tentativas de Login**
Se um jogador está bloqueado por muitas tentativas:
```yaml
jogador:
  # ... outros campos ...
  login-attempts: 0  # Resetar para 0
```

## 📊 Vantagens vs SQLite

| Aspecto | YAML | SQLite |
|---------|------|--------|
| **Legibilidade** | ✅ Totalmente legível | ❌ Binário |
| **Edição Manual** | ✅ Qualquer editor | ❌ Precisa de ferramentas |
| **Backup** | ✅ Copiar arquivo | ❌ Dump necessário |
| **Portabilidade** | ✅ Total | ✅ Boa |
| **Performance** | ✅ Rápido para poucos dados | ✅ Melhor para muitos dados |
| **Tamanho** | ✅ Menor (130 KB) | ❌ Maior (13 MB) |
| **Dependências** | ✅ Nenhuma | ❌ Driver SQLite |

## 🔒 Segurança

### **Senhas Criptografadas**
- Todas as senhas são armazenadas com hash **BCrypt**
- Impossível recuperar a senha original
- Cada hash é único (salt aleatório)

### **Exemplo de Hash BCrypt:**
```
Senha original: "minhasenha123"
Hash armazenado: "$2a$10$N9qo8uLOickgx2ZMRZoMye.IjPeGRnLYjqo.FZhLp1234567890"
```

### **Proteções Implementadas:**
- ✅ Senhas nunca em texto plano
- ✅ UUIDs verificados via API Mojang
- ✅ Sessões com expiração automática
- ✅ Limite de tentativas de login
- ✅ IPs registrados para auditoria

## 🔄 Migração de Outros Plugins

### **Do AuthMe (MySQL/SQLite)**
1. Exporte dados do AuthMe
2. Converta para formato YAML
3. Importe para `players.yml`
4. Execute `/authreload`

### **Exemplo de Script de Conversão:**
```python
# Script básico para converter dados
import yaml
import sqlite3

# Conectar ao banco do AuthMe
conn = sqlite3.connect('authme.db')
cursor = conn.execute('SELECT * FROM authme')

players = {}
for row in cursor:
    username, password, ip, lastlogin, regdate = row
    players[username.lower()] = {
        'password': password,
        'uuid': None,  # Será detectado automaticamente
        'premium': False,
        'last-login': lastlogin,
        'registration-date': regdate,
        'last-ip': ip,
        'login-attempts': 0
    }

# Salvar em YAML
with open('players.yml', 'w') as f:
    yaml.dump(players, f, default_flow_style=False)
```

## 📈 Performance e Limites

### **Recomendações de Uso:**
- ✅ **Ideal para**: Até 10.000 jogadores
- ✅ **Ótimo para**: Servidores pequenos/médios
- ⚠️ **Considerar SQLite para**: Mais de 50.000 jogadores

### **Otimizações Automáticas:**
- Cache em memória para dados frequentes
- Limpeza automática de sessões expiradas
- Carregamento lazy dos dados
- Salvamento assíncrono

## 🛡️ Troubleshooting

### **Arquivo Corrompido:**
```bash
# Verificar sintaxe YAML
python -c "import yaml; yaml.safe_load(open('players.yml'))"

# Ou usar ferramenta online
# https://yamlchecker.com/
```

### **Permissões de Arquivo:**
```bash
# Linux/Mac
chmod 644 plugins/AuthPlugin/*.yml
chown minecraft:minecraft plugins/AuthPlugin/*.yml

# Windows
# Clique direito > Propriedades > Segurança
```

### **Backup Automático:**
Crie um script para backup automático:
```bash
#!/bin/bash
# backup-auth.sh
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p backups/
cp plugins/AuthPlugin/players.yml backups/players_$DATE.yml
cp plugins/AuthPlugin/sessions.yml backups/sessions_$DATE.yml
echo "Backup criado: $DATE"
```

## 🎉 Conclusão

O sistema YAML do **AuthPlugin** oferece:
- **Simplicidade** - Fácil de entender e gerenciar
- **Flexibilidade** - Edição manual quando necessário
- **Confiabilidade** - Backup e restauração simples
- **Performance** - Rápido e eficiente para a maioria dos casos

Perfeito para servidores que valorizam **transparência** e **controle total** sobre seus dados!

---

**💡 Dica**: Use `/authreload` após editar manualmente os arquivos YAML para recarregar os dados no servidor.