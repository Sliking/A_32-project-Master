# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 32 - Campus Alameda

Miguel Pinto, 79060, miguelpinto25@hotmail.com  
Guilherme Pinho, 78819, guilherme.pinho@ist.utl.pt  
André Vieira, 79591, andre.pa.vieira@hotmail.com


Repositório:
[tecnico-distsys/A_32-project](https://github.com/tecnico-distsys/A_32-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Linux


[1] Iniciar servidores de apoio (já estão instalados na RNL)

JUDDI:
```
cd juddi-3.3.2_tomcat-7.0.64_9090/bin/
./startup.sh
```


[2] Obter código fonte do projeto (versão entregue)

```
git clone https://github.com/tecnico-distsys/A_32-project.git 
```


[3] Instalar módulos de bibliotecas auxiliares

***UDDI***
```
cd uddi-naming
mvn clean install
```

***Handlers***
```
cd ws-handlers
mvn clean install
```

-------------------------------------------------------------------------------
### Serviço Certificate Authority

[1] Construir o **servidor**

```
cd CA-ws
mvn clean install
```

[2] Copiar a pasta **keys_2016_05_06__14_33_11/ca** para **CA-ws/target/classes**

[3] Executar o **servidor**

```
mvn exec:java
```

[4] Instalar a dependência do **cliente** na biblioteca maven

```
cd CA-ws-cli
mvn clean install
```

-------------------------------------------------------------------------------

### SecurityClass

[1] Instalar a dependência da **SecurityClass** na biblioteca maven

```
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir o **servidor** (Simulação com 2 servidores)

```
cd transporter-ws
mvn clean install
```

[2] Copiar as pastas **keys_2016_05_06__14_33_11/transportadora1-ws** e **keys_2016_05_06__14_33_11/transportadora2-ws** para **transportadora-ws/target/classes**

[3] Executar os **servidores**

```
mvn exec:java
```

```
mvn -Dws.i=2 exec:java
```

[4] Instalar a dependência do **cliente** na biblioteca maven

```
cd transporter-ws-cli
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Instalar a dependência do **cliente** na biblioteca maven

```
cd broker-ws-cli
mvn clean install
```

[2] Construir o **servidor**

```
cd broker-ws
mvn clean install
```

[3] Copiar a pasta **keys_2016_05_06__14_33_11/broker-ws** para **broker-ws/target/classes**

[4] Executar o **servidor secundário**

```
mvn -Dws.i=4 -Dprimary=secondary exec:java
```

[3] Executar o **servidor primário**

```
mvn exec:java
```

[2] Executar o **cliente**

```
cd broker-ws-cli
mvn exec:java
```


-------------------------------------------------------------------------------
**FIM**
