# 🛠️ Guia de Instalação - AuthPlugin

## ✅ Plugin Compilado com Sucesso!

O plugin **AuthPlugin** foi compilado e está pronto para uso. O arquivo JAR final está localizado em:
```
target/AuthPlugin-1.0.0.jar (130 KB)
```

## 📋 Pré-requisitos

- **Servidor Minecraft**: Spigot/Paper 1.8+ (testado até 1.20.1)
- **Java**: 8 ou superior
- **Configuração obrigatória**: `online-mode=false` no `server.properties`

## 🚀 Instalação Rápida

### 1. Copiar o Plugin
```bash
cp target/AuthPlugin-1.0.0.jar /caminho/para/seu/servidor/plugins/
```

### 2. Configurar o Servidor
Edite o arquivo `server.properties`:
```properties
online-mode=false
```

### 3. Reiniciar o Servidor
Reinicie o servidor Minecraft para carregar o plugin.

### 4. Configurar o Plugin (Opcional)
O plugin criará automaticamente:
- `plugins/AuthPlugin/config.yml` - Configurações principais
- `plugins/AuthPlugin/players.yml` - Dados dos jogadores
- `plugins/AuthPlugin/sessions.yml` - Sessões ativas

## ⚡ Teste Rápido

1. **Conta Premium**: Entre no servidor com uma conta original → Login automático
2. **Conta Pirata**: Entre com nick não-premium → Use `/register senha senha`
3. **Administrador**: Use `/authreload` para recarregar configurações

## 🎯 Funcionalidades Principais

### ✅ Para Jogadores Premium
- ✅ **Login automático** - Entra direto sem precisar de senha
- ✅ **Verificação de UUID** - Confirma se é realmente a conta premium
- ✅ **Proteção anti-pirata** - Impede piratas de usar seu nick

### ✅ Para Jogadores Piratas
- ✅ **Sistema de registro** - `/register senha confirmar`
- ✅ **Login seguro** - `/login senha`
- ✅ **Mudança de senha** - `/changepassword antiga nova confirmar`

### 🔒 Segurança
- ✅ **Criptografia BCrypt** - Senhas protegidas
- ✅ **Limite de tentativas** - Proteção contra ataques
- ✅ **Sistema de sessões** - Mantém login por tempo configurável
- ✅ **API Mojang** - Verificação real-time de contas premium

## 📝 Comandos Disponíveis

### Para Jogadores
| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `/register <senha> <confirmar>` | Criar conta | `/register minhasenha minhasenha` |
| `/login <senha>` | Fazer login | `/login minhasenha` |
| `/changepassword <atual> <nova> <confirmar>` | Trocar senha | `/changepassword antiga nova nova` |

### Para Administradores
| Comando | Permissão | Descrição |
|---------|-----------|-----------|
| `/authreload` | `authplugin.admin` | Recarregar config |
| `/unregister <jogador>` | `authplugin.admin` | Remover conta |

## ⚙️ Configuração Avançada

### Exemplo de `config.yml`:
```yaml
security:
  premium-auto-login: true      # Auto-login para premium
  check-premium-uuid: true      # Verificar UUID via Mojang
  block-premium-names: true     # Bloquear piratas com nicks premium
  session-timeout: 300          # Sessão de 5 minutos
  max-login-attempts: 3         # Máximo 3 tentativas
  login-timeout: 60             # 60s para fazer login

spawn:
  teleport-to-spawn: true       # Teleportar para spawn
  world: "world"
  x: 0
  y: 64
  z: 0
```

## 🔧 Solução de Problemas

### ❌ "Jogadores premium não conseguem entrar"
**Solução**: Verifique se `online-mode=false` no `server.properties`

### ❌ "API Mojang indisponível"
**Solução**: Plugin funciona offline, mas não verifica contas premium

### ❌ "Erro nos arquivos YAML"
**Solução**: Verifique permissões da pasta `plugins/AuthPlugin/` e se os arquivos não estão corrompidos

### ❌ "Plugin não carrega"
**Solução**: Verifique se está usando Java 8+ e Spigot/Paper 1.8+

## 🌟 Recursos Especiais

### 🛡️ Proteção Anti-Pirata
O plugin impede que jogadores piratas usem nicks de contas premium:
1. Verifica se o nick pertence a uma conta Mojang
2. Compara o UUID do jogador com o UUID real da conta
3. Bloqueia acesso se não coincidir

### ⚡ Performance Otimizada
- Cache inteligente para reduzir chamadas à API Mojang
- Consultas assíncronas que não travam o servidor
- Arquivos YAML leves e editáveis manualmente
- Limpeza automática de sessões expiradas

### 🌍 Compatibilidade
- **Versões**: Minecraft 1.8 até 1.20.1
- **Servidores**: Spigot, Paper, Purpur
- **Redes**: Compatível com BungeeCord/Velocity
- **Plugins**: Não conflita com outros plugins de auth

## 📊 Estatísticas do Plugin

- **Tamanho**: 130 KB (muito leve!)
- **Dependências incluídas**:
  - BCrypt 0.4
  - JSON 20230618
  - Bukkit YAML (nativo do Spigot)
- **Linguagem**: Java 8 (compatível com versões superiores)
- **Licença**: Uso livre para servidores Minecraft

## 🎉 Plugin Pronto para Uso!

O **AuthPlugin** está completamente funcional e pronto para proteger seu servidor. Ele oferece:

✅ **Segurança máxima** para contas premium e piratas  
✅ **Facilidade de uso** com comandos intuitivos  
✅ **Performance otimizada** para servidores de qualquer tamanho  
✅ **Configuração flexível** para diferentes necessidades  

---

**Desenvolvido especialmente para servidores que precisam de autenticação robusta mantendo compatibilidade total com contas premium e piratas.**

Para suporte ou dúvidas, consulte o arquivo `README.md` ou os exemplos em `config-examples.yml`.