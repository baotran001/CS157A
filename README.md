# CS157A

### QuizMe

## Team Members:
Hai Ly
Isabel Luong
Baotran Nguyen

## Set Up & Compiling
To run this project, Visual Studio Code (VSCode) and MySQL must be installed.

In your terminal, do the following:

```
$ mysql -u root -p
```

You will be prompted to enter your MySQL password. Once you have, continue with the following:

```
$ use mysql;
$ ALTER USER 'root'@'localhost' IDENTIFIED BY 'Bb32003211';
$ FLUSH PRIVILEGES;
$ quit
```

Open VSCode and open a directory of your choice. Running this project requires all the files from the GitHub Repository. You can do that by downloading all the files and dragging it to your chosen directory. Or, you can open a new terminal inside your current directory and clone the GitHub Repository with the following command:

```
$ git clone https://github.com/baotran001/CS157A
```

Next, on the left side, find and go to the "Extensions" tab. Search and download the following:
* "Maven for Java" by Microsoft
* "Extension Pack for Java" by Microsoft
* "Spring Boot Extension Pack" by VMware

Once every is downloaded, return to the "Explorer" tab on the left side. Find and open the file "ServerApplication.java" located under this path:

CS157A/server/src/main/java/com/example/server/ServerApplication.java

Once the file is open, run it. Wait for about 10 seconds for SpringBoot to finish setting up before running this link in your browser:

http://localhost:8080/quizMeDB/login

Congrats! You have finished setting up the application. To quit, simply close the application and stop running the Java file.