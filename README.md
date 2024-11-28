# Reactor Core/Web Reactive project

Aplicação em Java que utiliza a framwork Spring WebFlux que realiza consultas a uma API REST para manipulação de dados sobre media e utilizadores, utilizando programação reativa.

### Estrutura

O projeto está estruturado em 3 partes:
- Um Servidor que fornece uma API REST para expor os dados da media e users.
- Uma base de dados PostgreSQL para guardar as informações (ficheiro no folder "bd" com o script para as tabelas)
- Uma aplicação cliente que efetua consultas HTTP para obter os dados e posteriormente manipula-os 

### Configuração

- Java
- Maven
- PostgreSQL