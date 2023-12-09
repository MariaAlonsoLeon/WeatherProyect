# Cover Page:

- Subject: Data Science Application Development (DACD)
- Academic Year: 2023-2024
- Degree: Data Science and Engineering (GCID)
- School: School of Computer Engineering (EII)
- University: University of Las Palmas de Gran Canaria (ULPGC)

# Main Functionality

The Weather application provides weather forecasts by integrating with the OpenWeatherMap API. It consists of two main modules: "weather-provider" and "event-store-builder." The first module collects the weather forecast every 6 hours for the next 5 days for the 8 Canary Islands and sends it to an active messaging system or broker (ActiveMQ). The second module, "event-store-builder," subscribes to weather events from the messaging system and stores them in local files, using the following directory structure: "eventstore/prediction.Weather/{ss}/{YYYYMMDD}.events." Here, ss is the data source, "YYYYMMDD" is the date of the event, and ".event" is the file extension for storing events associated with a specific day.

# How to Run the Program

The program can be easily executed by following these steps:

1. Download the latest version from the releases of this project. These already include all the necessary dependencies.
2. Unzip the downloaded folders and place them in the desired location.
3. Start the broker. To do this, you will need to download it from this link → [ActiveMQ (apache.org)](https://activemq.apache.org/)
4. Run the 'event-store-builder' module. To do this, from the terminal, use 'cd' to navigate to the directory where the uncompressed folders are located. Use the 'java -jar' command and add the path where you want to store the directory structure as a program argument.
5. Now, do the same with the 'weather-provider' module.
6. In this case, the program argument will be the API key. Obtain your key from this link → [Members (openweathermap.org)](https://openweathermap.org/members)

# Implementation

## Module "weather-provider"

### Control:

- **ActiveMQMessageSender (implements TopicSender):** Sends weather data to the broker.
- **OpenWeatherMapSupplier (implements WeatherSupplier):** Obtains weather forecasts from OpenWeatherMap.
- **WeatherController:** Controls the periodic retrieval and sending of weather data.
- **Main:** Initiates the weather forecast application.

### Model:

- **Location:** Represents a geographical location.
- **Weather:** Represents meteorological data events.

## Module "event-store-builder"

- **FileEventStoreBuilder (implements EventStoreBuilder):** Stores weather events in local files.
- **TopicSubscriber (implements Subscriber):** Subscribes to weather events and stores them locally.
- **Main:** Initiates the weather event subscription system.

## Design and Principles

The application adheres to SOLID principles to ensure a robust and maintainable design. Below are specific SOLID principles applied in the implementation:

### Single Responsibility Principle (SRP):

Ensures that each class has a single responsibility. For example, the FileEventStoreBuilder class has the exclusive responsibility of saving weather events to local files. This way, if the storage process needs modification, only this class will be affected.

### Open/Closed Principle (OCP):

Indicates that existing code should only be modified to fix errors and not to add new functionalities. For instance, the weather provider interface allows introducing new information providers without modifying the WeatherController logic.

### Liskov Substitution Principle (LSP):

Subclasses should be substitutable for their base classes without affecting functionality. For example, the OpenWeatherMapSupplier class is entirely substitutable for its base class WeatherSupplier, ensuring that any part of the system using WeatherSupplier works correctly with OpenWeatherMapSupplier.

### Interface Segregation Principle (ISP):

Interfaces should be designed without unnecessary methods, keeping them clean. Large interfaces should be divided into smaller ones. For example, the WeatherSupplier interface offers only the necessary functionality to obtain weather forecasts.

### Dependency Inversion Principle (DIP):

Dependencies are separated to avoid depending on low-level modules. Instead, both should depend on abstractions. For example, the WeatherController class depends on the WeatherSupplier and WeatherStore interfaces rather than their implementations.

### Observer Pattern:

The Observer pattern is used to notify subscribers, in this case, the "event-store-builder" module, about new weather events. This decouples event generation and storage.

### Exception Handling:

A consistent exception-handling system is implemented. In the "event-store-builder" module, custom exceptions like WeatherReceiverException are used to encapsulate specific errors for proper handling. No interface throws exceptions that are not custom, especially Runtime Exceptions.

### Logger Usage:

The use of a logger throughout the code improves visibility and exception handling. Exceptions are appropriately logged, aiding debugging and system monitoring in production.

### Data Lake:

The "event-store-builder" module stores weather events in a local file format organized as a "Data Lake." This approach allows for easy expansion and long-term data analysis.
