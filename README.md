
# Agendamento de Reuniões

Aplicação web desenvolvida em Java para organizar reuniões e consultar a disponibilidade dos participantes.

O sistema permite que um proponente crie uma reunião, defina possíveis datas e horários e convide outras pessoas. Cada participante recebe um token individual para registrar sua disponibilidade. Com base nas respostas, o proponente pode consultar os resultados e escolher um horário definitivo.

Projeto acadêmico desenvolvido para a disciplina de Linguagem de Programação Orientada a Objetos II (LPOO2).

## 1. Funcionalidades

- Criação de reuniões com título, descrição, local e dados do proponente.
- Definição de um intervalo de datas para a reunião.
- Geração de horários em três modalidades:
  - **DIA:** uma opção por dia.
  - **PERIODOS:** opções de manhã, tarde e noite.
  - **HORAS:** intervalos de duração configurável.
- Cadastro do proponente como participante.
- Cadastro de convidados durante a criação ou posteriormente.
- Geração de tokens individuais para os participantes.
- Registro e atualização das respostas `SIM`, `TALVEZ` e `NAO`.
- Remoção de respostas de disponibilidade.
- Consulta dos resultados e classificação dos horários.
- Escolha de um horário definitivo para a reunião.
- Bloqueio de alterações de disponibilidade após a finalização.
- Consulta pública do resultado mediante token público.
- Gerenciamento administrativo mediante token administrativo.
- Exclusão de reuniões e de seus dados relacionados.
- Simulação do envio de convites por e-mail.
- Suporte ao envio SMTP mediante configuração do servidor.

## 2. Tecnologias utilizadas

| Tecnologia | Utilização |
|---|---|
| Java 17 | Implementação da aplicação |
| Jakarta Servlet 6.0 | Tratamento das requisições HTTP |
| JSP e JSTL | Construção das páginas web |
| JDBC | Comunicação com o banco de dados |
| MySQL 8 | Armazenamento dos dados |
| Maven | Gerenciamento das dependências e compilação |
| Apache Tomcat 10.1 | Servidor de aplicações |
| Jakarta Mail / Angus Mail | Envio de e-mails |
| JUnit 5 | Testes automatizados |
| Git e GitHub | Versionamento do projeto |

A aplicação utiliza a arquitetura web tradicional com Servlets, serviços, DAOs JDBC, modelos Java e páginas JSP.

## 3. Requisitos para execução

Para executar o sistema, é necessário instalar e configurar:

- JDK 17;
- Apache Maven;
- MySQL 8;
- Apache Tomcat 10.1;
- Um navegador web.

O MySQL deve estar em execução na porta `3306`, conforme a configuração atual da classe `Conexao`.

Para verificar as ferramentas instaladas:

```powershell
java -version
mvn -version
```

O projeto deve ser compilado com Java 17.

## 4. Preparação do banco de dados

O script principal está localizado em:

```text
banco/schema.sql
```

Abra o MySQL Workbench, conecte-se ao servidor e execute o conteúdo desse arquivo.

O script cria o banco:

```sql
agendamento_reunioes
```

E as tabelas:

| Tabela | Responsabilidade |
|---|---|
| `reuniao` | Dados das reuniões e tokens de acesso |
| `horario` | Datas, períodos e intervalos disponíveis |
| `participante` | Proponente e convidados |
| `disponibilidade` | Respostas dos participantes |
| `reuniao_finalizacao` | Horário definitivo de cada reunião |

Para verificar a instalação:

```sql
USE agendamento_reunioes;
SHOW TABLES;
```

**Importante:** o `schema.sql` é destinado à criação da estrutura. Como utiliza `CREATE TABLE IF NOT EXISTS`, não substitui um processo de migração para bancos já existentes com estruturas antigas.

## 5. Configuração da conexão MySQL

A aplicação utiliza variáveis de ambiente para obter as credenciais do banco, sem armazenar a senha no código-fonte.

| Variável | Descrição |
|---|---|
| `DB_USER` | Usuário do MySQL |
| `DB_PASSWORD` | Senha do MySQL |
| `DB_NAME` | Nome do banco; opcional |

Quando `DB_NAME` não é definida, a aplicação utiliza:

```text
agendamento_reunioes
```

No Windows PowerShell, um exemplo de configuração é:

```powershell
$env:DB_USER = "root"
$senha = Read-Host "Senha do MySQL" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $senha).Password
Remove-Variable senha
```

Essas variáveis são definidas no ambiente do terminal atual e são herdadas pelos processos iniciados a partir dele.

O Tomcat também precisa receber as variáveis de ambiente. Caso seja iniciado em outro terminal ou como serviço do Windows, configure-as no ambiente desse processo.

Não publique senhas em arquivos versionados, prints ou no repositório.

## 6. Compilação

Abra o terminal na pasta principal do projeto, onde está localizado o `pom.xml`.

Execute:

```powershell
mvn clean package
```

O Maven compilará a aplicação, executará os testes unitários identificados pelo Surefire e gerará o arquivo:

```text
target/agendamento-reuniao.war
```

O empacotamento utiliza o plugin `maven-war-plugin`.

## 7. Implantação no Tomcat

Com o Tomcat 10.1 instalado e configurado:

1. Compile o projeto utilizando `mvn clean package`.
2. Pare o Tomcat caso esteja executando uma versão anterior da aplicação.
3. Copie `target/agendamento-reuniao.war` para a pasta `webapps` do Tomcat.
4. Inicie o Tomcat com as variáveis de conexão configuradas.
5. Aguarde a implantação do arquivo WAR.

Se o Tomcat estiver instalado em `C:\Tomcat10`, no PowerShell:

```powershell
Copy-Item ".\target\agendamento-reuniao.war" `
    "C:\Tomcat10\webapps\" -Force

& "C:\Tomcat10\bin\startup.bat"
```

Acesse:

```text
http://localhost:8080/agendamento-reuniao/reunioes/nova
```

A porta `8080` corresponde à configuração padrão utilizada no ambiente de desenvolvimento. Se o Tomcat utilizar outra porta, ajuste o endereço.

Ao atualizar uma implantação existente, pare o Tomcat e faça a substituição do WAR de maneira controlada para evitar o uso de arquivos antigos.

## 8. Como utilizar o sistema

### 8.1. Criar uma reunião

Acesse a página de criação e informe:

- Título da reunião;
- Descrição e local, quando necessário;
- Nome e e-mail do proponente;
- Data inicial e data final;
- Modalidade de divisão dos horários;
- Configurações de horário, quando aplicáveis;
- Convidados iniciais, caso existam.

A aplicação valida os dados, gera os horários e registra os participantes no banco.

### 8.2. Gerenciar convidados

O gerenciamento é acessado por meio do token administrativo da reunião.

Na área administrativa, o proponente pode:

- Visualizar os participantes cadastrados;
- Adicionar convidados enquanto a reunião estiver aberta;
- Consultar os tokens individuais;
- Copiar convites;
- Processar o envio de e-mails;
- Consultar os resultados;
- Acessar a finalização;
- Solicitar a exclusão da reunião;
- Sair do gerenciamento.

O token administrativo deve ser mantido em sigilo.

### 8.3. Registrar disponibilidade

Cada participante utiliza seu token individual para acessar a página de disponibilidade.

As respostas possíveis são:

- **SIM:** disponível no horário;
- **TALVEZ:** disponibilidade incerta;
- **NAO:** indisponível.

As respostas podem ser atualizadas ou removidas enquanto a reunião estiver aberta.

Página:

```text
http://localhost:8080/agendamento-reuniao/reunioes/disponibilidade
```

### 8.4. Consultar os resultados

Os resultados permitem comparar os horários com base nas respostas dos participantes.

O sistema contabiliza as respostas por horário e apresenta informações para auxiliar a escolha do proponente.

Página:

```text
http://localhost:8080/agendamento-reuniao/reunioes/resultado
```

A consulta pública pode utilizar o token público da reunião.

### 8.5. Finalizar a reunião

Na área administrativa, o proponente escolhe um horário definitivo entre as opções da reunião.

Após a finalização:

- O horário escolhido fica registrado;
- A reunião não pode ser finalizada novamente;
- Novas respostas de disponibilidade são bloqueadas;
- O cadastro de novos participantes é bloqueado.

A finalização é protegida por transações e validações no serviço responsável.

### 8.6. Excluir uma reunião

A exclusão está disponível na área administrativa e exige confirmação explícita.

Ao excluir uma reunião, são removidos os dados relacionados, incluindo horários, participantes, disponibilidades e o registro de finalização.

A operação é permanente e utiliza uma transação no banco de dados.

## 9. Envio de convites por e-mail

O projeto permite processar convites em dois modos.

### Modo de simulação

O modo padrão é `console`, que registra os convites no console da aplicação sem enviar mensagens reais.

Para selecionar explicitamente esse modo:

```powershell
$env:EMAIL_MODE = "console"
```

A simulação não deve ser confundida com o envio efetivo de um e-mail.

### Modo SMTP

O projeto também possui implementação para envio de mensagens por SMTP.

Para selecionar o modo:

```powershell
$env:EMAIL_MODE = "smtp"
```

Esse modo exige as configurações de SMTP utilizadas pelo `SMTPEmailService`, além de um endereço público HTTPS para os links dos convites.

A variável do endereço público é:

```text
EMAIL_PUBLIC_BASE_URL
```

O endereço deve incluir o contexto da aplicação, por exemplo:

```text
https://exemplo.com/agendamento-reuniao
```

Esse endereço é apenas ilustrativo: deve ser substituído por uma URL real, válida e acessível aos participantes.

As credenciais SMTP devem ser configuradas no ambiente do servidor, nunca no repositório.

O modo de simulação pode ser utilizado para testar o fluxo de convites sem depender de um servidor SMTP externo.

## 10. Testes automatizados

O projeto utiliza JUnit 5 para verificar regras de negócio, persistência e concorrência.

### Testes unitários

Os testes do `GeradorHorariosTest` verificam:

- Geração por dia;
- Geração por períodos;
- Geração por horas;
- Datas iguais;
- Intervalos válidos e inválidos;
- Duração dos horários;
- Validação de entradas incorretas.

Para executar os testes unitários:

```powershell
mvn clean test
```

### Banco exclusivo de testes

Os testes de integração utilizam o banco:

```text
agendamento_reunioes_test
```

A estrutura pode ser criada executando:

```text
banco/schema_teste.sql
```

**Atenção:** os testes de integração criam, alteram e excluem registros. Não devem ser executados no banco principal da aplicação.

No PowerShell:

```powershell
$env:DB_NAME = "agendamento_reunioes_test"
```

Também é necessário configurar `DB_USER` e `DB_PASSWORD` no mesmo ambiente.

### Testes de integração

A classe `ReuniaoIntegracaoIT` verifica operações como:

- Cadastro de reuniões e convidados;
- Persistência dos horários;
- Registro e atualização de respostas;
- Finalização;
- Exclusão e limpeza dos registros;
- Rollback em caso de erro.

Para executar:

```powershell
mvn "-Dtest=ReuniaoIntegracaoIT" test
```

### Testes de concorrência

A classe `ConcorrenciaReuniaoIT` verifica situações como:

- Tentativas simultâneas de finalização;
- Bloqueio de alterações após a finalização;
- Consistência entre resposta e finalização concorrentes.

Para executar:

```powershell
mvn "-Dtest=ConcorrenciaReuniaoIT" test
```

Para executar os dois conjuntos de integração:

```powershell
mvn "-Dtest=ReuniaoIntegracaoIT,ConcorrenciaReuniaoIT" test
```

Os testes de integração utilizam o sufixo `IT` e são executados explicitamente pelos comandos acima.

Após os testes, remova a seleção do banco de testes:

```powershell
Remove-Item Env:DB_NAME -ErrorAction SilentlyContinue
```

Isso restaura o nome padrão do banco para novas conexões no terminal atual.

## 11. Organização do projeto

```text
Agendamento-Reuniao/
|-- banco/
|   |-- schema.sql
|   |-- schema_teste.sql
|
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   `-- br/ufpr/tads/agendamento/
|   |   |       |-- dao/
|   |   |       |-- model/
|   |   |       |-- service/
|   |   |       |-- servlet/
|   |   |       `-- util/
|   |   |
|   |   `-- webapp/
|   |       `-- WEB-INF/
|   |           `-- views/
|   |
|   `-- test/
|       `-- java/
|           `-- br/ufpr/tads/agendamento/service/
|
|-- pom.xml
`-- README.md
```

As principais responsabilidades estão divididas em:

- **model:** classes que representam os dados do sistema;
- **dao:** operações de acesso ao banco;
- **service:** regras de negócio, validações e transações;
- **servlet:** processamento das requisições HTTP;
- **util:** recursos auxiliares, incluindo a conexão JDBC;
- **views:** páginas JSP da aplicação.

## 12. Cuidados de segurança e integridade

O projeto utiliza diferentes mecanismos para proteger as operações:

- Tokens administrativos e individuais;
- Validação de acesso nas rotas administrativas;
- Tokens CSRF nos formulários de alteração;
- Invalidação da sessão no logout;
- PreparedStatement nas operações JDBC;
- Restrições de integridade no MySQL;
- Transações para operações relacionadas;
- Bloqueios de banco com `SELECT ... FOR UPDATE`;
- Validação das respostas após a finalização.

Esses mecanismos reduzem riscos comuns, mas não dispensam configurações adicionais de segurança caso a aplicação seja disponibilizada publicamente na internet.

## 13. Considerações finais

O projeto reúne conceitos de orientação a objetos, desenvolvimento web Java, persistência de dados com JDBC, arquitetura em camadas, transações, concorrência e testes automatizados.

A aplicação foi organizada para separar a interface, as regras de negócio e o acesso ao banco de dados, facilitando a manutenção e a evolução do sistema.

O código-fonte, os scripts SQL e os testes estão disponíveis neste repositório.
