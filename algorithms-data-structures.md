# Algorithms & Data Structures

[← Back to Home](index.md)

## Grazioso Salvare Rescue Dashboard

**Original Course:** CS 340: Client/Server Development
**Enhancement Category:** Algorithms and Data Structures

## Artifact Overview

The Grazioso Salvare Rescue Dashboard is a Python application that uses Dash and MongoDB to display and manage animal shelter data. I originally created the project in CS 340 as a client/server application that connects to a MongoDB database through a separate CRUD module.

The dashboard allows users to filter animal records for different rescue categories and displays the results through a data table, bar chart, and interactive map.

## Original Artifact

The original dashboard successfully retrieved and displayed animal records, but there were opportunities to improve how the data was searched, sorted, and retrieved.

The original CRUD `read()` method accepted a MongoDB query and returned all matching documents. The dashboard also retrieved complete MongoDB documents before removing fields that were not needed. While the application supported filtering and table sorting, it did not contain a specialized data structure for quickly locating an animal by its ID.

**<a href="https://github.com/bdes021/bdes021.github.io/tree/main/algorithms-data-structures/original" target="_blank" rel="noopener noreferrer"><strong>View Original Grazioso Salvare Source Code</strong></a>**

## Enhancement

For this enhancement, I focused on improving the efficiency of searching, sorting, and retrieving animal records.

One of the main additions was a **HashMap** that uses the animal ID as the key. Building the HashMap requires O(n) time because each animal record must be processed once. After the HashMap has been created, individual animal ID searches can be completed in average O(1) time.

I also expanded the MongoDB `read()` method to support:

* Projections
* Sorting
* Result limits

Using projections allows MongoDB to return only the fields required by the dashboard instead of retrieving complete documents and removing unnecessary fields afterward.

Python's built-in sorting functionality was also used to organize search results. Python uses **Timsort**, which provides O(n log n) worst-case performance.

MongoDB indexes were added to commonly searched fields such as animal ID, animal type, breed, outcome type, and name. These indexes improve repeated search operations by allowing MongoDB to locate records more efficiently.

The enhanced dashboard also includes an Animal ID search feature that uses the HashMap for fast lookups.

**<a href="https://github.com/bdes021/bdes021.github.io/tree/main/algorithms-data-structures/enhanced" target="_blank" rel="noopener noreferrer"><strong>View Enhanced Grazioso Salvare Source Code</strong></a>**

## Skills Demonstrated

This enhancement demonstrates skills in:

* Python
* MongoDB
* HashMaps
* Searching and sorting algorithms
* Algorithmic complexity
* Timsort
* MongoDB indexing
* Database projections
* Query optimization
* Performance analysis
* Evaluating design tradeoffs

## Enhancement Narrative

The artifact I selected to enhance for Category Two: Algorithms and Data Structures is the Grazioso Salvare Rescue Dashboard that I originally created in CS 340. The project is a web-based dashboard built with Python, Dash, and MongoDB that allows users to search, filter, and visualize animal rescue data. The original project demonstrated the use of CRUD operations, MongoDB queries, and interactive data visualization. For this enhancement, I improved the application's efficiency by implementing additional algorithms and data structures. I expanded the CRUD module to support projections, sorting, and result limits, added MongoDB indexes to improve query performance, created a HashMap to provide average O(1) lookups by animal ID, and added a search feature that uses the HashMap. I also used Python's built-in Timsort algorithm to organize search results and reduced unnecessary data retrieval by using MongoDB projections. These improvements made the dashboard more efficient while maintaining the original functionality.

I selected this artifact for my ePortfolio because it demonstrates my understanding of how algorithms and data structures can improve the performance of an existing application. Rather than simply adding new features, I focused on making the application more efficient and scalable. The HashMap implementation showcases my understanding of data structures by providing fast animal ID lookups, while the enhanced CRUD module demonstrates how sorting, projections, and indexes can improve database performance. These enhancements show my ability to evaluate an existing solution, identify opportunities for improvement, and implement more efficient algorithms without changing the overall user experience.

This enhancement successfully meets course outcomes I planned to demonstrate. It supports course outcome 3 by improving the design and evaluation of a computing solution through more efficient algorithms and database operations while considering performance trade-offs. It also supports course outcome 4 by applying appropriate tools and techniques, including MongoDB indexing, HashMaps, and Python's built-in sorting algorithms, to improve the application's overall performance. At this time, I do not have any changes to my original outcome-coverage plan because this enhancement demonstrates the outcomes I intended to showcase.

Enhancing this artifact reinforced the importance of selecting the appropriate data structures and algorithms for a particular problem instead of relying only on basic solutions. Before this project, I understood HashMaps and sorting algorithms primarily from coursework, but applying them to an existing application showed me how they can significantly improve performance and usability. One of the biggest challenges was modifying the original dashboard while keeping all of its existing functionality. Small changes to the CRUD module often affected multiple parts of the application, requiring careful testing and debugging throughout the process. This enhancement demonstrated that improving software is not always about adding new features. In many cases, optimizing existing code with better algorithms and data structures can produce a more efficient, maintainable, and scalable solution.

<a href="https://github.com/bdes021/bdes021.github.io/raw/refs/heads/main/docs/Algorithms_Data_Structures_Narrative.docx"><strong>Download Narrative (Word)</strong></a>

---

[← Back to Home](index.md)
