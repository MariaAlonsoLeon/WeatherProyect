# WeatherApp
- **Subject:** Development of Applications for Data Science (DACD)
- **Curse:** 2023-2024
- **Degree:** Data Science and Engineering (GCID)
- **School:** School of Computer Engineering (EII)
- **University:**  University of Las Palmas de Gran Canaria (ULPGC)

## Functionality Summary

The WeatherApp provides weather information for a location on each of the Canary Islands. It fetches data from the OpenWeatherMap API (an online service providing meteorological data) and stores the information locally in an SQLite database.

The application, when running, updates and inserts data from the API every 6 hours. After this update, it offers the possibility to check the weather forecast for several days, specifically 5 days after the current date. If the application already has a history, you can also check the recorded data from past days.

## Resources Used
- **Development Environment:** IntelliJ IDEA.
- **Version Control Tools:** Git, GitHub

## Project Structure

The project follows an organized structure with various packages that separate responsibilities and facilitate code maintainability. The project's package structure is described below:

- `org.ulpgc.dacd.control`: Contains classes and interfaces related to application control:
    - **Classes:**
        - Main: The main class that starts the application. It configures and connects the main components.
        - WeatherController: Responsible for coordinating the main logic of the application, such as updating meteorological data and interacting with the user.
        - OpenWeatherMapSupplier: Retrieves real-time meteorological data from OpenWeatherMap.
        - SQLiteWeatherStore: Manages local storage of meteorological data in an SQLite database.
        - WeatherTask: A scheduled task that executes the logic of obtaining meteorological data according to a set schedule.
    - **Interfaces:**
        - WeatherStore: Defines methods for storing and retrieving meteorological data.
        - WeatherSupplier: Defines methods for obtaining meteorological data from an external source.

- `org.ulpgc.dacd.model`: Defines the model classes used to represent meteorological information:
    - **Classes:**
        - Location: Represents location information, including the name of the island and its geographical coordinates.
        - Weather:  Contains meteorological data for a specific location, such as temperature, humidity, and weather description.

- `jdbc`: Directory that stores the SQLite database and its connection.

## Design

### Design Patterns and Principles

The application uses the Model-View-Controller (MVC) design pattern to achieve a modular and maintainable structure, taking into account principles such as modularity, cohesion, and low coupling. The separation of responsibilities facilitates extensibility and promotes a more robust and maintainable design over time.

## Implementation Decisions

### Exception Handling
Proper exception handling is implemented in various parts of the code, ensuring that errors are logged and handled correctly. This contributes to the system's robustness and facilitates the identification of potential issues.

### Secure Data Extraction
The use of the Jsoup library to extract HTML data from OpenWeatherMap is done safely and controlled, minimizing potential security threats.

### Use of Try-with-Resources
In the handling of resources, such as JDBC connections, the try-with-resources statement is used, ensuring proper resource release and increasing code reliability.

### Use of float vs double

Due to the higher precision of double but larger space occupation, it is only used for latitude and longitude, as the rest of the data provided by the API does not have many decimal places.

### Use of Final Variables

Most variables are marked as final to ensure immutability and prevent unexpected changes in the state of objects. This choice contributes to the consistency and predictability of the code, promoting security and maintenance throughout the application's lifecycle.

### API Key Management as an Argument

The API Key is passed as an argument when running the application in IntelliJ IDEA to improve security and flexibility. It avoids the direct exposure of keys in the source code and facilitates configuration in different environments without modifying the code.

### Use of Logger

The decision to use a logger to record information, warnings, and errors provides efficient traceability of the application's execution, facilitating the identification and resolution of potential issues.

## Class Diagram

For a better understanding of the project and its structure, it has been decided to add a class diagram. It is important to note that only the main attributes and methods of each class have been included to make it more readable.

![PruebaUML1 (5)](https://github.com/MariaAlonsoLeon/Practice1/assets/145381435/8eda6e19-1fe7-47ba-bca0-0826f2d05d28)

### Dependency Relationships

- The `WeatherController` class depends on the `WeatherSupplier` and `WeatherStore` classes to obtain and store meteorological data.
- The `OpenWeatherMapSupplier` class implements the `WeatherSupplier` interface and uses the Jsoup library to obtain data from OpenWeatherMap.
- The `SQLiteWeatherStore` class implements the `WeatherStore` interface.
- The `WeatherTask` class depends on `WeatherController` to execute the logic of obtaining meteorological data according to a set schedule, thus coordinating the scheduled task with the main logic of the application.
- The `Main` class depends on `WeatherController` to start and coordinate the application flow, and on `WeatherTask` to perform the periodic task.
- The `Weather` class has an attribute of the `Location` class to indicate the location.

