# ⚡ Comandos Rápidos para GitHub

## 🚀 Se você já tem o repositório configurado

```bash
git add .
git commit -m "feat: AuthPlugin completo com correções de bugs e melhorias"
git push origin main
```

## 🆕 Se for um repositório novo

### 1. Criar repositório no GitHub primeiro:
- Acesse: https://github.com/new
- Nome: `AuthPlugin`
- Descrição: `Plugin de autenticação para Minecraft com proteção contra contas piratas`
- **NÃO** marque "Initialize with README"

### 2. Depois execute estes comandos:
```bash
git init
git add .
git commit -m "feat: Plugin de autenticação completo para Minecraft"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/AuthPlugin.git
git push -u origin main
```

## 🔧 Configuração inicial do Git (se necessário)

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@exemplo.com"
```

## 🐛 Problemas comuns

### Erro de "remote origin already exists"
```bash
git remote remove origin
git remote add origin https://github.com/SEU_USUARIO/AuthPlugin.git
git push -u origin main
```

### Erro de "failed to push some refs"
```bash
git pull origin main --allow-unrelated-histories
git push origin main
```

## 📋 Checklist

- [ ] Git configurado com nome e email
- [ ] Repositório criado no GitHub
- [ ] Comandos executados com sucesso
- [ ] Arquivos visíveis no GitHub
- [ ] README.md formatado corretamente