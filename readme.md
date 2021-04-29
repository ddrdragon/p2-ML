
GitHub: https://github.com/ddrdragon/p2-ML

DokerHub: https://hub.docker.com/repository/docker/xd59/cs643-project2


### Setup AWS and Spark Cluster

1. Create 4 EC2 instances on AWS(1 master and 3 workers).


2. Generate public/private rsa key pair, and copy the public 
   key to every other server's `.ssh/authorized_keys` file. 
   Make sure they can ssh to other server without password.


3. Install Java and Spark on them.


4. Configure the master and works of spark and environment variables.


### Run Training Application

1. Upload TrainingDataSet.csv to every server, and training application to Master server.


2. Start spark cluster on Master server using: `start-all.sh`


3. Run training application using:
   
   `spark-submit --class edu.njit.cloudComputing.project2.Training Master-1.0.jar`

   Then, the model will be saved in the current directory.


### Run Prediction Application without Docker

1. Prediction is running on local mode. So you can stop the spark cluster by using: `stop-all.sh`


2. Run prediction application using: 

   `spark-submit --class edu.njit.cloudComputing.project2.Predict Master-1.0.jar`


### Run Prediction Application within Docker

1. Install Docker: `sudo apt  install docker.io`


2. Pull the Docker image: `sudo docker pull xd59/cs643-project2:latest`


3. Run prediction application: 

   `docker container run -p 8000:3000 -it prediction /bin/bash`


###### Please check the latest version on github. 
###### Current version 1.1