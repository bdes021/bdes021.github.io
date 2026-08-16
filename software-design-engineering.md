# Software Design & Engineering

[← Back to Home](index.md)

## Trakker Event Management Application

**Original Course:** CS 360: Mobile Architecture and Programming
**Enhancement Category:** Software Design and Engineering

## Artifact Overview

Trakker is an Android event management application that allows users to create an account, log in, and manage personal events stored in an SQLite database. I originally created the application in CS 360 using Java and Android Studio.

The original application included account management, a calendar interface, event creation and editing, event deletion, settings, and local SQLite data storage.

## Original Artifact

The original version of Trakker successfully performed its required functions, but much of the application logic was handled directly inside the activities. The user interface, navigation, application logic, and database operations were closely connected, which made the application more difficult to maintain and expand.

**[View Original Trakker Source Code](software-design-engineering/original/)**

## Enhancement

For this enhancement, I refactored Trakker using the **Model-View-ViewModel (MVVM)** architecture.

The original structure relied heavily on activities communicating directly with the database. The enhanced version separates responsibilities between:

* **View** — Displays information and handles user interaction.
* **ViewModel** — Manages application state and logic for the interface.
* **Repository** — Provides a central location for accessing application data.
* **SQLite Database** — Stores user and event information.

This structure reduces the amount of responsibility placed on individual activities and makes the application easier to maintain, organize, and expand.

I also added an **Android home screen widget** that displays upcoming events without requiring the user to open the main application. The widget retrieves event information through the Repository layer so that it follows the same architecture as the rest of the application.

**[View Enhanced Trakker Source Code](software-design-engineering/enhanced/)**

## Skills Demonstrated

This enhancement demonstrates skills in:

* Java and Android development
* MVVM architecture
* Repository design pattern
* Separation of concerns
* Object-oriented programming
* SQLite integration
* Android home screen widgets
* Maintainable and scalable application design

## Enhancement Narrative

The artifact I selected for this enhancement is an Android application I made called Trakker. Trakker is an event management application that allows users to create an account, log in, and manage personal events stored in an SQLite database. I originally created this application in my CS 360: Mobile Architecture and Programming class as part of a project to develop a functional Android application using Java and SQLite.

I selected Trakker for my ePortfolio because it demonstrates several important software engineering concepts within a single application. It combines user interface design, database integration, and Android application development into a complete project that can be expanded with more advanced features. For this enhancement, I refactored the application from its original design into a Model-View-ViewModel (MVVM) architecture. Instead of allowing the activities to communicate directly with the database, I separated the application into View, ViewModel, and Repository layers. This makes the code easier to maintain, organize, and expand as the application grows. I also added an Android home screen widget that displays upcoming events. The widget retrieves event information through the Repository layer, allowing it to stay consistent with the rest of the application's architecture. This enhancement demonstrates my ability to implement Android components outside of the main application while following good software engineering practices. These enhancements improved the application's organization, maintainability, and scalability while showcasing more advanced Android development techniques than the original version.

I believe this enhancement successfully meets a couple of the course outcomes I planned for. This enhancement meets course outcome 2 by making the application more organized and professional. Along with the architectural improvements, I continued to refine the user interface and application structure to create a better overall experience. This enhancement also meets course outcome 4 by demonstrating software engineering practices used to build maintainable and scalable applications. Refactoring the project into the MVVM architecture and implementing the Repository pattern improved the application's overall design and made future development easier. I may have to revisit this artifact at a later date, but at this time I do not have any changes to my original outcome-coverage plans. The completed enhancements provided a good opportunity to demonstrate the software engineering skills I intended to showcase.

Refactoring Trakker taught me the importance of separating responsibilities within an application. In the original version, the activities handled both the user interface and the database operations. Moving the database logic into a Repository and introducing a ViewModel made the application much easier to understand and maintain. One of the biggest challenges was restructuring the project without breaking the existing functionality. Since many parts of the application depended on the original structure, even small changes often affected multiple files and caused multiple errors. This required careful testing throughout the refactoring process. Another challenge was implementing the home screen widget. I did not have any Android widget experience prior to this, so I had to learn how widgets communicate with the application and how to update the widget whenever event data changed. This enhancement showed me that good software engineering is not only about adding new features but also about creating an application that is organized, maintainable, and easier to build on in the future.

---

[← Back to Home](index.md)
