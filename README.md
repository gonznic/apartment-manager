<h1 align="center">
 Apartment Manager
</h1>

<h4 align="center">
A scalable apartment management system.
</h4>
  
<p align="center">
  <a href="#about">About</a> •
  <a href="#overview">Overview</a> •
  <a href="#installation">Installation</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#updates">Updates</a> •
  <a href="#license">License</a>
</p>  
  

## About

<table>
<tr>
<td>

**Apartment Manager** is a scalable **apartment management system** with a focus on accessibility and transparency. It is responsible for leasing, rent collection, maintenance, and performance analytics. It is also designed in a minimalist and modern style, has **dark/light mode**, and can be rezised to a variety of sizes for compatibility.
  
Understanding a property’s performance should be simple and straight forward. Apartment Manager makes it easy for property managers to view and understand **key performance indicators (KPIs)** and periodic changes in operations. With an administrator account, a manager has full control over unit assignments, resident accounts, leases, and profitability.
  
This application also enhances **customer experience**. Residents can view their payment history, upcoming charges, lease information, and account status all in one place. It is also easy to make payments and understand a breakdown of an account’s charges.

<p align="center">
<img src="https://user-images.githubusercontent.com/103907036/179035685-4fbdbc81-c1a9-47d2-8e3a-caaabd9003f3.png" width="800">  
</p>
  
</td>
</tr>
</table>


## Overview

### Administrator Account

&nbsp;&nbsp;&nbsp;Pages: Dashboard, Unit Manager, Resident Manager, Complaints, and Settings
<details>
  <summary>Screenshots</summary>

  <img src="https://user-images.githubusercontent.com/103907036/179044162-69c381b2-d56a-4b24-ad17-40658fe587e0.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179044312-b68604f2-c6f0-46a8-8a9f-5fc24f3ea1c4.png" width="110">

  <img src="https://user-images.githubusercontent.com/103907036/179044369-4a84d184-65f5-41a9-9012-4590b745cae7.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179044482-f17c53ab-500f-495d-a5be-c6ec7c086ffa.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179044552-d3c63b56-5b4c-438a-b392-1b0796a76830.png" width="200">
  
  &nbsp;
  
  <img src="https://user-images.githubusercontent.com/103907036/179045737-7d852891-83d1-4cf7-82a4-9c9bd7bc2da5.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179045749-5962406f-d883-4063-a223-f4ab7e01555b.png" width="110">

  <img src="https://user-images.githubusercontent.com/103907036/179045773-487722fd-b106-4f0a-ae13-6e8011d674ef.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179045794-33859e9f-53a6-4928-a82f-da7f7e6119e3.png" width="200">

  <img src="https://user-images.githubusercontent.com/103907036/179045804-5c1d7421-0eb7-4c9a-bd16-9ed6cd92bdd5.png" width="200">

</details>



### Resident Account

&nbsp;&nbsp;&nbsp;Pages: Dashboard, Complaints, and Settings
<details>
  <summary>Screenshots</summary>

  <img src="https://user-images.githubusercontent.com/103907036/179047077-a3e26d2d-690e-4000-9172-b68d863e1f31.png" width="160">

  <img src="https://user-images.githubusercontent.com/103907036/179047320-5a1083c8-3b1a-4e1c-aa35-4b6b86377d7d.png" width="160">

  <img src="https://user-images.githubusercontent.com/103907036/179047090-bbacc3d1-3275-491d-8b7c-d5336830b8d0.png" width="160">

  <img src="https://user-images.githubusercontent.com/103907036/179047324-6f5bb9a8-4a10-4de8-80dc-197679382f21.png" width="160">

</details>


## Installation

##### Requirements
* Java SE
* A SQL database and tool (Made using MariaDB and phpMyAdmin)
* JDBC Connector for your type of SQL database (Made using MySQL Connector/J)
* An IDE **(Recommended)** (Made using Eclipse)

##### Setup

* **Download** and unzip the latest version of Apartment Manager.
* **Create** a new SQL database (example query: `CREATE DATABASE apartment_manager`).
* **Import** `src/db/init.sql` into the new databse to create the necessary tables.
* **Add the JDBC Connector** to your project and set the classpath (In Eclipse: right click > “Build Path” > “Add to Build Path”). The JDBC Connector used during development can be found in `lib/`.

##### Database Access

* Set the `dbUrl`, `dbUsername`, and `dbPassword` variables in `src/main/java/Page.java` to your SQL server url, and the log-in credentials to a database access account. This accout will be used by non-admin users to access the database: they should have privileges to SELECT from all tables and INSERT and UPDATE in payments and complaints.
* Create a database account with the username and password: "admin", "password" (This is the default admin account in the users table). Administrator accounts must be created in the users table and have a database access account with the same username and password (Admin accounts use the same username and password used to log in to access the database). These accounts can hava all database privileges.

##### Run the Application

* Compile and **run** `src/main/java/Launch.java`.
* **Log in** using username and password: "admin", "password".
* To access resident accounts, find a username in the admin's Resident Manager and log in with the password: "password".


## Configuration

To **generate demo data** and fill the database:
* Set the `dbUrl`, `dbUsername`, and `dbPassword` variables in `src/db/PseudoDataGenerator.java` to your SQL server url, and the log-in credentials to a database access account with **all priviledges**. 
* Compile and **run** `PseudoDataGenerator.java`. This will clear all data previously in the database except rows in the users table where type = 'admin' before inserting new data.


## Updates
Apartment Manager has all the basic features required to manage residential multi-family properties. However, there are still features left to implement. Some include:
* Inspections
* Utility Payments
* Security Deposits
* Screening Tenants

Apartment Manager can also be expanded into a broader Property Management System (PMS), allowing an operator to manage different types of properties (commercial, hotel, industrial, etc.).


## License
Apache License 2.0
