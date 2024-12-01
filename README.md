# **Secure Password Manager**

A web application built using **Java Spring Boot** for the backend, **React** with **TypeScript** for the frontend, and **PostgreSQL** as the database. The project is configured with HTTPS using a self-signed certificate and supports environmental configurations through `.env` files. It is a project built for the **ICS0022/ICS0027** courses.

---

## **Prerequisites**

Ensure you have the following installed:

1. **Java** (JDK 17 or higher)
    - Verify installation:
      ```bash
      java -version
      ```

2. **Node.js** (version 16 or higher) and **Yarn**
    - Install Node.js: [Download Node.js](https://nodejs.org/)
    - Install Yarn:
      ```bash
      npm install -g yarn
      ```
    - Verify installation:
      ```bash
      node -v
      yarn -v
      ```

3. **PostgreSQL** (version 13 or higher)
    - Verify installation:
      ```bash
      psql --version
      ```

4. **SendGrid Account** (for email functionality)
    - Obtain an API key from [SendGrid](https://sendgrid.com/).

---

## **Clone the repository**
Run the following command to clone the repository:
```bash
git clone https://github.com/Glaiskar/ICS0022-0027.git
cd ICS0022-0027
```

## **Environment Configuration**

The project requires two `.env` files:

### **Backend `.env` (in the root directory)**
Create a file named `.env` in the root directory and add the following variables:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/secure_password_manager
SPRING_DATASOURCE_USERNAME=<your_postgres_username>
SPRING_DATASOURCE_PASSWORD=<your_postgres_password>
JWT_SECRET=<your_jwt_secret>
JWT_EXPIRATION=86400000
SENDGRID_API_KEY=<your_sendgrid_api_key>
SENDGRID_FROM_EMAIL=<your_sendgrid_email>
SENDGRID_FROM_NAME=<your_sendgrid_name>
KEYSTORE_PASSWORD=<your_keystore_password>
```
### **Frontend `.env` (in the `frontend` directory)**
Create a file named `.env` in the `frontend` directory and add the following variables:
```env
REACT_APP_API_URL=https://localhost:8443/api
```
---
## **Database Configuration**
1. Start PostgreSQL via PGAdmin or the command line:
   - On Linux: `sudo systemctl start postgresql`
   - On macOS: `sudo launchctl start postgresql`
   - On Windows: Use the PostgreSQL service manager.
2. Access the PostgreSQL shell:
   ```bash
   psql -U <your_postgres_username>
   ```
3. Create the database:
   ```sql
    CREATE DATABASE secure_password_manager;
    ```
4. Update the `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` in the backend `.env` file with your PostgreSQL username and password.
---
## **HTTPS Configuration**
1. Generate a self-signed certificate:
    ```bash
    keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365
    ```
   You will be prompted to enter a keystore password, your name, organization, and location. Remember the keystore password as it will be used in the backend `.env` file.
2. Place the `keystore.p12` file in the `src/main/resources` directory.
3. Update the `KEYSTORE_PASSWORD` in the backend `.env` file with the keystore password.
---
## **Backend Setup**
1. Navigate to the root directory:
    ```bash
    cd ICS0022-0027
    ```
2. Run the backend:
    ```bash
    ./mvnw spring-boot:run
    ```
3. Access the backend at `https://localhost:8443`.
4. The backend will automatically create the necessary tables in the database.
---
## **Frontend Setup**
1. Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2. Install the frontend dependencies:
    ```bash
    yarn install
    ```
3. Run the frontend:
    ```bash
    yarn start
    ```
4. Access the frontend at `https://localhost:3000`.
