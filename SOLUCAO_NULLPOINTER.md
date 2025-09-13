# 🔧 Solução Definitiva - NullPointerException

## ❌ Problema
O erro `NullPointerException: Cannot invoke "com.authplugin.utils.MojangAPI.isOriginalAccount(String)" because "this.mojangAPI" is null` indica que você está usando uma versão antiga do JAR que não tem as correções.

## ✅ Solução Completa

### Passo 1: Recompilação Forçada
```cmd
force-rebuild.bat
```

### Passo 2: Instalação Automática
```cmd
install.bat
```

### Passo 3: Configuração do Servidor
1. Configure `online-mode=false` no `server.properties`
2. Reinicie o servidor

## 🔍 O que foi corrigido:

1. **Ordem de inicialização** - `mojangAPI` é criado antes do `authUtils`
2. **Verificação de null** - Adicionada verificação de segurança
3. **Estrutura de recursos** - `plugin.yml` e `config.yml` na pasta correta
4. **Scripts automatizados** - Para recompilação e instalação

## 🚀 Comandos Rápidos

```cmd
# Solução completa (copie e cole)
force-rebuild.bat && install.bat
```

## 📋 Verificação

Após executar os comandos, verifique:

1. **JAR compilado**: `target/AuthPlugin-1.0.0.jar`
2. **plugin.yml incluído**: Deve aparecer "✅ plugin.yml encontrado!"
3. **MojangAPI incluído**: Deve aparecer "✅ MojangAPI encontrado no JAR!"
4. **Plugin instalado**: Na pasta `plugins/` do servidor

## 🎯 Teste Final

1. **Reinicie o servidor**
2. **Entre com sua conta original**
3. **Deve aparecer**: "Conta original detectada! Você foi autenticado automaticamente."

## 🐛 Se Ainda Der Erro

### Verificar se está usando o JAR correto:
```cmd
# Verificar data de modificação
dir target\AuthPlugin-1.0.0.jar

# Verificar conteúdo
jar -tf target\AuthPlugin-1.0.0.jar | findstr MojangAPI
```

### Limpeza completa:
```cmd
# Remover tudo e recompilar
rmdir /s /q target
rmdir /s /q src\main\resources
force-rebuild.bat
```

## 📋 Checklist

- [ ] Executeu `force-rebuild.bat`
- [ ] Viu "✅ plugin.yml encontrado!"
- [ ] Viu "✅ MojangAPI encontrado no JAR!"
- [ ] Executeu `install.bat`
- [ ] Configurou `online-mode=false`
- [ ] Reiniciou o servidor
- [ ] Testou com conta original

## ⚠️ Importante

- **NÃO** use arquivos JAR antigos
- **SEMPRE** recompile após mudanças no código
- **VERIFIQUE** se o JAR tem os arquivos corretos
- **TESTE** com conta original primeiro