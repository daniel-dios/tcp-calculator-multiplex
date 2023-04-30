# tcp-calculator-multiplex

Cuarto ejercicio de programación: Un Acumulador Remoto usando la técnica de multiplexación

## Objetivo

El propósito de este ejercicio es desarrollar dos aplicaciones en red que se comunican según el modelo cliente-servidor,
tcpmpcli y tcpmpser. El comando tcpmpcli establecerá una conexión TCP con tcpmpser, y posteriormente le enviará
operaciones aritméticas. Tras recibir la operación, tcpmpser actualizará el valor de un acumulador interno sumando
el resultado de la operación solicitada (AC ← AC + resultado), devolviendo este valor del acumulador al cliente tcpmpcli

Esta nueva versión de tcpmpser deberá ser capaz de atender simultáneamente a varios clientes, cada uno de los cuales
tendrá su propio acumulador independiente.

Los detalles de ambos programas son los siguientes:

## El cliente (tcpmtcli)

Sinopsis
tcpmtcli direccion_ip_servidor numero_puerto_servidor

### Comportamiento

Inmediatamente tras el inicio, el programa establece una conexión TCP con la direccion_ip_servidor y
numero_puerto_servidor indicados, y espera a que el usuario introduzca una operación artitmética desde el terminal.
Si el usuario introduce QUIT entonces el programa finaliza, en otro caso envía la operación al servidor y espera por
la respuesta. Cuando llega la respuesta, muestra por pantalla el valor devuelto por el servidor y solicita una nueva
operación. El programa debería indicar claramente cómo se deben introducir las operaciones, auque se recomienda usar
la habitual notación infija. Se supone que todos los números introducidos son enteros en el rango -128 a 127 (
inclusive).

No obstante, el formato del mensaje que se envía al servidor y el conjunto de operaciones a implementar deben seguir el
formato exacto definido en el Anexo I.

### Comportamiento opcional

Uso de UDP en vez de TCP (8 puntos)
Si la línea de comandos del cliente incluye el flag -u, el programa usará UDP en vez de TCP para comunicarse con el
servidor.

## El servidor (tcpmtser)

Sinopsis
tcpmtser num_puerto

### Comportamiento

Tras arrancar espera la llegada de peticiones de conexión de clientes. Al inicio de cada conexión individual, establece
el valor del acumulador para esa conexión en particular a 0. Luego, deberá procesar cada operación aritmética recibida
y actualizar el acumulador independiente sumando a éste el resultado de dicha operación. Tanto la operación aritmética
recibida como su resultado son mostrados por pantalla en el servidor junto a la identificación del cliente
(IP y puerto origen). Adicionalmente, se responde al cliente enviándole el valor actual del acumulador.

Si la operación no puede ser realizada o se produce un error (ej. división por cero,
factorial de un número negativo, desbordamiento, etc.), el acumulador no será modificado. En este caso, se enviará al
cliente un mensaje de error y el valor del acumulador. A continuación, el servidor espera a que llegue la siguiente operación del
cliente como siempre. Es obligatorio detectar, como mínimo, la división por cero, devolviendo el error correspondiente
al cliente.

El formato del mensaje enviado al cliente por la red se describe en el Anexo I.

Recuerde que la aplicación servidor nunca finaliza.

Es obligatorio usar las capacidades de multiplexación para la gestión de los sockets del lenguaje de programación para
esta versión del servidor.

En el caso de Java, esto significa usar el paquete java.nio, más específicamente, las clases Selector,
ServerSocketChannel, SocketChannel y las relacionadas con ellas.
En el caso de C/C++, sugerimos la utilización de epoll (7)  (o la llamada al sistema poll (2) si queremos mayor
portabilidad).
En el caso de Rust, se puede usar la unidad compilada mio.
En el caso de Python, se puede usar el modulo Selectors.

Asegúrese que los clientes no interfieren unos con otros cuando acceden al servidor. Es decir, debe parecer que el
servidor se halla siempre desocupado desde el punto de vista de los clientes.
En esta versión está prohibido usar las capacidades multihilo del lenguaje de programación.

### Comportamiento opcional

El servidor puede aceptar peticiones UDP, además de las TCP. En este caso, todos los clientes UDP compartirán un mismo
acumulador, como en el primer ejercicio de programación.

## Comprobación de los argumentos

Ambos programas solo deben comprobar que el número de argumentos es correcto. Si la comprobación falla, el programa
mostrará por pantalla un mensaje con la sintaxis correcta, y finalizará su ejecución. No es necesario comprobar que el
formato de la IP es correcto (cliente) o que el puerto está disponible (servidor). No haremos ninguna prueba jugando con
argumentos de formato incorrecto.

## Anexo I

Todos los mensajes intercambiados entre cliente y servidor debe ser codificados siguiendo un formato TLV. El tipo será
un entero de 8 bits representado la operación, la longitud será otro entero de 8 bits indicando el número de bytes
usado por el valor, y finalmente se añade el valor propiamente dicho. Finalmente, los contenidos del campo valor
dependen del tipo del mensaje. Todas las fantidades de más de un byte usará el formato big-endian.

### Mensajes del Cliente

En el caso del cliente se han definido los siguientes tipos de mesnajes:

| tipo	 | longitud | valor                           | Comentario                                                 |
|-------|----------|---------------------------------|------------------------------------------------------------|
| 1     | 2        | dos enteros de 8 bits con signo | La suma de los números enviados                            |
| 2     | 2        | dos enteros de 8 bits con signo | La resta de los números enviados                           |
| 3     | 2        | dos enteros de 8 bits con signo | La multiplicación de los números enviados                  |
| 4     | 2        | dos enteros de 8 bits con signo | La parte de la división del primer número entre el segundo |
| 5     | 2        | dos enteros de 8 bits con signo | El resto de la división del primer número entre el segundo |
| 6     | 1        | un entero de 8 bits con signo   | El factorial del número enviado                            |

Por ejemplo [1,2,120,54] debería dar como resultado 174, [5,2,11,3] debería dar como resultado 2 y [2,2,5,10] debería
dar como resultado -5.

### Respuestas del Servidor

El formato de las respuesta del servidor es el siguiente:

| tipo | longitud | valor                       | Comentario                                                                                                          |
|------|----------|-----------------------------|---------------------------------------------------------------------------------------------------------------------|
| 10   | variable | Uno o más campos TLV        | Una respuesta conteniendo un valor del acumulador o un mensaje de error                                             |
| 11   | variable | String UTF-8                | Un mensaje de error si el cálculo no pudo ser realizado                                                             |
| 16   | 8        | entero de 64 bits con signo | Un valor del acumulador que se representa como un entero de 64 bits con signo en formato big-endian ([-2⁶³,2⁶³-1]). |

Por ejemplo, si el servidor tiene que enviar al cliente un valor del acumulador igual a -525, el mensaje se codificaría
como: [10,10,16,8,255,255,255,255,255,255,253,243]. Y si el valor a devolver fuese el mensaje de error “Can not divide
by 0”, y el valor del acumulador fuese 5, el mensaje sería, o
bien [10,31,11,19,67,97,110,32,110,111,116,32,100,105,118,105,100,101,32,98,121,32,48,16,8,0,0,0,0,0,0,0,5] o
bien [10,31,16,8,0,0,0,0,0,0,0,5,11,19,67,97,110,32,110,111,116,32,100,105,118,105,100,101,32,98,121,32,48], es decir,
el orden del mensaje de error y del valor del acumulador dentro de la respuesta no importa.
