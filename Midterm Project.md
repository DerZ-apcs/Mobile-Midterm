## Midterm Project

CS426 - Mobile Device Application Development

## Project Context

You are tasked with building a mobile application for a fictional coffee shop named "The Code Cup". This application allows users to conveniently place orders, collect reward points, and manage their accounts.

## Learning Objectives

The learning objectives of this project are to evaluate your ability to:

- Build a complete multi-screen application.

- Implement effective application state management.

- Handle user input and events.

- Implement basic business logic and manage local data.

## General Requirements

- This is an individual project.

- Students are permitted to use any framework of their choice, such as the native Android SDK, Flutter, React Native, or others.

- Regardless of the framework used, the application must target the Android platform.

- The project source code must be cleaned (i.e., remove all temporary build files) before submission.


## Submission Requirements

A .zip archive named StudentID.zip must be submitted. The archive must adhere to the following directory structure:

StudentID/

├── source/

├── StudentID-demo.mp4

containing a YouTube link is an acceptable alternative.

├── StudentID-app.apk

└── StudentID-report.pdf

Deadlines: July 29th

// Contains all application source code.

// A video demonstrating the application's functionality. An optional demo.txt file

// The compiled, installable Android Package Kit (APK) or AAB

// A comprehensive project report.

## Application Requirements

Create an app with the following features


## Home Screen

UI Implementation: Implement the fundamental UI layout for the home screen.

Header Component: Integrate the specified header element.

Bottom Navigation Bar: Implement a functional bottom navigation controller.

Loyalty Card View: Display the user's loyalty card status.

Coffee List View: Populate and render a ListView or RecyclerView of available coffee

products.

Navigation Intent :Implement an on-click listener for list items that navigates the user to 3 the corresponding product "Details" screen

1

2

3

3

3


## Rewards

Rewards Screen UI: Implement the primary layout for the Rewards screen.

1

Loyalty Stamp Logic: For each completed order, increment the loyalty card stamp count

3

by one, up to a maximum of eight.

Loyalty Card Reset: Implement an event (e.g., on-click) to reset the stamp count to zero

3

upon reaching eight stamps.

Points Calculation & Display: Implement logic to award reward points based on the total

3

monetary value of each order and display them in a list.

Total Points Aggregation: Display the sum of all accumulated reward points.

2


| Redeem Rewards |   |
| --- | --- |
| Points Redemption: On a user action, allow the exchange of accumulated points for a | 3 |
| product, and correctly decrement the total points. |   |


| Profile |   |
| --- | --- |
| UI Implementation: Implement the user profile screen layout. | 3 |
| Profile Editing Functionality: Enable an edit mode via an icon press, allowing for | 3 |
| modification of user profile data. |   |


| General application requirements |   |
| --- | --- |
| State & Lifecycle Management: Implement robust state management to handle the | 12 |
| application lifecycle (e.g., onPause, onResume, onStop) and preserve data across |   |
| configuration changes. |   |
| Data Persistence & Initialization: Implement a data persistence strategy (e.g., | 6 |
| SharedPreferences, Room, SQLite) and handle the initial seeding of required application |   |
| data. |   |
| User-Defined Features: Implement novel features or requirements beyond the scope of | 50 |
| this document. |   |
| Min(150, Total score of all requirements above) Total | 150 |
| Code Quality, Report & Demo (±10 Points): The final score may be adjusted by up to 10 Additional Criteria | [-15,+15] |
| points based on the quality, clarity, and organization of the source code, the project report, |   |
| and the video demonstration. |   |
| Your score: Min(10,(Total + Additional Criteria) / 15) |   |

## Regulations and Support

## Academic Integrity

- Plagiarism or direct copying of code from other students or online sources is strictly prohibited. All submitted code must be the student's own original work.

- The use of third-party libraries is permitted but must be properly attributed in the report.

- Any violation of these policies will result in a score of zero for the project and may be reported for further disciplinary action.

## Use of AI Tools

- You are permitted to use AI-powered tools (e.g., GitHub Copilot, ChatGPT) to assist you.

- However, you are fully responsible for the code you submit. You must ensure that you thoroughly understand everything you add to your project.


- The instructor may ask you to explain how any part of your source code works during the final review. You must be able to explain the logic and functionality of the code you have submitted.

## Support Channel

- All questions regarding the project requirements should be posted on the official class Facebook group for assistance.
