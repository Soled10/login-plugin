# 🔧 Solução de Problemas - AuthPlugin

## ❌ Erro: "Directory 'plugins\original-AuthPlugin-1.0.0.jar' failed to load"

### 🎯 Causa
O Maven Shade Plugin está criando um arquivo com nome incorreto que não contém o `plugin.yml`.

### ✅ Solução

#### Passo 1: Limpeza Completa
```cmd
cleanup.bat
```

#### Passo 2: Recompilar
```cmd
build.bat
```

#### Passo 3: Verificar Arquivo Gerado
O arquivo correto deve ser: `target/AuthPlugin-1.0.0.jar`

### 🔍 Verificação

1. **Verifique se existe o arquivo correto:**
   ```cmd
   dir target\*.jar
   ```

2. **Deve mostrar apenas:**
   ```
   AuthPlugin-1.0.0.jar
   ```

3. **NÃO deve ter:**
   ```
   original-AuthPlugin-1.0.0.jar
   ```

### 🚀 Instalação no Servidor

1. **Copie apenas o arquivo correto:**
   ```
   target/AuthPlugin-1.0.0.jar → plugins/
   ```

2. **NÃO copie:**
   - `original-AuthPlugin-1.0.0.jar`
   - `dependency-reduced-pom.xml`

### 🐛 Se Ainda Der Erro

#### Opção 1: Limpeza Manual
```cmd
# Remover pasta target
rmdir /s /q target

# Recompilar
mvn clean package
```

#### Opção 2: Verificar plugin.yml
```cmd
# Verificar se o plugin.yml está correto
type plugin.yml
```

#### Opção 3: Testar JAR
```cmd
# Verificar conteúdo do JAR
jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml
```

### 📋 Checklist de Verificação

- [ ] Executeu `cleanup.bat`
- [ ] Executeu `build.bat`
- [ ] Arquivo gerado: `AuthPlugin-1.0.0.jar`
- [ ] Arquivo tem `plugin.yml` dentro
- [ ] Copiou apenas o arquivo correto para `plugins/`
- [ ] Servidor em `online-mode=false`

### ⚠️ Problemas Comuns

1. **Múltiplos arquivos JAR**: Copie apenas `AuthPlugin-1.0.0.jar`
2. **Arquivo original-**: Este é um arquivo temporário, ignore
3. **plugin.yml não encontrado**: Recompile o projeto
4. **Servidor não reconhece**: Verifique se é o arquivo correto

### 🎯 Comandos Rápidos

```cmd
# Limpeza e build completo
cleanup.bat && build.bat

# Verificar arquivo gerado
dir target\*.jar

# Testar JAR
jar -tf target\AuthPlugin-1.0.0.jar | findstr plugin.yml
```