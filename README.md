# AuthPlugin - Plugin de Autenticação para Minecraft

Um plugin completo de autenticação para servidores Minecraft que suporta tanto contas premium (originais) quanto piratas, com proteções avançadas contra uso indevido de nicks premium.

## 🌟 Características

### ✅ Funcionalidades Principais
- **Auto-login para contas premium**: Jogadores com Minecraft original entram automaticamente
- **Sistema de registro para piratas**: Contas não-premium precisam se registrar
- **Proteção contra nick premium**: Impede piratas de usar nomes de contas originais
- **Verificação via API Mojang**: Validação real-time de contas premium
- **Sistema de sessões**: Mantém jogadores logados por um tempo configurável
- **Mudança de senha**: Comando `/changepassword` para alterar senhas
- **Banco de dados SQLite**: Armazenamento seguro com criptografia BCrypt

### 🔒 Segurança
- **Verificação de UUID**: Confirma se o UUID corresponde à conta premium
- **Limite de tentativas**: Proteção contra ataques de força bruta
- **Timeout de login**: Tempo limite para autenticação
- **Bloqueio de ações**: Jogadores não autenticados não podem interagir
- **Cache inteligente**: Reduz chamadas desnecessárias à API Mojang

### ⚙️ Configurável
- **Spawn personalizado**: Teleporte automático para local definido
- **Mensagens customizáveis**: Todas as mensagens podem ser alteradas
- **Timeouts ajustáveis**: Configure tempos limite conforme necessário
- **Proteções opcionais**: Ative/desative recursos conforme preferir

## 📋 Comandos

### Para Jogadores
- `/register <senha> <confirmar-senha>` - Registrar uma conta
- `/login <senha>` - Fazer login
- `/changepassword <senha-atual> <nova-senha> <confirmar-nova-senha>` - Alterar senha

### Para Administradores
- `/authreload` - Recarregar configuração
- `/unregister <jogador>` - Remover registro de um jogador

## 🔧 Instalação

### Pré-requisitos
- **Servidor**: Spigot/Paper 1.8+ (testado até 1.20.1)
- **Java**: 8 ou superior
- **Configuração**: `online-mode=false` no server.properties

### Passos de Instalação

1. **Compilar o plugin**:
   ```bash
   mvn clean package
   ```

2. **Instalar no servidor**:
   - Copie o arquivo `AuthPlugin-1.0.0.jar` da pasta `target/` para `plugins/`
   - Reinicie o servidor

3. **Configurar o servidor**:
   - Defina `online-mode=false` no `server.properties`
   - Reinicie o servidor novamente

4. **Personalizar configuração** (opcional):
   - Edite `plugins/AuthPlugin/config.yml`
   - Use `/authreload` para recarregar

## ⚙️ Configuração

### Exemplo de config.yml
```yaml
security:
  premium-auto-login: true          # Auto-login para contas premium
  check-premium-uuid: true          # Verificar UUID via API Mojang
  block-premium-names: true         # Bloquear piratas usando nicks premium
  session-timeout: 300              # Duração da sessão (segundos)
  max-login-attempts: 3             # Máximo de tentativas de login
  login-timeout: 60                 # Tempo limite para fazer login

database:
  path: "plugins/AuthPlugin/database.db"

spawn:
  teleport-to-spawn: true
  world: "world"
  x: 0
  y: 64
  z: 0
  yaw: 0
  pitch: 0
```

## 🛡️ Como Funciona a Proteção

### Para Contas Premium
1. Jogador entra no servidor
2. Plugin verifica se é conta premium via API Mojang
3. Se for premium e UUID corresponder → **Auto-login**
4. Se for premium mas UUID não corresponder → **Bloqueado**

### Para Contas Piratas
1. Jogador entra no servidor
2. Plugin verifica se o nick pertence a uma conta premium
3. Se pertencer → **Bloqueado** (proteção ativa)
4. Se não pertencer → **Precisa registrar**

### Sistema de Sessões
- Jogadores autenticados ganham uma sessão temporária
- Sessões são válidas por IP e tempo configurável
- Evita necessidade de login constante

## 🔍 Logs e Monitoramento

O plugin registra eventos importantes:
- Registros de novos jogadores
- Tentativas de login (sucessos e falhas)
- Tentativas de uso indevido de nicks premium
- Erros de conexão com API Mojang

## 🚨 Solução de Problemas

### Erro "API Mojang indisponível"
- **Causa**: Conexão com internet ou API Mojang fora do ar
- **Solução**: Plugin funciona em modo offline, assumindo contas como não-premium

### Jogadores premium não conseguem entrar
- **Verifique**: `online-mode=false` no server.properties
- **Verifique**: `premium-auto-login: true` na configuração
- **Verifique**: Conexão com internet para verificar API Mojang

### Banco de dados não inicializa
- **Verifique**: Permissões da pasta `plugins/AuthPlugin/`
- **Verifique**: Espaço em disco disponível
- **Solução**: Remova `database.db` para recriar

## 🔄 Migração de Outros Plugins

### Do AuthMe
1. Exporte dados do AuthMe (se necessário)
2. Instale AuthPlugin
3. Configure mensagens similares
4. Teste com jogadores premium e piratas

### Configuração Recomendada para Migração
```yaml
security:
  premium-auto-login: true
  block-premium-names: true
  session-timeout: 600  # 10 minutos
  max-login-attempts: 5
```

## 📊 Performance

### Otimizações Implementadas
- **Cache de API**: Reduz chamadas repetitivas à API Mojang
- **Consultas assíncronas**: Não bloqueia thread principal
- **Índices de banco**: Consultas SQL otimizadas
- **Limpeza automática**: Remove sessões expiradas periodicamente

### Recursos Utilizados
- **RAM**: ~5-10MB base + cache de jogadores
- **CPU**: Mínimo (apenas durante autenticação)
- **Rede**: Consultas esporádicas à API Mojang
- **Disco**: Banco SQLite (~1MB por 1000 jogadores)

## 🤝 Suporte

### Reportar Bugs
- Descreva o problema detalhadamente
- Inclua versão do servidor e plugin
- Anexe logs relevantes

### Sugestões de Recursos
- Abra uma issue descrevendo a funcionalidade
- Explique o caso de uso
- Considere compatibilidade com versões existentes

## 📜 Licença

Este plugin é fornecido como está, para uso em servidores Minecraft. Sinta-se livre para modificar conforme necessário.

## 🔗 Dependências

- **Spigot/Paper API**: 1.8+
- **SQLite JDBC**: 3.42.0.0
- **BCrypt**: 0.4 (para hash de senhas)
- **JSON**: 20230618 (para API Mojang)

---

**Desenvolvido para servidores que precisam de autenticação robusta mantendo compatibilidade com contas premium e piratas.**