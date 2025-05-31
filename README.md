# GoRestAPIAutomationProject 🚀

Automated REST API testing project using **Rest Assured** for the [GoRest API](https://gorest.co.in/) , focusing on the main resources: `Users`, `Posts`, `Comments`, and `Todos`.

---

## 📌 Project Description

This project automates the testing of key GoRest API endpoints. It validates CRUD operations across multiple resources to ensure API reliability, correctness, and performance. The framework is built using Rest Assured with Java and includes data-driven testing, schema validation, and custom logging.

---

## 🧰 Tech Stack

- **Java**
- **Rest Assured**
- **Maven**
- **TestNG**
- **Allure Reports**
- **Log4j**
- **JSON Schema Validator**
- **POJO Classes for Serialization/Deserialization**


### ⚙️ Prerequisites

- Java Development Kit (JDK) installed
- IDE (eg: IntelliJ IDEA, Eclipse)
- Maven installed

---

### 📊 Test Coverage

Each resource (`Users`, `Posts`, `Comments`, `Todos`) includes tests for:

- **GET** – Fetch single and multiple resources
- **POST** – Create new entries
- **PUT / PATCH** – Full or partial updates
- **DELETE** – Remove resources

Each test validates:

- ✅ Status codes  
- ✅ Response body values  
- ✅ JSON schema validation  
- ✅ Response time  
- ✅ Edge and negative test scenarios  

---

### 🧪 Features

- 🔁 **Reusable Request Specification** – Centralized configuration for requests  
- 📂 **Data-Driven Testing (DDT)** – Uses dynamic values and external JSON files  
- 🧾 **JSON Schema Validation** – Validates response structure against predefined schemas  
- 🪵 **Log4j Logging** – Detailed logs for requests, responses, and custom events  
- 🧪 **POJO Classes** – For clean request/response mapping  
- 🧪 **TestNG XML Runners** – Modular execution of test suites  
- 📈 **Allure Reporting** – Beautiful, interactive test reports  

---

## 🚀 How to Run

Before running the tests, ensure you have installed all the [⚙️ Prerequisites](#-Prerequisites).

1. **Clone the repository**
   
   ```bash
   git clone https://github.com/AbdelrahmanFahmy0/GoRestAPIAutomationProject.git
   cd GoRestAPIAutomationProject
   ```
3. **Run the tests**
   
   ```bash
   mvn clean test
   ```
5. **Generate Allure Report**
   
   ```bash
   allure serve Test-Outputs/allure-results
   ```

---

## 🤝 Contributions

Contributions are welcome! Please fork the repository and create a pull request.
