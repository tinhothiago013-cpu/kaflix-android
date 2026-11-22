# KaFlix Android — projeto inicial nativo

Este é um projeto Android nativo em Kotlin (não é um site e não usa WebView). Inclui identidade visual escura/vermelha, abertura com fala “KaFlix” via mecanismo de voz do Android, login com usuário e senha, autenticação Xtream Codes e carregamento inicial das categorias de TV ao vivo, filmes e séries.

## Antes de compilar

1. Abra `app/build.gradle.kts`.
2. Localize `buildConfigField("String", "PROVIDER_BASE_URL", "\"https://SEU-SERVIDOR-AQUI\"")`.
3. Troque apenas o endereço de exemplo pelo endereço-base HTTPS do seu provedor. Não coloque usuário ou senha nessa linha.
4. Salve e compile com JDK 17, Android SDK 35 e Gradle/Android Gradle Plugin compatíveis.

O login da tela pede somente usuário e senha. O endereço do provedor fica configurado no projeto, não aparece na tela.

## Limitações desta primeira base

- A autenticação e a listagem inicial de categorias usam a API Xtream Codes (`player_api.php`). O provedor precisa oferecer essa API.
- A reprodução de canais/filmes/séries, seleção de qualidade SD/HD/Full HD, navegação completa por títulos e tratamento de formatos ainda precisam ser implementados e testados com os dados reais do provedor.
- A voz depende das vozes instaladas no Android; o app solicita português do Brasil e velocidade reduzida, mas a voz feminina depende da configuração de TTS do aparelho.
- Não inclui conteúdo de TV, filmes ou séries. Use apenas listas e serviços aos quais você tenha autorização de acesso.

## Estrutura

- `app/`: aplicativo Android nativo
- `app/src/main/java/com/kaflix/app/MainActivity.kt`: telas, fala inicial, login e chamadas básicas da API
