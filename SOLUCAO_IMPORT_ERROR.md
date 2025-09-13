# 🔧 Solução de Erro de Import - AuthPlugin

## ❌ Erro Identificado

```
[ERROR] package OnlineModeSimulator does not exist
```

## ✅ Soluções

### Solução 1: Build com Correção de Imports
```cmd
build-fix-imports.bat
```

### Solução 2: Build com Debug
```cmd
build-debug.bat
```

### Solução 3: Verificação Manual

1. **Verifique se o arquivo existe**:
   ```cmd
   dir src\main\java\com\authplugin\utils\OnlineModeSimulator.java
   ```

2. **Verifique o package**:
   - Deve estar em `com.authplugin.utils`
   - Deve ter `package com.authplugin.utils;` no início

3. **Verifique os imports**:
   - `PlayerJoinListener.java` deve ter:
   ```java
   import com.authplugin.utils.OnlineModeSimulator;
   ```

## 🔍 Verificação de Arquivos

### Estrutura Correta:
```
src/main/java/com/authplugin/
├── utils/
│   ├── OnlineModeSimulator.java
│   ├── AuthUtils.java
│   └── MojangAPI.java
├── listeners/
│   ├── PlayerJoinListener.java
│   └── ...
└── ...
```

### Arquivos Necessários:
- ✅ `OnlineModeSimulator.java` - Classe principal
- ✅ `PlayerJoinListener.java` - Listener com import correto
- ✅ `AuthUtils.java` - Utilitários atualizados

## 🚀 Comandos de Solução

### Opção 1: Build Automático
```cmd
build-fix-imports.bat
```

### Opção 2: Build com Debug
```cmd
build-debug.bat
```

### Opção 3: Limpeza e Rebuild
```cmd
# Limpar tudo
rmdir /s /q target
rmdir /s /q src\main\resources

# Recompilar
mvn clean package
```

## 🔍 Verificação de Funcionamento

Após a compilação bem-sucedida:

1. **Verifique o JAR**:
   ```cmd
   jar -tf target\AuthPlugin-1.0.0.jar | findstr OnlineModeSimulator
   ```

2. **Deve mostrar**:
   ```
   com/authplugin/utils/OnlineModeSimulator.class
   ```

3. **Teste no servidor**:
   - Instale o plugin
   - Reinicie o servidor
   - Verifique logs

## ⚠️ Problemas Comuns

### Se ainda der erro:
1. **Verifique se está na pasta correta**
2. **Verifique se todos os arquivos existem**
3. **Verifique se os packages estão corretos**
4. **Use o build-debug.bat para mais detalhes**

### Se o JAR não funcionar:
1. **Verifique se plugin.yml está incluído**
2. **Verifique se todas as classes estão no JAR**
3. **Teste em servidor limpo**

## 📋 Checklist

- [ ] Arquivo `OnlineModeSimulator.java` existe
- [ ] Package correto: `com.authplugin.utils`
- [ ] Import correto em `PlayerJoinListener.java`
- [ ] Compilação sem erros
- [ ] JAR gerado com sucesso
- [ ] Plugin funciona no servidor