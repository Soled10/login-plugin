# AuthPlugin - Plugin de Autenticação para Minecraft

## Descrição
Plugin completo de autenticação para servidores Minecraft que diferencia entre contas originais e piratas, com sistema de proteção contra uso de nicks de contas originais.

## Funcionalidades

### 🔐 Sistema de Autenticação
- **Contas Originais**: Autenticação automática via API da Mojang
- **Contas Piratas**: Sistema de registro e login com senha
- **Proteção de Nicks**: Impede uso de nicks de contas originais por jogadores piratas

### 🛡️ Proteções de Segurança
- Verificação em tempo real de contas originais
- Lista de proteção para nicks de contas originais
- Restrições de movimento e interação para jogadores não autenticados
- Bloqueio de comandos para jogadores não autenticados

### 🔧 Comandos Disponíveis
- `/register <senha> <confirmar_senha>` - Registra uma nova conta
- `/login <senha>` - Faz login na conta
- `/changepassword <senha_atual> <nova_senha>` - Altera a senha
- `/auth <comando>` - Comando principal com subcomandos

### 🗄️ Banco de Dados
- SQLite para armazenamento local
- Criptografia SHA-256 com salt para senhas
- Tabelas separadas para contas e nicks originais protegidos

## Instalação

1. Compile o plugin usando Maven:
```bash
mvn clean package
```

2. Coloque o arquivo `AuthPlugin-1.0.0.jar` na pasta `plugins` do seu servidor

3. Configure o servidor com `online-mode=false` no `server.properties`

4. Reinicie o servidor

## Configuração

O plugin funciona automaticamente sem necessidade de configuração adicional. Ele criará automaticamente:
- Banco de dados SQLite em `plugins/AuthPlugin/auth.db`
- Tabelas necessárias para funcionamento

## Como Funciona

### Para Contas Originais
1. Jogador entra no servidor
2. Plugin verifica se é conta original via API da Mojang
3. Se for original, autentica automaticamente
4. Adiciona o nick à lista de proteção

### Para Contas Piratas
1. Jogador entra no servidor
2. Plugin verifica se o nick pertence a conta original
3. Se não pertencer, permite registro/login
4. Se pertencer, kicka o jogador

### Proteções Ativas
- Jogadores não autenticados não podem:
  - Mover-se horizontalmente
  - Falar no chat
  - Usar comandos (exceto de autenticação)
  - Interagir com blocos
  - Quebrar/colocar blocos
  - Receber dano

## Dependências

- Java 8 ou superior
- Spigot/Paper 1.16.5+
- SQLite (incluído no plugin)
- Gson (incluído no plugin)

## Compilação

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd AuthPlugin

# Compile o plugin
mvn clean package

# O arquivo JAR estará em target/AuthPlugin-1.0.0.jar
```

## Estrutura do Projeto

```
src/main/java/com/authplugin/
├── AuthPlugin.java              # Classe principal
├── commands/                    # Comandos do plugin
│   ├── AuthCommand.java
│   ├── ChangePasswordCommand.java
│   ├── LoginCommand.java
│   └── RegisterCommand.java
├── database/                    # Gerenciamento do banco de dados
│   └── DatabaseManager.java
├── listeners/                   # Event listeners
│   ├── PlayerJoinListener.java
│   ├── PlayerMoveListener.java
│   ├── PlayerChatListener.java
│   ├── PlayerCommandListener.java
│   ├── PlayerInteractListener.java
│   └── PlayerDamageListener.java
└── utils/                       # Utilitários
    ├── AuthUtils.java
    └── MojangAPI.java
```

## Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## Suporte

Para suporte ou reportar bugs, abra uma issue no repositório do projeto.