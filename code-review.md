# Code Review

[← Back to Home](index.md)

## CS 499 Informal Code Review

As part of my CS 499 capstone, I completed a code review of the original artifacts selected for my ePortfolio. The purpose of the review was to examine the existing functionality of each project, identify areas that could be improved, and explain the enhancements I planned to make.

The review covers two projects:

* **Trakker**, an Android event management application originally developed in CS 360: Mobile Architecture and Programming.
* **Grazioso Salvare Rescue Dashboard**, a Python and MongoDB application originally developed in CS 340: Client/Server Development.

## Areas of Enhancement

### Software Design and Engineering

For Trakker, I identified areas where the application's structure could be improved. The original `MainActivity` handled the user interface, navigation, application logic, and direct database communication. My enhancement focused on separating these responsibilities through the Model-View-ViewModel (MVVM) architecture and Repository pattern. I also added an Android home screen widget for displaying upcoming events.

### Algorithms and Data Structures

For the Grazioso Salvare Rescue Dashboard, I focused on improving the efficiency of searching, sorting, and retrieving animal records. My planned enhancements included a HashMap for animal ID lookups, improved MongoDB queries, projections, sorting, and database indexes.

### Databases

For Trakker, I reviewed the original SQLite database and identified opportunities to improve its structure, functionality, and security. My enhancements added support for event categories and recurring events, improved database indexing, expanded input validation, and increased the use of parameterized SQL queries.

---

## Code Review Video

**[Watch My CS 499 Code Review](https://youtu.be/PwC-C_M5vH8)**

The video provides a walkthrough of the original source code, discusses areas identified for improvement, and explains how the planned enhancements align with the five Computer Science program outcomes.

---

[← Back to Home](index.md)
