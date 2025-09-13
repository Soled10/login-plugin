# AuthPlugin - Plugin de Autenticação para Minecraft

Um plugin completo de autenticação para servidores Minecraft que permite contas premium (originais) entrarem diretamente e exige registro para contas piratas, com proteções contra uso indevido de nicks premium.

## 🚀 Características

- **Login automático** para contas premium/originais do Minecraft
- **Sistema de registro** obrigatório para contas piratas
- **Proteção contra pirates** usando nicks de contas originais
- **Comando de mudança de senha** (`/changepassword`)
- **Verificação via Mojang API** para validar contas premium
- **Sistema de timeout** para login
- **Restrições completas** até autenticação (movimento, chat, interações)
- **Database SQLite** integrado
- **Configuração flexível**

## 📋 Requisitos

- **Servidor**: Spigot/Paper 1.20.1+
- **Java**: 17+
- **Configuração**: `online-mode=false` no `server.properties`

## 🔧 Instalação

1. **Baixar o plugin**: Pegue o arquivo `AuthPlugin-1.0.0.jar` da pasta `build/libs/`

2. **Instalar no servidor**: Coloque o JAR na pasta `plugins/` do seu servidor

3. **Configurar o servidor**: No arquivo `server.properties`, defina:
   ```properties
   online-mode=false
   ```

4. **Reiniciar o servidor**: Reinicie para carregar o plugin

5. **Configurar** (opcional): Edite o arquivo `plugins/AuthPlugin/config.yml` conforme necessário

## 🎮 Como Usar

### Para Jogadores Premium (Contas Originais)
- **Login automático**: Apenas entre no servidor - o login será feito automaticamente
- **Sem necessidade de registro**: O sistema detecta automaticamente contas premium

### Para Jogadores Piratas
- **Primeiro acesso**: Use `/register <senha> <confirmar-senha>`
- **Próximos acessos**: Use `/login <senha>`
- **Mudar senha**: Use `/changepassword <senha-atual> <nova-senha>`

### Comandos Disponíveis

| Comando | Aliases | Descrição | Uso |
|---------|---------|-----------|-----|
| `/login` | `/l` | Fazer login | `/login <senha>` |
| `/register` | `/reg` | Registrar conta | `/register <senha> <confirmar-senha>` |
| `/changepassword` | `/changepass`, `/cp` | Alterar senha | `/changepassword <senha-atual> <nova-senha>` |

## 🛡️ Proteções Implementadas

### Contra Pirates Usando Nicks Premium
- **Verificação de UUID**: Compara o UUID do jogador com o UUID oficial da Mojang
- **Bloqueio automático**: Impede registro se o nick pertence a uma conta premium
- **Cache inteligente**: Otimiza consultas à API da Mojang

### Restrições Até Autenticação
- ❌ Movimento (exceto rotação da cabeça)
- ❌ Chat
- ❌ Comandos (exceto login/registro)
- ❌ Interações com blocos/itens
- ❌ Abrir inventários
- ❌ Quebrar/colocar blocos
- ❌ Receber/causar dano

## ⚙️ Configuração

O arquivo `config.yml` permite personalizar:

```yaml
# Tempo limite para login (segundos)
login-timeout: 60

# Spawn de login (opcional)
login-spawn:
  enabled: false
  world: "world"
  x: 0.0
  y: 100.0
  z: 0.0

# Configurações de segurança
security:
  check-premium: true
  block-premium-names: true
  premium-cache-duration: 300000

# Mensagens personalizáveis
messages:
  prefix: "§e[Auth] "
  login-success: "§aLogin realizado com sucesso!"
  # ... mais mensagens
```

## 🔨 Compilação

Para compilar o plugin você mesmo:

```bash
# Clonar/baixar o código fonte
git clone <repository-url>
cd AuthPlugin

# Compilar com Gradle
./gradlew clean build

# O JAR será gerado em build/libs/AuthPlugin-1.0.0.jar
```

## 📁 Estrutura do Projeto

```
AuthPlugin/
├── src/main/java/com/example/authplugin/
│   ├── AuthPlugin.java              # Classe principal
│   ├── commands/                    # Comandos do plugin
│   │   ├── LoginCommand.java
│   │   ├── RegisterCommand.java
│   │   └── ChangePasswordCommand.java
│   ├── database/                    # Sistema de database
│   │   └── DatabaseManager.java
│   ├── listeners/                   # Event listeners
│   │   └── PlayerListener.java
│   ├── managers/                    # Gerenciadores
│   │   ├── AuthManager.java
│   │   └── SessionManager.java
│   ├── models/                      # Modelos de dados
│   │   └── PlayerData.java
│   └── utils/                       # Utilitários
│       └── MojangAPI.java
├── src/main/resources/
│   ├── plugin.yml                   # Configuração do plugin
│   └── config.yml                   # Configuração padrão
├── build.gradle                     # Configuração do Gradle
└── README.md                        # Este arquivo
```

## 🔒 Segurança

### Criptografia
- **BCrypt**: Senhas são criptografadas com BCrypt (salt automático)
- **Não reversível**: Impossível recuperar senhas em texto plano

### Validação Premium
- **API Mojang**: Verifica contas premium através da API oficial
- **UUID Matching**: Garante que o UUID corresponde ao username
- **Cache**: Sistema de cache para otimizar consultas

### Proteção de Dados
- **SQLite local**: Database armazenado localmente no servidor
- **Logs de segurança**: Registro de tentativas de login/registro
- **Timeout automático**: Expulsa jogadores que não se autenticam

## 🐛 Solução de Problemas

### Plugin não carrega
- Verifique se está usando Java 17+
- Confirme que o servidor é Spigot/Paper 1.20.1+
- Verifique logs do servidor para erros

### Contas premium não fazem auto-login
- Confirme que `online-mode=false` no server.properties
- Verifique conectividade com a API da Mojang
- Consulte logs para erros de rede

### Database não funciona
- Verifique permissões de escrita na pasta do plugin
- Consulte logs para erros de SQLite

## 📝 Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 🤝 Contribuição

Contribuições são bem-vindas! Sinta-se livre para:
- Reportar bugs
- Sugerir melhorias
- Enviar pull requests
- Melhorar documentação

## ⚠️ Aviso Legal

- Permitir contas piratas pode violar os termos de serviço da Mojang
- Use este plugin por sua conta e risco
- Certifique-se de cumprir as leis locais sobre pirataria de software

---

**Desenvolvido para facilitar servidores híbridos que atendem tanto jogadores premium quanto piratas, mantendo a segurança e integridade do servidor.**