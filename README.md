<h1 align="center">
 Apartment Manager
</h1>

<h4 align="center">
A scalable apartment management system.
</h4>
  
<p align="center">
  <a href="#about">About</a> •
  <a href="#installation">Installation</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#overview">Overview (Screenshots)</a> •
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
<img src="https://user-images.githubusercontent.com/103907036/175788353-1cce435d-b6da-4849-b91e-259b38e7fddb.png" width="700">  
</p>
  
</td>
</tr>
</table>

## Installation

##### Requirements
* **[Java SE]([https://www.java.com/en/download/manual.jsp](https://www.oracle.com/java/technologies/downloads/))** (Made using JavaSE-16)

##### Instructions
* **Download** and unzip the latest version of Apartment Manager.
* Open a **command line** (Command Prompt, Terminal).
* In the command line, **open the directory where the apartment-manager source was extracted**. To do this, type `cd <directory>/src` in the command line. For example: `cd Downloads/apartment-manager-main/src` (If done correctly, you should not receive any additional messages. The command line should begin with the entered directory).
* **Compile** the java code: enter `javac Launch.java` into the command line (This should generate .class files for every .java file in the source directory).
* **Run** the program by entering `java Launch.java` into the command line.

## Configuration

By default, the **log-in page is disabled**. When the program is launched, the administrator account will be accessed.
* To access a resident account, change `isAdmin` to `false` in the "Log In" section of `Launch.java`.
* To log-in manually, change `skipLogIn` to `false` in the "Log In" section of `Launch.java`. (Admin credentials: Operator, password)

## Overview

Apartment Manager has two types of accounts:
* **Administrator**, with five pages (tabs): Dashboard, Unit Manager, Resident Manager, Complaints, and Settings.
* **Resident**, with: Dashboard, Complaints, and Settings

The application also has **Dark and Light themes**. By default, the application is in Dark mode, but it can be switched in the Settings page.

#### Log-In Page

<img src="https://user-images.githubusercontent.com/103907036/175792022-fcea74b9-4636-48e9-abb1-902e617390df.png" width="200">

#### Admin Account

* Dashboard
<img src="https://user-images.githubusercontent.com/103907036/175792418-9403fe3f-273b-4bde-940a-b55e799d2da1.png" width="700">

* Dashboard (small)
<img src="https://user-images.githubusercontent.com/103907036/175792467-571e12c4-28ff-4b7d-adb1-56716137363f.png" width="370">

* Unit Manager
<img src="https://user-images.githubusercontent.com/103907036/175792528-3cb589ae-2713-4a96-b2f9-eda029c22eb6.png" width="700">

* Resident Manager
<img src="https://user-images.githubusercontent.com/103907036/175792530-1486ae6a-3452-4ba4-aed5-d562c9315eaa.png" width="700">

* Complaints (Admin can see all complaints)
<img src="https://user-images.githubusercontent.com/103907036/175792542-33ff0e05-5d7a-4d66-847b-26596ba35308.png" width="700">

#### Resident Account

* Dashboard
<img src="https://user-images.githubusercontent.com/103907036/175792599-c97b01fe-8bfd-49d6-a217-8b24b927edae.png" width="700">

* Dashboard (small)
<img src="https://user-images.githubusercontent.com/103907036/175792611-b9b813f4-3827-49db-9763-a99f84875136.png" width="370">

#### Settings
<img src="https://user-images.githubusercontent.com/103907036/175792635-27d013fc-7f9d-4d97-a5ee-034f86de1421.png" width="370">

## Updates
Apartment Manager has all the basic features required to manage residential multi-family properties. However, there are still features left to implement. Some include:
* Inspections
* Utility Payments
* Security Deposits
* Screening Tenants

The program currently only works offline and does not save data. The next step is to set up an online database (MySQL) that contains account and building data (This data is currently generated by a pseudorandom generator when the program is ran).

Apartment Manager can also be expanded into a broader Property Management System (PMS), allowing an operator to manage different types of properties (commercial, hotel, industrial, etc.).

## License
Apache License 2.0
