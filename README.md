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
  <a href="#license">License</a>
</p>  
  

## About

<table>
<tr>
<td>

**Apartment Manager** is a scalable **apartment management system** with a focus on accessibility and transparency. It is responsible for leasing, rent collection, maintenance, and performance analytics. It is also designed in a minimalist and modern style with **dark/light mode**.
  
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
* To skip the log-in page, change `skipLogIn` to true in `Launch.java`, "Log In" section. (`false` by default).
* If `skipLogIn = true`, `isAdmin` will determine what type of account will be accessed (`true` by default).

## Overview



## License
Apache License 2.0
