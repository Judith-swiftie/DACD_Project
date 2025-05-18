### Breve descripción del proyecto y su propuesta de valor.
El proyecto recoge datos de conciertos y festivales de Ticketmaster y a partir de Spotify crea playlist personalizadas 
para cada uno de estos eventos. La motivación de haber escogido este proyecto ya que la música forma parte de la vida de todos y los conciertos y festivales 
con música en vivo forma parte de nuestra cultura...

### Justificación de la elección de APIs y estructura del datamart.
Se han escogido la API de Spotify porque es una de las platafaormas de musica mas utilizadas mundialmente actualmente.
Por otro lado se ha escogido la de Ticketmaster para la obtención de eventos ya que es una de las plataformas
que disponen de muchisimos eventos relacionados con la música.
Ambas son Apis web del tipo REST (Representational State Transfer), ambas exponen sus datos como recursos
accesibles a través de URLs, utilizando métodos HTTP 
La interacción con el usuario se realiza con una interfaz de linea de comando (CLI).
El Datamart es una base de datos que obtiene tanto datos historicos en formato JSON de todos los .event como también es capaz de obtener
datos en tiempo real puesto que también está subscrito al broker.

### Instrucciones claras para compilar y ejecutar cada módulo.
Para ejecutar el proyecto y generar una playlist para un concierto o festival en concreto se deberá ejecutar el Main del módulo playlists-for-events,
pásandole como variable de entorno la base de datos del datamart (DB_URL).
Para poder cargar datos de las APIs, en primer lugar habrá que activar el broker (ActiveMQ) que actua como puerto externo, se encarga de 
enviar los datos de los publishers (las APIs) a un Topic. Luego habrá que ejecutar el main del módulo event-store-builder,
el cual hace la función de subscribers tanto al topic de playlist de spotify-feeder como al topic events de ticketmaster-feeder del broker.
Luego se podrá ejecutar el main de ticketmaster-feeder proporcionando como variable de entorno la API_KEY de Ticketmaster, además de enviar los eventos
al broker, también creará un archivo "artists.txt" con los nombres de todos los artistas de todos los eventos obtenidos para posteriormente poder
hacer la busqueda en la API de Spotify en base a estos artistas. 
Una vez ejecutado el modulo ticketmaster-feeder, se podrá ejecutar el main del spotify-feeder, pasándole como variable de entonrno
el ID_ClIENT y el CLIENT_SECRET de la API.

### Ejemplos de uso (consultas, peticiones REST, etc.).

### Arquitectura de sistema y arquitectura de la aplicación (con diagramas).
El proyecto tiene una estructura hexagonal ya que hay separación entre la lógica de negocio y entradas y salidas y se hace uso de interfaces(ports) y adaptadores.
Los módulos como spotify-feeder y ticketmaster-feeder se encargan de extraer datos desde APIs externas y los envían al sistema (entrada),
además, event-store-builder se suscribe al broker y almacena los datos (salida), asimismo,
playlists-for-events consume los datos almacenados para generar nuevas funcionalidades (otro adaptador)

### Principios y patrones de diseño aplicados en cada módulo. 
En todos los módulos se ha tenido en cuenta los principios SOLID:
-Principio de Responsabilidad Única (SRP): Cada clase debe tener una única responsabilidad, lo que hace que el código sea más fácil de entender y mantener.

-Principio Abierto/Cerrado (OCP): El código debe estar abierto para su extensión pero cerrado para su modificación. Esto permite agregar nuevas funcionalidades sin alterar el código existente.

-Principio de Sustitución de Liskov (LSP): Las clases derivadas deben poder sustituir a sus clases base sin alterar el comportamiento esperado del programa.

-Principio de Segregación de Interfaces (ISP): Es mejor tener varias interfaces específicas en lugar de una única interfaz general. Esto evita que las clases implementen métodos que no necesitan.

-Principio de Inversión de Dependencias (DIP): Los módulos de alto nivel no deben depender de los módulos de bajo nivel. En su lugar, ambos deben depender de abstracciones.

Además se ha tenido en cuenta la abstracción, modularidad, cohesión y coupling (menos la dependencia por el archivo artist.txt)

Para las clases, ficheros, paquetes, métodos... Se ha respetado su limpieza y organización correcta, usando nombres que revelan las inteciones,
sin repeticiones (DRY), dej´´andolo simple(KISS), sin necesidad de comentarios, cumpliendo el orden de los paquetes, imports y atributos, usando camelCase o camelCase with first UpperCase cuando necesario.
Se han hecho clases y métodos cortos, que hagan una sola función.

En cuanto a la modularidad, se aplica la modularidad de intercambio ya que tanto spotify-feeder como ticketmaster-feeder usan el mismo componente destino: el event-store-builder, que se encarga de 
recibir y almacenar eventos en archivos JSON desde el mismo broker. Esto es reutilización clara de un componente común. También se hace uso de la modularidad de sustitución de componentes puesto que
ticketmaster-feeder y spotify-feeder tienen estructuras similares pero fuentes distintas. Cada uno puede sustituir su proveedor de datos (TicketMasterClient, SpotifyClient, etc.) sin alterar la 
lógica base de publicación al broker.
Asimismo se aplica la modularidad de mezcla ya que el módulo playlists-for-events puede combinar eventos almacenados por event-store-builder desde distintos feeders (Spotify + Ticketmaster), 
lo que resulta en una nueva funcionalidad emergente: generar playlists a partir de eventos.
Otra modularidad usada es la de bus, porque los módulos se comunican a través del broker ActiveMQ, que actúa como un bus de mensajes. ticketmaster-feeder y spotify-feeder publican en topics 
y event-store-builder los consume.


Por otro lado, en el proyecto se aplica Event Sourcing ya que los eventos llegan por un topic (playlist, events).
Se procesan con messageProcessor. Luego se guardan como archivos .events en el disco, sin sobrescribir.
Esto es consistente con el patrón Event Sourcing, porque cada evento representa un cambio en el sistema y se almacena de forma inmutable

También de usa collection pipeline implicitamente ya que se recolectan archivos .event, se le linea a linea el archivo, se transforma cdaa línea con parse,
se filtran duplicados y se acumulan eventos únicos
