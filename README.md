# Employee Microservice

A small Spring Boot REST microservice with CRUD APIs, Spring Data JPA, validation, and a file-based H2 database. It is designed for learning and a straightforward AWS EC2 deployment.

## Requirements
- Java 17
- Maven 3.8+
- AWS EC2 Ubuntu instance (for deployment)
- Postman (collection is in `postman/`)

## Run locally

From the project root:

```bash
mvn clean package
java -jar target/employee-microservice-1.0.0.jar
```

The service starts at `http://localhost:8080`.

## REST endpoints

| Method | URL | Description |
|---|---|---|
| POST | `/api/employees` | Create employee |
| GET | `/api/employees` | List employees |
| GET | `/api/employees/{id}` | Get employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Delete employee |

Example POST/PUT JSON:

```json
{
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "department": "Engineering"
}
```

Successful create returns HTTP `201`; delete returns HTTP `204`. Invalid input returns `400`, duplicate email generally returns `409`, and missing employee returns `404`.

## Import into Postman

1. Open Postman.
2. Click **Import**.
3. Import `postman/Employee-Microservice.postman_collection.json`.
4. Import `postman/Employee-Microservice.postman_environment.json`.
5. Select the **Employee Microservice - Local** environment.
6. For EC2 testing, change `baseUrl` to `http://YOUR_EC2_PUBLIC_IP:8080`.

The collection automatically saves the created employee ID after the Create request.

## Deploy to AWS EC2 (Ubuntu)

### 1. Launch an EC2 instance
- Choose Ubuntu Server 22.04 LTS or 24.04 LTS.
- A small instance is sufficient for learning.
- In the EC2 Security Group inbound rules, allow:
  - SSH TCP `22` from **your IP only**.
  - Custom TCP `8080` from **your IP only** while testing.
- Do not expose SSH to `0.0.0.0/0`. For production, put the service behind a reverse proxy/load balancer and use HTTPS rather than exposing the app port publicly.

### 2. Connect to EC2

From your computer (replace the key and address):

```bash
chmod 400 your-key.pem
ssh -i your-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

### 3. Install Java and Maven

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven unzip
java -version
mvn -version
```

### 4. Upload and extract the project

Run these commands on **your local computer**, from the directory containing the ZIP:

```bash
scp -i your-key.pem employee-microservice.zip ubuntu@YOUR_EC2_PUBLIC_IP:/home/ubuntu/
```

Then, inside the EC2 SSH session:

```bash
cd /home/ubuntu
unzip -o employee-microservice.zip
cd employee-microservice
```

### 5. Build and start the service

```bash
mvn clean package
mkdir -p data
nohup java -jar target/employee-microservice-1.0.0.jar > app.log 2>&1 &
```

Check startup:

```bash
tail -f app.log
```

Stop viewing logs with `Ctrl+C` (this does not stop the application).

### 6. Test from Postman

Set the environment variable `baseUrl` to:

```text
http://YOUR_EC2_PUBLIC_IP:8080
```

Send `GET {{baseUrl}}/api/employees`.

If it does not connect, verify the EC2 instance is running, the security group permits TCP 8080 from your current IP, and the application is listening:

```bash
sudo ss -lntp | grep 8080
```

### 7. Stop or restart the app

Find the process:

```bash
ps -ef | grep employee-microservice
```

Stop it using its PID:

```bash
kill YOUR_PROCESS_ID
```

Restart:

```bash
cd /home/ubuntu/employee-microservice
nohup java -jar target/employee-microservice-1.0.0.jar > app.log 2>&1 &
```

## Notes
- H2 uses a local file under `./data`; it survives application restarts, but it is not a managed/production database and does not protect against EC2 disk/instance loss.
- This sample has no authentication. Do not use it for real employee or sensitive production data.
- For production, consider RDS (PostgreSQL/MySQL), IAM/Secrets Manager, HTTPS, authentication/authorization, monitoring, backups, and a systemd service or container orchestration.
