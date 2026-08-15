# Demo AQA Project: Banking API Test Framework

[![CI](https://github.com/Viktorm-tech/demo-aqa-bank/actions/workflows/ci.yml/badge.svg)](https://github.com/Viktorm-tech/demo-aqa-bank/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-blue)](https://Viktorm-tech.github.io/demo-aqa-bank/allure-report/)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

---


## 📖 Description

This project is a **test automation framework** for a banking API ([banking-api](https://github.com/Viktorm-tech/banking-api)). It showcases:

- **API tests** – verifying REST endpoints (account creation, deposits, withdrawals, transfers).
- **Integration tests** – validating data in PostgreSQL and events in Kafka.
- **External service mocking** – WireMock for stubbing the limits check service.
- **Containerization** – Docker Compose to spin up the entire environment.
- **CI/CD** – GitHub Actions with automated test execution and Allure report generation.

The project is implemented in **Java 21** (Maven)
