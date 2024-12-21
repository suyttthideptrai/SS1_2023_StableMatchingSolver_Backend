## Docker compose for BE FE deployment. 

#### Currently under development, might not be running as expected.
#### Tested with Ubuntu 24.04. gi gi day LTS


### Step 1: pre-setup for deployment
```bash
sudo bash docker_setup.sh
```

### Step 2: Setup deployment system's public IP for Web Application

```bash
nano /frontend/.env
# Then change REACT_APP_BACKEND_URL to the correct public ipv4 of the server
```

### Step 3: Start docker
```bash
docker compose build
docker compose up -d
```
