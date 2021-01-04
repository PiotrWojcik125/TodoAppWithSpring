This project was made (its not final version yet) during course "Java - Spring Framework" for full information go to:
https://full-stack.engineering/
It also contains my additional features.

Main goal of this project was to learn Spring framework.

This application is advance "to do list".

"Project" is a template for creating group of tasks. Each task have description and deadline.
When creating project, task's deadline value is passed as amount of days before project's deadline(negative integer).
You can also create group of tasks without creating project. Group's deadline will be set as the latest deadline from tasks.
"Tasks for today" shows undone tasks with deadline expiring today, deadline already expired and deadline is null.
Deleting group of tasks will delete all tasks assigned to given group. Deleting project will not delete assigned groups of tasks.
This application is Keycloak secured.There are 2 roles available: "admin" and "user".
Remember to change database location in application properties before running application or change profile to "prod" so database will be kept in memory.


