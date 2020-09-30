#Teste Técnico SouthSystem

###procedimentos para execução
##### 1 - clonar repositório
##### 2 - buildar: mvn clean install
##### 3 - executar: mvn spring-boot:run

###Observação
##### 1 - a pasta padrão é definida com base numa varíável de ambiente com o nome "HOMEPATH". Esta precisa ser previsamente setada.
##### 2 - extensão aceita para o processamento: ".dat"
##### 3 - após iniciado, a cada 30 segundos o server escaneia a pasta padrão de entrada para processamento dos arquivos.
##### 4 - o resumo do processamento é gerado na pasta padrão de saída.