# WeatherApp
- **Asignatura:** Desarrollo de Aplicaciones para Ciencia de Datos (DACD)
- **Curso:** 2023-2024
- **Titulación:** Ciencia e Ingeniería de Datos (GCID)
- **Escuela:** Escuela de Ingeniería Informática (EII)
- **Universidad:** Universidad de las Palmas de Gran Canaria (ULPGC)

## Resumen de la Funcionalidad
La aplicación WeatherApp proporciona información meteorológica para una ubicación en cada una de las Islas Canarias. Obtiene los datos de la API de OpenWeatherMap (servicio en línea que proporciona datos meteorologicos) y almacena la información localmente en una base de datos SQLite.

La aplicación en ejecución cada 6 horas actualiza e inserta los datos de la última llamada de la API. Una vez hecha esta actualización ofrece la posibilidad de consultar el pronóstico del tiempo para varios días, concretamente 5 días posteriores a la fecha actual y en caso de que la aplicación ya tenga historial se podrán consultar los datos ya registrados de días pasados.

## Recursos Utilizados
- **Entorno de Desarrollo:** IntelliJ IDEA.
- **Herramientas de Control de Versiones:** Git, GitHub

## Estructura del Proyecto y Dependencias

El proyecto sigue una estructura organizada con varios paquetes que separan las responsabilidades y facilitan la mantenibilidad del código. A continuación, se describe la estructura de paquetes del proyecto:

- `org.ulpgc.dacd.control`: Contiene las clases e interfaces relacionadas con el control de la aplicación:
    - **Clases:**
        - Main: La clase principal que inicia la aplicación. Configura y conecta los componentes principales.
        - WeatherController: Encargada de coordinar la lógica principal de la aplicación, como la actualización de datos meteorológicos y la interacción con el usuario.
        - OpenWeatherMapSupplier: Obtiene datos meteorológicos en tiempo real de OpenWeatherMap.
        - SQLiteWeatherStore: Gestiona el almacenamiento local de datos meteorológicos en una base de datos SQLite.
        - WeatherTask: Es una tarea programada que ejecuta la lógica de obtención de datos meteorológicos según un horario establecido.
    - **Interfaces:**
        - WeatherStore: Define métodos para almacenar y recuperar datos meteorológicos.
        - WeatherSupplier: Define métodos para obtener datos meteorológicos de una fuente externa.

- `org.ulpgc.dacd.model`: Define las clases de modelo utilizadas para representar la información meteorológica:
    - **Clases:**
        - Location: Representa la información de la ubicación, incluyendo el nombre de la isla y sus coordenadas geográficas.
        - Weather: Contiene los datos meteorológicos para una ubicación específica, como temperatura, humedad y descripción del tiempo.

- `jdbc`: Directorio que almacena la base de datos SQLite y su conexión.

## Diseño

### Patrones y Principios de Diseño


La aplicación utiliza el patrón de diseño MVC (Modelo-Vista-Controlador) para lograr una estructura modular y mantenible, teniendo en cuenta principios como la modularidad, cohesión y bajo acoplamiento. La separación de responsabilidades facilita la extensibilidad y promueve un diseño más sólido y mantenible a lo largo del tiempo.

## Decisiones de Implementación

### Manejo de Excepciones
Se implementa un manejo adecuado de excepciones en varias partes del código, asegurando que los errores se registren y gestionen correctamente. Esto contribuye a la robustez del sistema y facilita la identificación de posibles problemas.

### Extracción de Datos Segura
El uso de la biblioteca Jsoup para extraer datos HTML de OpenWeatherMap se realiza de manera segura y controlada, minimizando posibles amenazas de seguridad.

### Utilización de Try-with-Resources
En el manejo de recursos, como conexiones JDBC, se emplea la declaración try-with-resources, garantizando la liberación adecuada de recursos y aumentando la fiabilidad del código.

### Uso de float vs double

Debido a que double presenta una mayor presición pero ocupa más espacio unicamente se utilizó para la latitud y la longitud, ya que el resto de datos proporcionados por la API no presentan muchos decimales.

### Uso de Variables Finales

La mayoría de variables se han marcado como final para garantizar inmutabilidad y prevenir cambios inesperados en el estado de los objetos. Esta elección contribuye a la consistencia y predictibilidad del código, promoviendo la seguridad y mantenimiento a lo largo del ciclo de vida de la aplicación.

### Gestión de API Key como Argumento

La API Key se pasa como argumento al ejecutar la aplicación en IntelliJ IDEA para mejorar la seguridad y flexibilidad. Evita la exposición directa de claves en el código fuente y facilita la configuración en diferentes entornos sin modificar el código.

### Uso de Logger

Se ha optado por utilizar el logger para registrar información, advertencias y errores, esto da una trazabilidad eficiente de la ejecución de la aplicación y facilitando la identificación y resolución de posibles problemas.
### Diagrama de Clases

Para un mejor entendimiento del proyecto y su estructura, se ha decidido añadir un diagrama de clases. Es importante destacar que solo se han incluido los atributos y métodos principales de cada clase con el fin de que sea más legible.

![Diagrama de Clases](..%2F..%2FPruebaUML1%20%283%29.png)

## Relaciones de Dependencia

- La clase `WeatherController` depende de las clases `WeatherSupplier` y `WeatherStore` para obtener y almacenar datos meteorológicos.
- La clase `OpenWeatherMapSupplier` implementa la interfaz `WeatherSupplier` y utiliza la biblioteca Jsoup para obtener datos de OpenWeatherMap.
- La clase `SQLiteWeatherStore` implementa la interfaz `WeatherStore`.
- La clase WeatherTask depende de WeatherController para ejecutar la lógica de obtención de datos meteorológicos según un horario establecido, coordinando así la tarea programada con la lógica principal de la aplicación.
- La clase `Main` depende de `WeatherController` para iniciar y coordinar el flujo de la aplicación, y de `WeatherTask` para realizar la tarea periódica.
- La clase `Weather` tiene un atributo de la clase `Location` para indicar la ubicación.