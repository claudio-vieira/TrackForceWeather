# Weather App

A clean, maintainable Android Weather Application using the OpenWeatherMap API, with offline support, MVVM (Clean Architecture), and modern Android development best practices.

Features

- Fetch and display weather information using REST API (OpenWeatherMap)
- Offline capabilities: latest fetched data displayed even after app restarts
- Uses MVVM + Use Case architecture for scalability and maintainability
- Dagger Hilt for dependency injection
- Retrofit for networking
- Room for local caching
- Jetpack Compose for UI
- MockK for testing
- Image loading with Coil
- Unit and UI tests included


It demonstrates Creational pattern by using Singleton on database access. 
An adapter implementation was used to map data. It's on mapper folder ensuring the structural requeried pattern.
For behavioral design the Observer flow was used.

# Getting Started

- Clone the Repository
- Get your API key from OpenWeatherMap
- Add your key on local.properties *WEATHER_API_KEY=your_api_key_here*
- Run the Application

# Offline Capabilities

- On launch, the app fetches weather data and saves it locally in Room.
- If the app is killed, the last saved weather data is displayed on the next launch.
- This ensures the user always has access to the last known weather data even without internet.
