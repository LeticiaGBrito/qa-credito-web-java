# QA Crédito Web — Java

[![Testes Web Java](https://github.com/LeticiaGBrito/qa-credito-web-java/actions/workflows/testes-web.yml/badge.svg?branch=main)](https://github.com/LeticiaGBrito/qa-credito-web-java/actions/workflows/testes-web.yml)

Projeto educacional de automação de testes web com Java, Selenium,
JUnit 5 e Gradle.

Inclui um simulador de análise fictícia de crédito, servidor próprio,
documentação Swagger, captura automática de tela em caso de falha
e execução dos testes no GitHub Actions.

A aplicação e suas regras não representam políticas reais de
concessão de crédito e não possuem vínculo com empresas.

Utilize somente dados fictícios.

## Objetivo

Demonstrar a automação de uma jornada pelo navegador, contemplando:

- Preenchimento de formulário.
- Aprovação e reprovação por regras de negócio.
- Valores de fronteira.
- Campos obrigatórios.
- Entradas inválidas.
- Formatos monetários com vírgula ou ponto.
- Limpeza de campos, resultados e mensagens de erro.
- Organização com Page Objects.
- Esperas explícitas.
- Execução com navegador visível ou em modo headless.
- Relatórios HTML e JUnit XML.
- Captura de tela em falhas.
- Integração contínua com GitHub Actions.

## Execução independente

Este projeto possui sua própria aplicação e seus próprios testes.

Não é necessário iniciar os projetos de API ou a versão Python
para executar o simulador ou a suíte Java.

Durante os testes do simulador, a aplicação é iniciada
automaticamente em uma porta local disponível.

Os testes não dependem de uma aplicação hospedada externamente.

## Tecnologias e finalidade

| Tecnologia | Finalidade |
| --- | --- |
| Java 17 | Implementação da aplicação e dos testes |
| Selenium WebDriver | Controle do navegador |
| Selenium Manager | Gerenciamento do driver utilizado pelo Selenium |
| JUnit 5 | Organização, parametrização e execução dos testes |
| Gradle Wrapper | Compilação, execução, dependências e relatórios |
| HttpServer do JDK | Servidor HTTP da aplicação |
| Gson | Leitura e geração de JSON |
| BigDecimal | Representação e comparação dos valores monetários |
| HTML, CSS e JavaScript | Interface e validações do formulário |
| Swagger UI | Documentação interativa e envio manual de requisições |
| OpenAPI 3.1 | Descrição dos endpoints, modelos e exemplos |
| GitHub Actions | Execução automática dos testes e armazenamento dos relatórios |

As versões das dependências estão definidas em `build.gradle`.

A versão do Gradle é definida na configuração do Gradle Wrapper.

## Organização e responsabilidade dos arquivos

### Aplicação

| Arquivo | Responsabilidade |
| --- | --- |
| `src/main/java/credito/AnaliseCreditoService.java` | Valida os dados e aplica as regras fictícias de crédito |
| `src/main/java/credito/ResultadoAnalise.java` | Representa o status e os motivos da análise |
| `src/main/java/credito/CreditoWebApplication.java` | Inicia o servidor e atende as rotas da interface, análise e documentação |
| `src/main/resources/web/index.html` | Define o formulário, o estilo e as interações do simulador |
| `src/main/resources/docs/swagger.html` | Configura a interface Swagger |
| `src/main/resources/docs/openapi.json` | Documenta os endpoints, modelos e exemplos |

### Automação

| Arquivo | Responsabilidade |
| --- | --- |
| `src/test/java/credito/pages/BasePage.java` | Centraliza esperas explícitas e interações reutilizáveis |
| `src/test/java/credito/pages/CreditoPage.java` | Encapsula os localizadores e as ações do simulador |
| `src/test/java/credito/support/BaseWebTest.java` | Gerencia a aplicação local e o navegador dos testes |
| `src/test/java/credito/support/EvidenciaFalhaExtension.java` | Captura a tela em falhas de execução ou preparação, quando existe navegador disponível |
| `src/test/java/credito/SolicitacaoCreditoTest.java` | Verifica aprovações, reprovações e motivos |
| `src/test/java/credito/ValidacaoFormularioTest.java` | Verifica campos obrigatórios |
| `src/test/java/credito/DadosInvalidosTest.java` | Verifica entradas inválidas |
| `src/test/java/credito/ComportamentoFormularioTest.java` | Verifica formatos decimais e limpeza do formulário |
| `src/test/java/credito/AmbienteWebTest.java` | Verifica o controle do Chrome usando uma página simples incluída no teste |

### Configuração

| Arquivo | Responsabilidade |
| --- | --- |
| `build.gradle` | Define dependências, Java 17, execução da aplicação e relatórios |
| `settings.gradle` | Define o nome do projeto Gradle |
| `gradlew` e `gradlew.bat` | Permitem executar o Gradle pelo Wrapper |
| `gradle/wrapper/` | Contém a configuração e os arquivos do Gradle Wrapper |
| `.github/workflows/testes-web.yml` | Configura a execução dos testes no GitHub Actions |
| `.gitignore` | Exclui arquivos gerados e configurações locais do versionamento |
| `.gitattributes` | Padroniza o tratamento de arquivos e quebras de linha |

## Estratégia de testes

A suíte completa possui **25 testes**:

| Grupo | Quantidade |
| --- | ---: |
| Aprovação e reprovação | 6 |
| Campos obrigatórios | 4 |
| Dados inválidos | 10 |
| Formatos decimais e limpeza | 4 |
| Verificação do ambiente Selenium | 1 |
| **Total** | **25** |

São **24 cenários do simulador** e **1 teste de ambiente**.

A contagem inclui as execuções dos testes parametrizados.

### Aprovação e reprovação

- Aprovação com dados válidos.
- Aprovação nos limites exatos.
- Reprovação por idade.
- Reprovação por renda.
- Reprovação por valor solicitado.
- Retorno de todos os motivos quando vários critérios falham.

### Campos obrigatórios

- Idade vazia.
- Renda vazia.
- Valor solicitado vazio.
- Todos os campos vazios.

Os testes verificam as mensagens, a marcação `aria-invalid`
e a ausência de resultado da análise.

### Dados inválidos

- Idade negativa.
- Idade acima de 120.
- Idade fracionária.
- Idade com letras.
- Renda igual a zero.
- Renda negativa.
- Renda com três casas decimais.
- Valor solicitado igual a zero.
- Valor solicitado negativo.
- Valor solicitado com três casas decimais.

Na interface atual, valores monetários negativos são rejeitados
pela validação de formato.

### Formatos decimais e limpeza

- Aceitação de vírgula decimal.
- Aceitação de ponto decimal.
- Limpeza dos campos e ocultação do resultado.
- Remoção das mensagens e marcações de erro.

### Verificação do ambiente

O teste `AmbienteWebTest` abre uma página simples incluída no
próprio teste e verifica se o Selenium consegue controlar o Chrome.

Ele não depende do simulador nem de uma página externa.

### Ciclo de execução

Para cada classe que herda de `BaseWebTest`:

1. A aplicação local é iniciada em uma porta disponível.
2. Cada cenário recebe uma nova sessão do Chrome.
3. O Page Object abre o simulador.
4. O teste interage com a interface e verifica os resultados.
5. Em caso de falha, a extensão tenta salvar uma captura de tela.
6. O navegador é encerrado após cada cenário.
7. A aplicação é encerrada ao final da classe.

Os testes são executados sequencialmente na configuração atual.

## Pré-requisitos

- JDK 17 instalado.
- Google Chrome instalado.
- Acesso à internet na primeira execução para baixar o Gradle,
  as dependências e os componentes necessários ao driver.

Não é necessário instalar o Gradle separadamente.

O projeto foi validado localmente no Windows, com Chrome visível
e em modo headless.

A suíte completa também foi executada com sucesso no GitHub Actions,
utilizando Ubuntu, Java 17 e Chrome em modo headless.

## Executar toda a suíte

Execute os comandos na raiz do projeto.

Inclui os 24 cenários do simulador e o teste de ambiente.

### Windows PowerShell

```powershell
.\gradlew.bat test
```

### Linux ou macOS

```bash
bash ./gradlew test
```

Não é necessário iniciar a aplicação manualmente.

Se as entradas e saídas estiverem atualizadas, o Gradle pode
informar `UP-TO-DATE`, reutilizando o resultado anterior.

Para forçar uma nova execução no Windows:

```powershell
.\gradlew.bat test --rerun-tasks
```

No Linux ou macOS:

```bash
bash ./gradlew test --rerun-tasks
```

## Executar somente os 24 testes do simulador

### Windows PowerShell

```powershell
.\gradlew.bat test --tests "credito.SolicitacaoCreditoTest" --tests "credito.ValidacaoFormularioTest" --tests "credito.DadosInvalidosTest" --tests "credito.ComportamentoFormularioTest"
```

### Linux ou macOS

```bash
bash ./gradlew test \
  --tests "credito.SolicitacaoCreditoTest" \
  --tests "credito.ValidacaoFormularioTest" \
  --tests "credito.DadosInvalidosTest" \
  --tests "credito.ComportamentoFormularioTest"
```

## Executar uma classe específica

Exemplo no Windows PowerShell:

```powershell
.\gradlew.bat test --tests "credito.DadosInvalidosTest"
```

No Linux ou macOS:

```bash
bash ./gradlew test --tests "credito.DadosInvalidosTest"
```

O relatório dessa execução apresenta os testes selecionados.

## Execução headless

O modo headless executa o navegador sem exibir uma janela.

### Windows PowerShell

```powershell
$env:HEADLESS = "true"

try {
    .\gradlew.bat test --rerun-tasks
}
finally {
    Remove-Item Env:HEADLESS -ErrorAction SilentlyContinue
}
```

Esse bloco executa toda a suíte e remove a variável ao terminar.

### Linux ou macOS

```bash
HEADLESS=true bash ./gradlew test --rerun-tasks
```

No modo headless, o navegador utiliza a configuração de janela
de 1440 × 1000.

Na execução com janela visível, o Chrome é maximizado.

## Relatórios

O Gradle gera:

| Caminho | Finalidade |
| --- | --- |
| `build/reports/tests/test/index.html` | Resumo HTML, resultados e detalhes dos testes |
| `build/test-results/test/` | Resultados XML compatíveis com ferramentas de CI |

Para abrir o relatório local no Windows PowerShell:

```powershell
Start-Process ".\build\reports\tests\test\index.html"
```

O relatório apresenta quantidade de testes, falhas, testes ignorados,
duração e resultados por classe.

Os relatórios são arquivos gerados e não são versionados.

No GitHub Actions, eles são disponibilizados como artefatos
da execução.

## Evidências de falha

A extensão `EvidenciaFalhaExtension` tenta salvar uma captura:

- Quando o corpo do teste lança uma exceção.
- Quando um método `@BeforeEach` falha e há navegador disponível.

As capturas são realizadas antes do encerramento do navegador.

Pasta de saída:

```text
build/evidencias/screenshots/
```

Os nomes incluem classe, método, etapa e um identificador único.

Para abrir a pasta no Windows PowerShell:

```powershell
Start-Process ".\build\evidencias\screenshots"
```

A pasta é criada quando uma captura é gerada.

A captura registra a área visível da página no momento da falha,
não necessariamente a página inteira.

Os PNGs são arquivos separados e não estão incorporados
ao relatório HTML do Gradle.

Se a captura falhar, a exceção original do teste é preservada.

A geração do PNG foi verificada com um teste de falha proposital,
removido após a confirmação.

O comando `gradlew clean` remove a pasta `build`, incluindo
relatórios e capturas anteriores.

## Integração contínua com GitHub Actions

O workflow está definido em:

```text
.github/workflows/testes-web.yml
```

A execução é iniciada:

- A cada push na branch `main`.
- Em pull requests direcionados à branch `main`.
- Manualmente pela opção `Run workflow` na aba `Actions`.

### Etapas da execução

1. Baixar o código do repositório.
2. Configurar o Java 17, distribuição Eclipse Temurin.
3. Configurar o cache de dependências do Gradle.
4. Conferir as versões do Java e do Chrome.
5. Permitir a execução do Gradle Wrapper.
6. Compilar o projeto e executar os 25 testes em modo headless.
7. Disponibilizar os relatórios e as capturas geradas.

O ambiente utiliza `ubuntu-24.04` e a variável `HEADLESS=true`.

O comando executado é:

```bash
./gradlew --no-daemon --console=plain clean test
```

A aplicação utilizada pelos testes é iniciada automaticamente
no próprio ambiente do GitHub Actions.

### Resultado validado

A execução inicial no GitHub Actions concluiu os 25 testes
com sucesso, sem falhas e sem testes ignorados.

Esse resultado corresponde aos cenários implementados e
não significa ausência de defeitos ou cobertura total.

### Baixar o relatório

1. Acesse a aba `Actions` do repositório.
2. Selecione uma execução do workflow `Testes Web Java`.
3. Na seção `Artifacts`, baixe `relatorios-testes-web-java`.
4. Extraia todo o arquivo ZIP.
5. Abra o arquivo `reports/tests/test/index.html` dentro
   da pasta extraída.

Mantenha os arquivos e as pastas extraídos juntos para preservar
os estilos e a navegação do relatório.

O artefato inclui os arquivos existentes nestes caminhos:

- `build/reports/tests/test/`
- `build/test-results/test/`
- `build/evidencias/screenshots/`

As capturas são incluídas somente quando forem geradas.

O envio do artefato é tentado mesmo quando uma etapa anterior falha.
A disponibilidade dos arquivos depende do ponto em que ocorreu a falha.

A retenção dos artefatos está configurada para **14 dias**.

### Escopo da integração

A configuração atual implementa integração contínua: compilação,
execução dos testes e disponibilização dos resultados.

Ainda não realiza implantação automática da aplicação.

## Executar o simulador manualmente

### Windows PowerShell

```powershell
.\gradlew.bat run
```

### Linux ou macOS

```bash
bash ./gradlew run
```

Endereços padrão:

| Recurso | Endereço |
| --- | --- |
| Simulador | [http://127.0.0.1:8080/simulador](http://127.0.0.1:8080/simulador) |
| Swagger UI | [http://127.0.0.1:8080/docs](http://127.0.0.1:8080/docs) |
| Especificação OpenAPI | [http://127.0.0.1:8080/openapi.json](http://127.0.0.1:8080/openapi.json) |
| Disponibilidade | [http://127.0.0.1:8080/health](http://127.0.0.1:8080/health) |

Esses endereços são locais e dependem da aplicação em execução.

O processo permanece ativo enquanto o servidor está em execução.

Para encerrar, pressione `Ctrl + C` no terminal.

A raiz `/` não possui página e retorna HTTP 404.

### Configuração por variáveis de ambiente

| Variável | Padrão | Finalidade |
| --- | --- | --- |
| `HOST` | `127.0.0.1` | Endereço de escuta da aplicação |
| `PORT` | `8080` | Porta da aplicação |
| `HEADLESS` | `false` | Execução dos testes sem janela quando definida como `true` |

Os testes do simulador criam sua própria aplicação local em porta
disponível, independentemente do `HOST` e `PORT` da execução manual.

## Utilizar o simulador

Exemplo de solicitação aprovada:

| Campo | Valor |
| --- | ---: |
| Idade | 30 |
| Renda mensal | 3000,00 |
| Valor solicitado | 10000,00 |

Clique em **Analisar crédito**.

O resultado esperado é **Solicitação aprovada**.

Para verificar uma reprovação por idade, altere a idade para `17`
e envie novamente.

O botão **Limpar** remove os valores, as mensagens de erro
e o resultado exibido.

## Regras fictícias

### Validação dos dados

- Todos os campos são obrigatórios.
- Idade deve ser um número inteiro entre 0 e 120.
- Renda mensal deve ser maior que zero.
- Valor solicitado deve ser maior que zero.
- Valores monetários devem ter no máximo duas casas decimais.

A interface aceita vírgula ou ponto decimal e não aceita
separadores de milhares.

Na chamada direta à API, os valores devem ser números JSON,
utilizando ponto como separador decimal.

### Critérios de aprovação

Todos os critérios devem ser atendidos:

- Idade igual ou superior a 18 anos.
- Renda mensal igual ou superior a R$ 2.000,00.
- Valor solicitado menor ou igual a cinco vezes a renda mensal.

Quando vários critérios falham, todos os motivos são retornados.

Uma solicitação válida reprovada por regra de negócio também
retorna HTTP 200, com status `REPROVADO`.

## Endpoints

| Método | Caminho | Finalidade |
| --- | --- | --- |
| GET | `/simulador` | Entrega a interface HTML |
| GET | `/health` | Consulta a disponibilidade da aplicação |
| POST | `/analises-credito` | Analisa uma solicitação |
| GET | `/docs` | Entrega a interface Swagger |
| GET | `/docs/` | Caminho alternativo da interface Swagger |
| GET | `/openapi.json` | Entrega a especificação OpenAPI |

### Exemplo de análise

Requisição:

```http
POST /analises-credito
Content-Type: application/json
```

Corpo:

```json
{
  "idade": 30,
  "rendaMensal": 3000.00,
  "valorSolicitado": 10000.00
}
```

Resposta HTTP 200:

```json
{
  "status": "APROVADO",
  "motivos": []
}
```

### Exemplo de reprovação

Para idade `17`, com renda e valor dentro dos critérios:

```json
{
  "status": "REPROVADO",
  "motivos": [
    "Solicitante deve ter pelo menos 18 anos."
  ]
}
```

### Respostas HTTP

| Código | Situação |
| --- | --- |
| 200 | Página, documentação, consulta ou análise disponibilizada |
| 400 | JSON ou dados de entrada inválidos |
| 404 | Endpoint não encontrado |
| 405 | Método HTTP não permitido |
| 413 | Corpo da requisição acima de 65.536 bytes |
| 415 | Content-Type não aceito |
| 500 | Recurso de documentação não encontrado na aplicação |

Os erros tratados pelo servidor utilizam o campo `erro`.

O endpoint `/health` retorna `{"status":"UP"}` e não verifica
banco de dados ou serviços externos.

## Swagger e OpenAPI

Com a aplicação em execução:

1. Acesse `/docs`.
2. Expanda `POST /analises-credito`.
3. Clique em `Try it out`.
4. Selecione um exemplo ou edite o JSON.
5. Clique em `Execute`.
6. Confira o código HTTP e o corpo em `Server response`.

A interface carrega os arquivos CSS e JavaScript do Swagger UI
pela CDN, com versão fixada em `5.32.0`.

O navegador precisa de acesso à internet para carregar esses arquivos.

A especificação `openapi.json` é mantida manualmente.
Alterações nos endpoints, modelos ou comportamentos devem ser
refletidas nesse arquivo.

As restrições dos modelos OpenAPI não representam todas as
particularidades da representação decimal recebida pelo servidor.

A abertura do Swagger e o envio de uma análise foram
verificados manualmente.

Os 24 testes do simulador não automatizam a interface Swagger.

## Limites e escopo atual

- Projeto educacional com regras fictícias.
- Sem autenticação ou persistência em banco de dados.
- Sem dados reais de clientes.
- Sem integração com serviços financeiros externos.
- Automação validada com Chrome.
- Não contempla uma matriz completa de navegadores ou dispositivos.
- Não inclui testes de carga, segurança ou acessibilidade.
- Testes aprovados não representam cobertura total.
- A especificação OpenAPI exige manutenção manual.
- A documentação interativa depende da CDN do Swagger UI.
- As capturas dependem de uma sessão de navegador disponível.
- Hospedagem pública e implantação automática ainda não configuradas.

## Autora

Letícia Gonçalves Garcia — QA Sênior

[Portfólio](https://leticiagbrito.github.io/) ·
[GitHub](https://github.com/LeticiaGBrito)