<h1>Desenvolvimento de testes unitários para validar uma API REST de gerenciamento de estoques de cerveja.</h1>

### Projeto de estudo com base no livecoding do professor Rodrigo Peleias, da Digital Innovation One. 

Neste projeto, vamos aprender a testar, unitariamente, uma API REST para o gerenciamento de estoques de cerveja, desenvolvendo testes unitários para validar o sistema de gerenciamento de estoques de cerveja. 

Tópicos aboradados:

* Baixar um projeto através do Git para desenolver nossos testes unitários. 
* Foco nos testes unitários.
* Principais frameworks para testes unitários em Java: JUnit, Mockito e Hamcrest. 
* Desenvolvimento de testes unitários para validação de funcionalides básicas: criação, listagem, consulta por nome e exclusão de cervejas.
* TDD: apresentação e exemplo prático em 1 funcionalidade: incremento do número de cervejas no estoque.

Para executar o projeto no terminal, digite o seguinte comando:

```shell script
mvn spring-boot:run 
```

Para executar a suíte de testes desenvolvida durante a live coding, basta executar o seguinte comando:

```shell script
mvn clean test
```

Após executar o comando acima, basta apenas abrir o seguinte endereço e visualizar a execução do projeto:

```
http://localhost:8080/api/v1/beers
```





