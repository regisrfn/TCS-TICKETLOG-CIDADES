# ITEM - API
Spring boot rest api

## PREREQUISITES
- Java
- Docker | https://docs.docker.com/engine/install/ubuntu/ 

## DOCKER
- RUNNING POSTGRES
- create:
docker run --name ticketlog_db -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=ticketlog -d postgres:alpine
- stop:
docker stop postgres
- start:
docker start postgres

## DATABASE DESCRIPTION

|       Cidades         |
|--------------------   |
|id: UUID (PK)          |
|nome: String           |
|populacao: Long        |
|custoCidadeUs: String  |
|idEstado: UF(0,1,2)    |

## API

#### GET METODOS
Lista de cidades -> http://localhost:5050/api/v1/cidade <br/>
Obter uma cidade -> http://localhost:5050/api/v1/cidade/get/{id}<br/>
Obter todas cidades -> http://localhost:5050/api/v1/cidade/get<br/>
Obter todas cidades de um estado -> http://localhost:5050/api/v1/cidade/{uf} , onde uf = sc,pr ou rs<br/>

#### POST METODOS
Salvar uma cidade - http://localhost:5050/api/v1/cidade/save<br/>
Salvar uma lista de cidades -> http://localhost:5050/api/v1/cidade/savelist

#### PUT METODOS
Deletar uma lista de cidades -> http://localhost:5050/api/v1/cidade/deletelist

#### DELETE METODOS
Deletar uma unica cidade -> http://localhost:5050/api/v1/cidade/delete/{id}