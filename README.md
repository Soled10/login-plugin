# MinecraftAuth Plugin

Um plugin de autenticação avançado para servidores Minecraft com `online-mode=false` que diferencia entre contas originais e piratas, com proteções de segurança contra uso indevido de nicks de contas originais.

## 🚀 Funcionalidades

### Sistema de Autenticação
- **Contas Originais**: Login automático para contas originais do Minecraft
- **Contas Piratas**: Sistema de registro e login obrigatório
- **Proteção Anti-Spoofing**: Impede uso de nicks de contas originais por jogadores piratas

### Comandos Disponíveis
- `/login <senha>` - Fazer login na conta
- `/register <senha> <confirmar_senha>` - Registrar nova conta
- `/changepassword <senha_atual> <nova_senha> <confirmar_nova_senha>` - Alterar senha
- `/auth <reload|stats|addoriginal>` - Comandos administrativos

### Recursos de Segurança
- **Criptografia de Senhas**: Usa BCrypt para hash seguro das senhas
- **Proteção contra Brute Force**: Bloqueio temporário após tentativas excessivas
- **Sessões Temporárias**: Sessões expiram automaticamente
- **Restrições de Ação**: Jogadores não autenticados têm ações limitadas

## 📋 Requisitos

- **Minecraft Server**: 1.20.1+ (Spigot/Paper)
- **Java**: 17+
- **Online Mode**: `false` no server.properties

## 🔧 Instalação

1. **Baixe o plugin**:
   ```bash
   # O arquivo JAR está em: build/libs/MinecraftAuth-1.0.0.jar
   ```

2. **Instale no servidor**:
   - Copie o arquivo `MinecraftAuth-1.0.0.jar` para a pasta `plugins/` do seu servidor
   - Reinicie o servidor

3. **Configure o servidor**:
   - Certifique-se de que `online-mode=false` no `server.properties`
   - O plugin criará automaticamente o banco de dados SQLite

## ⚙️ Configuração

O plugin criará um arquivo `config.yml` na pasta `plugins/MinecraftAuth/` com as seguintes opções:

```yaml
# Configurações de segurança
security:
  session_duration_minutes: 60      # Duração da sessão
  min_password_length: 6            # Tamanho mínimo da senha
  max_login_attempts: 5             # Tentativas antes do bloqueio
  lock_duration_minutes: 30         # Duração do bloqueio

# Configurações de proteção
protection:
  block_commands: true              # Bloquear comandos
  block_movement: true              # Bloquear movimento
  block_chat: true                  # Bloquear chat
  block_interactions: true          # Bloquear interações
  block_block_actions: true         # Bloquear quebra/colocação de blocos
  block_item_actions: true          # Bloquear drop/coleta de itens
  block_inventory: true             # Bloquear inventários
```

## 🛡️ Como Funciona

### Para Contas Originais
1. O jogador entra no servidor
2. O plugin detecta automaticamente que é uma conta original
3. Login automático é realizado
4. Uma senha padrão é gerada e enviada por mensagem privada
5. O jogador pode usar `/changepassword` para alterar a senha

### Para Contas Piratas
1. O jogador entra no servidor
2. Todas as ações são bloqueadas até a autenticação
3. O jogador deve usar `/register` para criar uma conta
4. Após o registro, pode usar `/login` para futuras sessões

### Proteção Anti-Spoofing
- O plugin mantém uma lista de contas originais
- Se um jogador tentar usar um nick de conta original com UUID diferente, será kickado
- Apenas o dono real da conta original pode usar aquele nick

## 🔨 Compilação

Para compilar o plugin do código fonte:

```bash
# Clone o repositório
git clone <repository-url>
cd MinecraftAuth

# Compile com Gradle
./gradlew clean build

# O JAR estará em: build/libs/MinecraftAuth-1.0.0.jar
```

## 📊 Banco de Dados

O plugin usa SQLite para armazenar:
- **users**: Dados dos usuários (UUID, username, senha, tipo de conta)
- **sessions**: Sessões ativas
- **original_accounts**: Lista de contas originais

## 🚨 Permissões

- `minecraftauth.login` - Permite fazer login (padrão: true)
- `minecraftauth.register` - Permite se registrar (padrão: true)
- `minecraftauth.changepassword` - Permite alterar senha (padrão: true)
- `minecraftauth.admin` - Comandos administrativos (padrão: op)

## 🐛 Solução de Problemas

### Plugin não carrega
- Verifique se o Java 17+ está instalado
- Confirme se o servidor é Spigot/Paper 1.20.1+

### Contas originais não fazem login automático
- Verifique se `online-mode=false` no server.properties
- Use `/auth addoriginal <username>` para adicionar contas originais manualmente

### Jogadores piratas não conseguem se registrar
- Verifique se o banco de dados tem permissões de escrita
- Confirme se o username não está em uso

## 📝 Changelog

### v1.0.0
- Sistema de autenticação completo
- Diferenciação entre contas originais e piratas
- Proteção anti-spoofing
- Sistema de sessões temporárias
- Comandos de administração
- Configuração flexível

## 🤝 Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para:
- Reportar bugs
- Sugerir novas funcionalidades
- Enviar pull requests

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido para servidores Minecraft com foco em segurança e proteção contra contas piratas.**