# 🚀 Guia Completo para Atualizar no GitHub

## 📋 Pré-requisitos
- Git instalado no seu computador
- Conta no GitHub
- Repositório já criado no GitHub (ou criar um novo)

## 🔧 Passo a Passo

### 1. Inicializar o Git (se ainda não foi feito)
```bash
git init
```

### 2. Adicionar o repositório remoto
```bash
# Substitua SEU_USUARIO e NOME_DO_REPOSITORIO pelos seus dados
git remote add origin https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git
```

### 3. Configurar usuário Git (se ainda não foi feito)
```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@exemplo.com"
```

### 4. Adicionar todos os arquivos
```bash
git add .
```

### 5. Fazer commit
```bash
git commit -m "feat: Plugin de autenticação completo com proteção contra contas piratas

- Sistema de autenticação diferenciado (originais vs piratas)
- Verificação via API da Mojang para contas originais
- Sistema de registro e login para contas piratas
- Proteção contra uso de nicks de contas originais
- Comandos: /register, /login, /changepassword, /auth
- Banco de dados SQLite com criptografia SHA-256
- Restrições para jogadores não autenticados
- Logs detalhados para debug
- Suporte a online-mode=false"
```

### 6. Fazer push para o GitHub
```bash
# Primeira vez (criar branch main)
git push -u origin main

# Ou se já existe o repositório
git push origin main
```

## 🔄 Comandos Rápidos (Copie e Cole)

Se você já tem o repositório configurado, use estes comandos:

```bash
git add .
git commit -m "feat: Atualização completa do AuthPlugin com correções de bugs"
git push origin main
```

## 🆕 Se for um repositório novo

### 1. Criar repositório no GitHub
1. Acesse https://github.com
2. Clique em "New repository"
3. Nome: `AuthPlugin` (ou o nome que preferir)
4. Descrição: `Plugin de autenticação para Minecraft com proteção contra contas piratas`
5. Marque como "Public" ou "Private"
6. **NÃO** marque "Initialize with README"
7. Clique em "Create repository"

### 2. Comandos para repositório novo
```bash
git init
git add .
git commit -m "feat: Plugin de autenticação completo para Minecraft"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/AuthPlugin.git
git push -u origin main
```

## 🐛 Resolução de Problemas

### Se der erro de "remote origin already exists"
```bash
git remote remove origin
git remote add origin https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git
```

### Se der erro de "failed to push some refs"
```bash
git pull origin main --allow-unrelated-histories
git push origin main
```

### Se der erro de autenticação
1. Use token de acesso pessoal do GitHub
2. Ou configure SSH keys

## 📁 Estrutura do Projeto no GitHub

Após o push, seu repositório terá:
```
AuthPlugin/
├── src/main/java/com/authplugin/
│   ├── AuthPlugin.java
│   ├── commands/
│   ├── database/
│   ├── listeners/
│   └── utils/
├── plugin.yml
├── pom.xml
├── build.sh
├── build.bat
├── config.yml
├── README.md
├── LICENSE
└── .gitignore
```

## ✅ Verificação

Após o push, verifique se:
- [ ] Todos os arquivos estão no GitHub
- [ ] README.md está formatado corretamente
- [ ] LICENSE está presente
- [ ] .gitignore está funcionando (não mostra arquivos desnecessários)

## 🎯 Próximos Passos

1. **Criar releases** no GitHub para versões
2. **Configurar GitHub Actions** para build automático
3. **Adicionar issues** para bugs e melhorias
4. **Documentar** configurações avançadas