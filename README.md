## Breve descripción del proyecto y su propuesta de valor.
Este proyecto recoge datos de conciertos y festivales musicales a través de la API de Ticketmaster y, utilizando la API de Spotify, genera playlists personalizadas para cada uno de estos eventos.
La motivación principal es la importancia de la música en la vida cotidiana y la relevancia cultural que tienen los conciertos y festivales en vivo. El objetivo es ofrecer una herramienta que conecte directamente los eventos musicales con su experiencia sonora previa, ayudando al usuario a descubrir artistas y prepararse para el evento de una forma más inmersiva y personalizada.

## Justificación de la elección de APIs y estructura del datamart.
Se ha elegido la API de Spotify por ser una de las plataformas de música más populares a nivel mundial, con un amplio catálogo y acceso sencillo a metadatos de artistas, álbumes y pistas.
La API de Ticketmaster permite acceder a una gran variedad de eventos musicales de todo el mundo, lo que la convierte en una fuente ideal para obtener datos en tiempo real sobre conciertos y festivales.

Ambas APIs son Web-based REST APIs, lo que significa que exponen recursos accesibles mediante URL y usan métodos HTTP estándar (GET, POST...).

La interacción con el usuario se realiza mediante una interfaz de línea de comandos (CLI).

La estructura del Datamart combina datos históricos (almacenados como archivos .event en formato JSON) y datos en tiempo real (provenientes del broker ActiveMQ), implementando una arquitectura Lambda que separa el procesamiento en:

- Capa de batch: procesa eventos históricos.

- Capa de velocidad: procesa eventos nuevos en tiempo real.

## Instrucciones claras para compilar y ejecutar cada módulo.
Para ejecutar el proyecto y generar una playlist para un concierto o festival en concreto se deberá ejecutar el Main del módulo playlists-for-events,
pasándole como variable de entorno la base de datos del datamart (DB_URL).

Para poder cargar datos de las APIs:
1. #### Activar el broker
   Se debe iniciar el broker ActiveMQ, ya que actúa como middleware para distribuir los eventos entre los distintos módulos.
2. #### Ejecutar event-store-builder
   Este módulo actúa como subscriber y se conecta a los topics playlist (Spotify) y events (Ticketmaster).
   Almacena los datos recibidos en archivos .event para su posterior uso.
3. #### Ejecutar ticketmaster-feeder
   Este módulo se conecta a la API de Ticketmaster y publica los eventos en el topic correspondiente del broker.
   También genera un archivo artists.txt con los artistas de los eventos recogidos.
   Se deberá proporcionar como variable de entorno la API_KEY de Ticketmaster.
4. #### Ejecutar spotify-feeder
   Este módulo lee los artistas desde artists.txt, consulta la API de Spotify y publica los resultados en el topic playlist.
   Habrá que especificar como variables de entorno el ID_ClIENT y el CLIENT_SECRET de la API.

## Ejemplos de uso (consultas, peticiones REST, etc.).



## Arquitectura de sistema y arquitectura de la aplicación (con diagramas).
#### Arquitectura de Sistema
Cliente → Broker (ActiveMQ) → Subscriptores.

Estilo event-driven con integración vía Service Bus (ESB).

#### Arquitectura de Aplicación
Se ha seguido una arquitectura hexagonal, donde:
- Los módulos spotify-feeder y ticketmaster-feeder actúan como adaptadores de entrada, obteniendo datos de APIs externas.
- event-store-builder es el adaptador de salida, que persiste los datos de forma estructurada.
- playlists-for-events es un adaptador de transformación, que genera una nueva funcionalidad a partir de datos históricos y en tiempo real.

#### Diagramas


## Principios y patrones de diseño aplicados en cada módulo. 

#### SOLID
En todos los módulos se ha tenido en cuenta los principios SOLID:

-Principio de Responsabilidad Única (SRP): cada clase tiene una función clara y única, por ejemplo, SpotifyClient solo interactúa con la API de Spotify.

-Principio Abierto/Cerrado (OCP): el código está abierto para su extensión, pero cerrado para su modificación. Esto permite agregar nuevas funcionalidades sin alterar el código existente. Por ejemplo, se puede cambiar la fuente de datos (Spotify o Ticketmaster) sin modificar la lógica principal.

-Principio de Sustitución de Liskov (LSP): las clases que implementan una interfaz deben poder sustituir a la interfaz sin que el comportamiento del programa cambie o falle.
Por ejemplo, EventStore es una interfaz implementada por clases concretas como ActiveMQEventStore (almacena eventos).
Esto permite que, si quisiéramos cambiar la implementación a una Database como SqliteEventStore (que guarde los eventos en una base de datos), podríamos hacerlo sin modificar el código que la utiliza.
El módulo playlists-for-events o event-store-builder seguirían funcionando de la misma manera mientras la nueva implementación respete el contrato definido por la interfaz EventStore.

-Principio de Segregación de Interfaces (ISP): hay varias interfaces específicas (Eventstore, MusicalEventProvider...) en lugar de una única interfaz general. Esto evita que las clases implementen métodos que no necesitan.

-Principio de Inversión de Dependencias (DIP): los módulos no dependen del resto, con la excepción de ticketmaster-feeder y spotify-feeder que comparten el archivo "artists.txt", lo cual podría desacoplarse fácilmente.

#### Otros principios aplicados
- DRY (Don't Repeat Yourself): código reutilizable y sin repeticiones innecesarias.
- KISS (Keep It Simple, Stupid): métodos simples y bien definidos.
- Clean Code: nombres claros, código autoexplicativo, sin necesidad de comentarios excesivos, correcto orden de imports, paquetes y atributos y clases y métodos cortos con una sola función.

#### Modularidad aplicada
- Modularidad de intercambio: spotify-feeder y ticketmaster-feeder publican en el mismo broker y son independientes entre sí.
- Modularidad de sustitución: cambiar SpotifyClient o TicketMasterClient no afecta la lógica de publicación.
- Modularidad de mezcla: playlists-for-events mezcla datos de distintos feeders para ofrecer una nueva funcionalidad.
- Modularidad de bus: ActiveMQ actúa como el bus de servicios que conecta todos los módulos.

#### Patrones de arquitectura
- Event Sourcing: todos los eventos se almacenan como archivos .event inmutables. Cada archivo contiene un evento serializado, y nunca se sobrescriben ni se pierden, asegurando una trazabilidad completa del historial del sistema.
- Collection Pipeline: se usa implícitamente para el procesamiento de archivos .event mediante streams para mapear, filtrar y acumular.
- Cliente-Servidor: ticketmaster-feeder y spotify-feeder son clientes de APIs externas, event-store-builder es el servidor de almacenamiento, mientras que playlists-for-events funciona como cliente del event-store-builder y consumidor de eventos.
- Publisher-Subscriber(Publisher->Topic->Subscribers) y Service Bus: todos los módulos se comunican a través del broker usando topics, sin conexiones directas entre ellos.

#### Pruebas y test unitarios
Se han implementado tests unitarios para todos los módulos del sistema, asegurando que cada componente funcione de forma independiente y conforme a su contrato.
Para las pruebas se han utilizado herramientas como JUnit 5 y Mockito para simular dependencias externas (como conexiones JMS, archivos o clientes HTTP) y garantizar aislamiento en los tests.

#### Otras técnicas
- Inyección de dependencias: se realiza desde los Main de cada módulo.
- Uso de hilos: event-store-builder utiliza threads para escuchar múltiples topics.

Además, durante el desarrollo del proyecto se han tenido en cuenta principios fundamentales del diseño de software como la abstracción, para ocultar los detalles internos y exponer solo lo necesario; la modularidad, para dividir el sistema en componentes independientes y reutilizables; la cohesión, asegurando que cada módulo tenga una única responsabilidad clara; y un acoplamiento bajo, evitando dependencias innecesarias entre módulos. 
La única excepción parcial se da con el archivo artists.txt, que introduce una dependencia entre ticketmaster-feeder y spotify-feeder, aunque podría resolverse fácilmente.


