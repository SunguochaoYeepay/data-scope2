# DataScope Deployment Guide

## System Requirements

### Hardware Requirements
- CPU: 4+ cores
- RAM: 16GB minimum, 32GB recommended
- Storage: 100GB minimum, SSD recommended
- Network: 1Gbps minimum

### Software Requirements
- Java 17 or higher
- MySQL 8.0 or higher
- Redis 6.0 or higher
- Maven 3.8 or higher
- Nginx (optional, for load balancing)

## Pre-deployment Setup

### Database Setup
1. Create MySQL database:
```sql
CREATE DATABASE datascope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Create database user:
```sql
CREATE USER 'datascope'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON datascope.* TO 'datascope'@'%';
FLUSH PRIVILEGES;
```

3. Initialize database schema:
```bash
mysql -u datascope -p datascope < schema.sql
```

### Redis Setup
1. Install Redis:
```bash
# Ubuntu/Debian
apt-get update
apt-get install redis-server

# CentOS/RHEL
yum install redis
```

2. Configure Redis:
```bash
# Edit /etc/redis/redis.conf
bind 127.0.0.1
port 6379
maxmemory 2gb
maxmemory-policy allkeys-lru
```

3. Start Redis:
```bash
systemctl start redis
systemctl enable redis
```

## Application Deployment

### Build Process
1. Clone repository:
```bash
git clone https://github.com/your-org/data-scope.git
cd data-scope
```

2. Configure application properties:
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Edit application.yml with your configuration
```

3. Build application:
```bash
mvn clean package -DskipTests
```

### Configuration Files

#### application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datascope?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: datascope
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    database: 0

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  config: classpath:logback-spring.xml
  level:
    root: INFO
    com.datascope: DEBUG

datascope:
  security:
    jwt:
      secret: your_jwt_secret
      expiration: 86400000
  query:
    timeout: 30000
    max-rows: 50000
  rate-limit:
    enabled: true
    capacity: 1000
    time-window: 60000
```

### Deployment Steps

#### Single Instance Deployment
1. Create service user:
```bash
useradd -r -s /bin/false datascope
```

2. Create directories:
```bash
mkdir -p /opt/datascope
mkdir -p /opt/datascope/logs
mkdir -p /opt/datascope/config
```

3. Copy files:
```bash
cp target/data-scope.jar /opt/datascope/
cp src/main/resources/application.yml /opt/datascope/config/
chown -R datascope:datascope /opt/datascope
```

4. Create systemd service:
```bash
# /etc/systemd/system/datascope.service
[Unit]
Description=DataScope Service
After=network.target

[Service]
User=datascope
ExecStart=/usr/bin/java -jar -Dspring.config.location=/opt/datascope/config/application.yml /opt/datascope/data-scope.jar
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

5. Start service:
```bash
systemctl daemon-reload
systemctl start datascope
systemctl enable datascope
```

#### Load Balanced Deployment

1. Configure Nginx:
```nginx
# /etc/nginx/conf.d/datascope.conf
upstream datascope {
    server 127.0.0.1:8080;
    server 127.0.0.1:8081;
}

server {
    listen 80;
    server_name datascope.example.com;

    location /api/ {
        proxy_pass http://datascope;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

2. Start multiple instances:
```bash
# Instance 1
java -jar -Dserver.port=8080 -Dspring.config.location=/opt/datascope/config/application.yml /opt/datascope/data-scope.jar

# Instance 2
java -jar -Dserver.port=8081 -Dspring.config.location=/opt/datascope/config/application.yml /opt/datascope/data-scope.jar
```

## Monitoring Setup

### Prometheus Configuration
```yaml
# /etc/prometheus/prometheus.yml
scrape_configs:
  - job_name: 'datascope'
    metrics_path: '/api/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Grafana Dashboard
1. Import dashboard template
2. Configure data source
3. Set up alerts

## Backup Strategy

### Database Backup
```bash
# Create backup script
#!/bin/bash
BACKUP_DIR="/backup/datascope"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u datascope -p datascope > $BACKUP_DIR/datascope_$DATE.sql
gzip $BACKUP_DIR/datascope_$DATE.sql

# Keep last 30 days of backups
find $BACKUP_DIR -name "datascope_*.sql.gz" -mtime +30 -delete
```

### Application Backup
```bash
# Backup configuration and logs
tar -czf /backup/datascope/config_$DATE.tar.gz /opt/datascope/config
tar -czf /backup/datascope/logs_$DATE.tar.gz /opt/datascope/logs
```

## Troubleshooting

### Common Issues

#### Application Won't Start
1. Check logs:
```bash
journalctl -u datascope -n 100
```

2. Verify database connection:
```bash
mysql -u datascope -p -h localhost datascope
```

3. Check Redis connection:
```bash
redis-cli ping
```

#### Performance Issues
1. Check system resources:
```bash
top
free -m
df -h
```

2. Monitor JVM:
```bash
jstat -gcutil $(pgrep -f data-scope.jar) 1000
```

3. Check slow queries:
```sql
SHOW FULL PROCESSLIST;
```

## Security Checklist

- [ ] Configure firewall rules
- [ ] Enable SSL/TLS
- [ ] Set up secure passwords
- [ ] Configure rate limiting
- [ ] Enable audit logging
- [ ] Set up backup encryption
- [ ] Configure JVM security options
- [ ] Review file permissions