# Databases

[← Back to Home](index.md)

## Trakker Event Management Application

**Original Course:** CS 360: Mobile Architecture and Programming
**Enhancement Category:** Databases

## Artifact Overview

Trakker is an Android event management application that uses SQLite to store user and event information. I originally created the application in CS 360 using Java and Android Studio.

The original database supported basic CRUD operations for users and events, including creating accounts, saving events, editing events, deleting events, and retrieving event information.

## Original Artifact

The original Trakker database was designed mainly for simple, one-time events. Event records stored information such as the title, date, description, location, and associated username.

While this structure worked for the original application, it did not support more advanced features such as event categories or recurring events.

The original database code also used a mix of parameterized queries and SQL statements that directly inserted values into query strings, creating an opportunity to improve consistency and security.

**<a href="https://github.com/bdes021/bdes021.github.io/tree/main/databases/original" target="_blank" rel="noopener noreferrer"><strong>View Original Trakker Source Code</strong></a>
**

## Enhancement

For this enhancement, I focused on improving the structure, functionality, performance, and security of the SQLite database.

The database was expanded to support:

* Event categories
* Daily recurring events
* Weekly recurring events
* Monthly recurring events
* Yearly recurring events
* Improved foreign key relationships
* Database indexes
* Stronger input validation
* Parameterized SQL queries

The enhanced database allows Trakker to support more advanced scheduling without creating unnecessary duplicate event records.

Indexes were added to commonly searched fields to improve query performance as the amount of stored data increases. Parameterized queries and stronger input validation were also used to reduce the risk of SQL injection and prevent invalid data from being stored.

**<a href="https://github.com/bdes021/bdes021.github.io/tree/main/databases/enhanced" target="_blank" rel="noopener noreferrer"><strong>View Enhanced Trakker Source Code</strong></a>**

## Skills Demonstrated

This enhancement demonstrates skills in:

* SQLite
* Relational database design
* Database schema development
* CRUD operations
* Foreign keys
* Database indexing
* Parameterized SQL queries
* Input validation
* SQL injection prevention
* Database security
* Schema migration and version management

## Enhancement Narrative

The artifact I selected for this enhancement is an Android application I created called Trakker. Trakker is an event management application that allows users to create an account, log in, and manage personal events stored in an SQLite database. I originally created this application in my CS 360: Mobile Architecture and Programming course as part of a project to develop a functional Android application using Java and SQLite. While my first enhancement focused on improving the application's architecture, this enhancement focuses on improving the database that supports the application.

I selected Trakker for my ePortfolio because it demonstrates several important database concepts within a single application. While the previous enhancement focused on software architecture, this enhancement demonstrates my ability to design, organize, and secure a relational database. I redesigned the database to support event categories and recurring events, including daily, weekly, monthly, and yearly schedules. I also added foreign key constraints to maintain relationships between the user and event tables and created indexes on commonly searched columns to improve query performance. To strengthen the application's security, I expanded input validation and used parameterized SQL queries to help prevent SQL injection attacks. These improvements made the database more organized, efficient, and secure while demonstrating more advanced database management techniques than the original version.

I believe this enhancement successfully meets the course outcomes I planned for. This enhancement meets course outcome 5 by strengthening the application's security through input validation, parameterized SQL queries, and improved database design. These changes help protect the application from invalid input and common database vulnerabilities while improving the overall reliability of the system. This enhancement also meets course outcome 1 by improving the way event information is organized and managed. A well-designed database makes information easier to retrieve, maintain, and expand, allowing users and organizations to manage event data more effectively while supporting future application development. At this time, I do not have any changes to my original outcome-coverage plans. The completed enhancements provided a good opportunity to demonstrate the database management and security skills I intended to showcase.

Enhancing the database taught me that a well-designed database is just as important as the application itself. The original version worked well for storing simple events, but adding recurring events and event categories showed me how quickly a database can become more complex as new features are introduced. I also gained a better understanding of how indexes improve query performance and how foreign keys help maintain relationships between tables. One of the biggest challenges was implementing the recurring event logic. Instead of creating duplicate records for every occurrence, the application needed to determine whether an event should appear on a selected date based on its recurrence pattern. Developing this logic for daily, weekly, monthly, and yearly events required careful planning and testing to make sure each event appeared correctly. Another challenge was updating the database structure without breaking the existing application. Since the application already depended on the original database design, changes to the schema required careful testing to ensure everything continued to work as expected.

<a href="https://github.com/bdes021/bdes021.github.io/blob/main/docs/Databases%20Narrative.docx?raw=1"><strong>Download Narrative (Word)</strong></a>


---

[← Back to Home](index.md)
