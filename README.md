# Resumo #
 O trabalho visa utilizar a placa Intel Galileo v1.0 para controlar um sistema de secagem de grãos. Dentre os requerimentos do projeto existe a necessidade de utilizar um aplicativo externo que terá acesso ao status do sistema, bem como ligar ou desligar remotamente o sistema.De acordo com as especificações abaixo:

+ Ligar/Desligar sistema através de um botão/chave
+ Controlar PWM do driver do Fan
+ Ligar ou desligar LED 1 (Sistema Ligado/Desligado)
+ Controlar PWM do LED 2 (Sensor de Luminosidade)
+ Controlar PWM do LED 3 (Sendor relativo ao fluxo de ar/Amplitude do sinal de controle do FAN)
+ Manter o sistema se comportando de acordo com a curva de fluxo:

![graficodefluxo.jpg](https://bitbucket.org/repo/AKknRy/images/3321541813-graficodefluxo.jpg)

## APP ##

+ Utilizar Threads
+ Utilizar Sockets
+ O usuário deve poder visualizar informações relativas ao sensor(nível) em tela
+ O usuário deve poder visualizar o valor relativo ao fluxo em tela
+ O usuário deve poder ligar/desligar o sistema pelo App
+ O usuário deve poder ver o status do sistema(ligado/desligado)
+ [EXTRA - 1,0 Ponto]:
    * O usuário deve poder alterar a curva de fluxo do sistema via App, de forma a reconfigurar o comportamento do sistema
+ [Sugestões]:
    * ID e Senha
    * Bandeira do América de Natal tremulante em algum ponto da tela (bônus da amizade)
    * Qualquer coisa, adicionem mais...se bem que quanto menos, menos trabalho e mais horas de sono, mas a vida é breve na engenharia XD

# Tutoriais() #

## Time ##

+ https://www.cs.rutgers.edu/~pxk/416/notes/c-tutorials/gettime.html
+ http://stackoverflow.com/questions/3756323/getting-the-current-time-in-milliseconds


## Threads ##

+ https://computing.llnl.gov/tutorials/pthreads/
+ https://www.tutorialspoint.com/cplusplus/cpp_multithreading.htm
+ https://linuxprograms.wordpress.com/2007/12/29/threads-programming-in-linux-examples/
+ http://timmurphy.org/2010/05/04/pthreads-in-c-a-minimal-working-example/
+ https://medeubranco.wordpress.com/2008/07/10/threads-em-python/
+ http://imasters.com.br/artigo/20127/py/threads-em-python?trace=1519021197&source=single

## Sockets ##

+ https://beej.us/guide/bgnet/output/html/singlepage/bgnet.html
+ http://www.linuxhowtos.org/C_C++/socket.htm
+ http://www.binarytides.com/socket-programming-c-linux-tutorial/
+ http://www.tenouk.com/Module40b.html
## MRAA ##

+ http://iotdk.intel.com/docs/master/mraa/

# Contato: #

## Desenvolvedores ##

### Caio Vilar ###

 - caio.b.vilar@gmail.com

### Dhiogo Boza ###

 - dhiogoboza@gmail.com

### Leonardo Augusto ###

 - leoaugustoam@gmail.com

### Miguel Rocha Jr. ###

 - miguelsrochajr@gmail.com

# Pauta Reunião #1 #

+ Servidor:
    * Worker Threads
    * Led 2
    * Led 3
    * ADC(Sensor)
    * Driver
    * Enviar dados -> cliente [curva atual,valor do led 2,valor do led 3,status(ligado/desligado),valor do driver]
    * Receber dados <- cliente [curva nova](supostamente 10 pontos);
    * Estrutura da curva de fluxo:  
       * Pares ordenados com tempo e amplitude(t,a);


+ Cliente:

    * Recebe dados do servidor[curva atual,valor do led 2, valor do led 3, status(ligado/desligado),valor do driver]
    * Envia[curva nova]
    * Mostrar curva atual num grafico
    * Mostrar curva que foi configurada num grafico
    * Mostrar Status
    * Mostrar valores dos leds
    * Mostrar valor do driver
    * Mostar bandeira do america 10/10 com um golfinho saltitante.

+ ~~Discutir abordagem para cada módulo~~
   * Threads necessárias:
       * Thread que recebe dados do stream do socket(TCP/IP)
       * Thread que envia dados via stream-socket(TCP/IP)
+ ~~Modelar Software~~
+ ~~Distribuir o desenvolvimento dos módulos entre os contribuidores~~
+ ~~Modelo de documentação.~~

# To Do #
 
+ ~~ Modelar Diagrama de Caso de Uso (básico) ~~ 
+ https://www.lucidchart.com/pt
+ Modelar Diagrama de Classes (básico)
+ Implementar módulo de comunicação via socket
+ Implementar módulo de controle do ADC
+ Implementar módulo de controle PWM (driver do FAN e LEDS)
+ Implementar módulo de status do botão(Thread Principal/Prioridade mais alta)