pipeline {
    options {
        disableConcurrentBuilds()
    }

    agent {
        docker {
            image 'gradle:jdk8'
        }
    }

    stages {
        stage('Echo') {
            steps {
                echo 'Hello...'
                echo 'Are You There'
            }
        }
        stage('Setup') {
            steps {
                echo 'Building..'
                sh './gradlew setupCIWorkspace --refresh-dependencies'
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                sh './gradlew build apiJar sourcesJar deobfJar javadocJar'
            }
        }
    }
}