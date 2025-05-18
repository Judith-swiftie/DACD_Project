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
El sistema realiza múltiples peticiones REST externas para recolectar información de fuentes externas (Ticketmaster y Spotify), haciendo uso de clientes HTTP diseñados específicamente para cada API.

Por un lado, desde el módulo ticketmaster-feeder, el TicketMasterEventProvider es responsable de consultar la API de Ticketmaster a través del cliente TicketMasterClient. Este cliente realiza llamadas GET a https://app.ticketmaster.com/discovery/v2/events.json incluyendo una API key y parámetros como el país o la página.
 ```
   HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
   HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```
La respuesta JSON es posteriormente parseada por TicketMasterEventParser, extrayendo eventos musicales relevantes, artistas participantes, lugar, ciudad, país, fechas y precios.

Por otro lado, desde el módulo spotify-feeder, el SpotifyArtistService interactúa con la API pública de Spotify utilizando un SpotifyClient previamente autenticado con un accessToken generado por SpotifyAuth mediante el protocolo OAuth2 (flujo client credentials). 
Este servicio permite buscar un artista por nombre mediante una petición GET a https://api.spotify.com/v1/search?q=%s&type=artist y obtener sus canciones más populares por país con otra petición GET a https://api.spotify.com/v1/artists/%s/top-tracks?market=%s.
```
   HttpRequest request = createRequest(url);
   HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
```
Ambas respuestas son procesadas y transformadas en datos relevantes para enriquecer los eventos musicales obtenidos previamente de Ticketmaster. Este flujo de peticiones REST permite un cruce de datos dinámico entre distintas fuentes, que posteriormente es almacenado como eventos para mantener una trazabilidad del sistema basada en el patrón Event Sourcing.

El sistema se divide en dos principales fuentes de datos: Ticketmaster y Spotify. Ambas fuentes alimentan sus respectivos stores mediante peticiones externas y mecanismos internos definidos por controladores (Controller) que encapsulan la lógica del ciclo completo de extracción, transformación y almacenamiento.

El Controller de ticketmaster-feeder realiza una petición a Ticketmaster mediante la clase TicketMasterEventProvider. Luego, utiliza el almacén de eventos (EventStore), que puede ser una implementación SQLite (SqliteEventStore) o una implementación basada en mensajería asíncrona con ActiveMQ (ActiveMQEventStore), para persistir y propagar los eventos.
El Controller generará mensajes JMS con formato JSON que incluyen metadatos como el timestamp, el source, y detalles del evento. Ejemplo:

```
   {
   "ts": "2025-05-05T18:00:39.674311500Z",
   "ss": "TicketmasterFeeder",
   "name": "Tate McRae: Miss Possessive Tour",
   "date": "2025-05-09",
   "venue": "Palacio Vistalegre",
   "artists": [{"name": "Tate McRae"}],
   "country":"Spain",
   "city":"Madrid",
   ...
   }
   ```

Una vez recolectados los eventos, la clase ArtistFileExporter extrae todos los nombres de artistas únicos y los exporta a un archivo artists.txt.

El Controller de spotify-feeder realiza búsquedas de artistas en Spotify (ArtistFinder), obtiene sus canciones más populares por país (TrackProvider) y guarda estos datos si han cambiado, usando MusicStore (que puede ser persistente como SqliteMusicStore o enviar eventos vía ActiveMQMusicStore).
En el caso de ActiveMQMusicStore, el mensaje enviado tendría un formato como:

   ```
   {
   "ts": "2025-05-08T17:32:02.973636600Z",
   "ss": "SpotifyFeeder",
   "artistId": "45dkTj5sMRSjrmBSBeiHym",
   "artistName": "Tate McRae",
   "tracks": ["Sports car","Revolving door","greedy","you broke me first","I know love (feat. The Kid LAROI)","exes","Dear god","Miss possessive","Purple lace bra"...]
   }
   ```

A partir de aquí, el event-store-builder entra en acción suscribiéndose a cada uno de esos topics mediante un Controller que, usando un JmsConnectionManager, crea una conexión duradera a ActiveMQ.
Cada mensaje es entregado a un JsonMessageProcessor, que extrae el texto JSON y lo entrega a una implementación de JsonEventStore (por ejemplo FileJsonEventStore). 
Esta clase utiliza un JsonEventPathBuilder para construir rutas de archivo basadas en la fecha y la fuente del evento, y un FileWriter para appender cada JSON a un fichero de eventos (.events) bajo un directorio organizado por topic y fecha, asegurando que ningún evento se sobrescribe y así se preserva un historial completo inmutable.
   `eventstore/<topic>/<source>/<YYYYMMDD>.events`

Una vez que los eventos han sido recogidos y almacenados, se interpretan y convierten en información estructurada. La clase DatamartEventProcessor, por ejemplo, implementa la interfaz EventProcessor y se encarga de transformar cada mensaje JSON recibido en una instancia de Event, delegando su almacenamiento en una instancia de Datamart, una base de datos en memoria que centraliza todos los eventos y datos musicales.

Por su parte, el componente HistoricalEventLoader está diseñado para leer todos los eventos previamente almacenados en el sistema de archivos. Explora recursivamente los directorios del eventstore/, recuperando archivos .events, y los interpreta mediante una implementación de EventParser (JsonEventParser) que convierte líneas JSON en objetos Event. 
Este proceso filtra eventos duplicados en función del nombre del evento, garantizando una colección histórica única y limpia.

Complementariamente, SpotifyFeederLoader se encarga de cargar datos auxiliares desde archivos .events de Spotify. Utiliza un SpotifyJsonParser para extraer el nombre del artista y su lista de canciones desde cada línea JSON.

La clase RealTimeEventConsumer introduce la capacidad de consumir eventos en tiempo real desde un broker JMS. Se suscribe a un topic específico y, por medio de un MessageListener, intercepta mensajes de texto, que luego son procesados usando una instancia de EventProcessor.

Por último, el sistema permite al usuario generar una playlist según un evento musical que ya está guardado en la base de datos mediante la línea de comandos (CLI)
El usuario puede elegir un evento de tres formas:

- Buscando por nombre del evento (1).
```
Introduce el nombre del evento: Mad Cool
Eventos encontrados:
   1. Mad Cool 2025 | Thursday Ticket
   2. Mad Cool 2025 | Friday Ticket
   3. Mad Cool 2025 | Saturday Ticket
   4. Brunch Electronik x Mad Cool
Selecciona el número del evento para generar la playlist (o 0 para cancelar): 1
Generando playlist para el evento: Mad Cool 2025 | Thursday Ticket

Playlist Generada:
Nombre de la playlist: Mi Playlist para el Evento: Mad Cool 2025 | Thursday Ticket
   - Back Down South
   - Closer
   - Manhattan
   - Pyro
   - Revelry
   - Sex on Fire
   - Use Somebody
   - WALLS
   - Wait for Me
   - Waste A Moment
   - Blowing Smoke
   - Call Me When You Break Up (with Gracie Abrams)
   - Close To You
   - I Love You, I'm Sorry
   - I know it won't work
   - I miss you, I’m sorry
   - Mess It Up
   - Risk
   - That’s So True
   - us. (feat. Taylor Swift)
   - Candy
   - I WANNA BE YOUR SLAVE (with Iggy Pop)
   ...
```

- Buscando eventos que tengan un artista específico (2).
```
   Introduce el nombre del artista: Dua Lipa
   Eventos encontrados:
   1. Dua Lipa - Radical Optimism Tour
   Selecciona el número del evento para generar la playlist (o 0 para cancelar): 1
   Generando playlist para el evento: Dua Lipa - Radical Optimism Tour
   
   Playlist Generada:
   Nombre de la playlist: Mi Playlist para el Evento: Dua Lipa - Radical Optimism Tour
     - Cold Heart - PNAU Remix
     - Dance The Night - From Barbie The Album
     - Don't Start Now
     - Handlebars (feat. Dua Lipa)
     - Houdini
     - IDGAF
     - Levitating (feat. DaBaby)
     - New Rules
     - One Kiss (with Dua Lipa)
     - Training Season
     ...

```
- Mostrando todos los eventos disponibles (3).
```
   Eventos disponibles:
   1. Imagine Dragons: LOOM World Tour | VIP Packages
   2. Imagine Dragons: LOOM World Tour
   3. Only The Poets
   4. wave to earth - 0.03 tour
   5. AURORA - What Happened To The Earth? Part 5
   6. Tate McRae: Miss Possessive Tour | VIP Packages
   7. Gloosito - EUROSLiME Tour 2025
   8. MËSTIZA
   9. Tate McRae: Miss Possessive Tour
   10. Morad
   ....
   118. Eladio Carrión
   119. Lauren Spencer Smith - THE ART OF BEING A MESS TOUR | VIP Packages
   Selecciona el número del evento deseado: 111
   Generando playlist para el evento: Amaral
   
   Playlist Generada:
   Nombre de la playlist: Mi Playlist para el Evento: Amaral
     - Ahí Estás
     - Cuando Suba la Marea
     - Cómo hablar
     - Días de verano
     - El universo sobre mí
     - Marta, Sebas, Guille y los demás
     - Moriría por vos
     - Si tú no lo vuelves
     - Sin ti no soy nada
     - Te necesito - Acústico

```


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


