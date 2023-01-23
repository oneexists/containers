#!/usr/bin/env bash

show_help() {
    echo "--------------------------------------------"
    echo "      CONTAINERS APPLICATION HELP MENU"
    echo "--------------------------------------------"
    echo "Usage: ./run.sh [ OPTION ]"
    echo
    echo "Option        Action"
    echo "--------------------------------------------"
    echo "-h            help menu"
    echo "-i            initialization instructions"
    echo "-k            kill/stop the application"
    echo "-u            update application"
}

instructions() {
    echo "Initialization Instructions:"
    echo "----------------------------------------------------------"
    echo "1. Ensure Docker is installed using WSL 2."
    echo
    echo "2. Install the JDK through WSL:"
    echo "  sudo apt update"
    echo "  sudo apt install openjdk-17-jdk"
    echo "  sudo update-alternatives --config java"
    echo
    echo "The last command will state there is nothing to configure."
    echo "Restart terminal and confirm setup by running 'mvn -v' to"
    echo "return the Maven verison."
    echo
    echo "If Maven doesn't return its version, then it can be installed"
    echo "using the command:"
    echo "  sudo apt install maven"
    echo
    echo "3. Run the application setup"
    echo "  The first time running the application will step through the"
    echo "  database configuration and return the environment variable"
    echo "  configuration needed. Add the environment variables to your"
    echo "  Bash configuration either in the ~/.bashrc file or"
    echo "  ~/.bash_profile file."
    echo
    echo "  After this is completed, run the script again to finish"
    echo "  initialization of the database and application containers."
    echo
    echo "Once initialization is complete, the application can be started"
    echo "using './run.sh' and stopped by using './run.sh -k to shut down."
}

configure_environment() {
    docker volume create containers_config
    docker volume create containers_data
    docker network create containers_net

    read -p "Database Name [containers]: " db_name
    if [[ -z $db_name ]]; then
        db_name=containers
    fi

    read -p "Database Password: " db_password
    while [[ -z $db_password ]]; do
        read -p "Database Password: " db_password
    done

    echo
    echo "Please add the following to your ~/.bashrc or ~/.bash_profile"
    echo "and restart your terminal to apply the changes. Then run this"
    echo "script again to finish database initialization."
    echo
    echo "# CONTAINERS DATABASE CONFIG"
    echo 
    echo "export PATH=\"/usr/lib/jvm/java-17-openjdk-amd64/bin/java:\$PATH\""
	echo "export JAVA_HOME=\"/usr/lib/jvm/java-17-openjdk-amd64\""
    echo "export CONTAINERS_DB_URL=jdbc:mysql://containers-db:3306/$db_name"
    echo "export CONTAINERS_DB_USERNAME=root"
    echo "export CONTAINERS_DB_PASSWORD=$db_password"
}

configure_database() {
    echo ">> Creating application image..."
    ./mvnw package -DskipTests spring-boot:build-image
    echo ">> Application image created!"

    echo ">> Creating database..."
    docker run -it -d -v mysql_data:/var/lib/mysql -v containers_config:/etc/mysql/conf.d --network containers_net --name containers-db -e MYSQL_ROOT_PASSWORD="$CONTAINERS_DB_PASSWORD" -p 3306:3306 mysql
    sleep 5
    docker exec -i containers-db mysql -u root -p"$CONTAINERS_DB_PASSWORD" <<< 'CREATE DATABASE '${CONTAINERS_DB_URL:32}';'
    docker exec -i containers-db mysql -u root -p"$CONTAINERS_DB_PASSWORD" < sql/schema.sql
    echo ">> Database created!"

    echo ">> Creating application container..."
    docker run -d --name containers-api --network containers_net -e CONTAINERS_DB_URL=$CONTAINERS_DB_URL -e CONTAINERS_DB_USERNAME=root -e CONTAINERS_DB_PASSWORD="$CONTAINERS_DB_PASSWORD" -p 8080:8080 containers:spring-boot
    echo ">> Application container created and running!"
}

setup() {
    if [[ -z $CONTAINERS_DB_URL ]]; then
        configure_environment
    else
        configure_database
    fi
}

stop_application() {
    echo ">> Shutting down application..."
    docker stop containers-api
    docker stop containers-db
    echo ">> Containers application has shut down."
}

run() {
    echo ">> Starting database..."
    docker start containers-db
    echo ">> Starting application..."
    docker start containers-api
}

update_application() {
    echo ">> Removing application container..."
    docker stop containers-api
    docker rm containers-api
    echo ">> Starting database..."
    docker start containers-db
    echo ">> Creating new application container..."
    ./mvnw package -DskipTests spring-boot:build-image
    docker run -d --name containers-api --network containers_net -e CONTAINERS_DB_URL=$CONTAINERS_DB_URL -e CONTAINERS_DB_USERNAME=root -e CONTAINERS_DB_PASSWORD="$CONTAINERS_DB_PASSWORD" -p 8080:8080 containers:spring-boot
    echo ">> Application updated and running!"
}

while getopts hiku option; do
    case $option in
        h) show_help;;
        i) instructions;;
        k) stop_application;;
        u) update_application;;
    esac
done

if [ $OPTIND -eq 1 ]; then
    if [[ $(docker container ls -a) =~ "containers-api" ]]; then
        run
    else
        setup
    fi
fi