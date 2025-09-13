# 🤝 Guia de Contribuição - AuthPlugin

Obrigado por considerar contribuir com o AuthPlugin! Este documento fornece diretrizes para contribuições.

## 🚀 Como Contribuir

### 1. Fork do Repositório
1. Faça um fork do repositório
2. Clone seu fork localmente
3. Crie uma branch para sua feature: `git checkout -b feature/nova-funcionalidade`

### 2. Desenvolvimento
1. Faça suas alterações
2. Teste o plugin localmente
3. Compile com `mvn clean package`
4. Teste no servidor Minecraft

### 3. Commit
```bash
git add .
git commit -m "feat: adiciona nova funcionalidade X"
```

### 4. Push e Pull Request
```bash
git push origin feature/nova-funcionalidade
```
Depois crie um Pull Request no GitHub.

## 📋 Padrões de Código

### Java
- Use Java 8+ features
- Siga as convenções de nomenclatura Java
- Adicione comentários Javadoc para métodos públicos
- Use indentação de 4 espaços

### Commits
- Use o padrão: `tipo: descrição`
- Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- Exemplo: `feat: adiciona comando de troca de senha`

## 🐛 Reportando Bugs

Use o template de issue do GitHub e inclua:
- Versão do Minecraft
- Versão do Spigot/Paper
- Logs do servidor
- Passos para reproduzir
- Screenshots (se aplicável)

## ✨ Sugerindo Melhorias

Para novas funcionalidades:
- Descreva a funcionalidade
- Explique o caso de uso
- Considere compatibilidade com versões anteriores

## 🧪 Testando

Antes de submeter:
1. Teste com contas originais
2. Teste com contas piratas
3. Teste comandos
4. Verifique logs
5. Teste em diferentes versões do Minecraft

## 📝 Documentação

- Atualize README.md se necessário
- Adicione comentários no código
- Documente novas configurações

## 🔒 Segurança

- Não inclua credenciais ou tokens
- Teste mudanças de segurança cuidadosamente
- Considere implicações de segurança

## 📞 Suporte

- Issues no GitHub para bugs
- Discussions para dúvidas
- Pull Requests para contribuições

Obrigado por contribuir! 🎉