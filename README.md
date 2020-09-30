#Teste Técnico SouthSystem

##procedimentos para execução
##### 1 - clonar repositório

##### Linux/mac
##### 2 - buildar: ./mvnw clean compile
##### 3 - executar: ./mvnw spring-boot:run

##### Windows
##### 2 - buildar: ./mvnw.cmd clean compile
##### 3 - executar: ./mvnw.cmd spring-boot:run

###Observação
##### 1 - a pasta padrão é definida com base numa varíável de ambiente com o nome "HOMEPATH". Esta precisa ser previsamente setada.
##### 2 - extensão aceita para o processamento: ".dat"
##### 3 - após iniciado, a cada 30 segundos o server escaneia a pasta padrão de entrada para processamento dos arquivos.
##### 4 - o resumo do processamento é gerado na pasta padrão de saída.