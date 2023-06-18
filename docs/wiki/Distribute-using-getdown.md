You can distribute *Amstrad PC* to end user computers using [getdown](https://github.com/threerings/getdown)

A *distribution* consists of

- The compiled code and resources of *Amstrad PC* to run the software
- A managed collection of Basic source code files

The advantange of a distribution is an easy installation process on end user computers and keeping it up-to-date without requiring explicit actions from end users. The only requirement is an active internet connection, although offline mode is supported once the software has been installed



## Create a distribution

To create a distribution, follow these steps

1. Obtain source code from [github](https://github.com/jandebr/amstradPc)
2. Build using `ant -buildfile ant.xml -Ddistribution.appbase=https://hostname/did/ -Ddistribution.id=did -Ddistribution.programbase=distpath/to/programs -Dprogram-repo.source=/localpath/to/programs distribute`
    - **distribution.appbase** defines the URL from which the distribution can be downloaded
    - **distribution.id** is your distribution name and should match the web context root in *distribution.appbase*
    - **distribution.programbase** is the relative path of the program collection in the distribution
    - **program-repo.source** is the absolute path to the program collection to include in the distribution
    - When successfully run, produces `dist/amstradPc.war`
3. Deploy `dist/amstradPc.war` on a web server of choice



## Install a distribution

To install a distribution on a client pc, follow these steps

1. Modify `install/getdown.txt` to contain a single line referencing the URL from which the distribution can be downloaded
    ```
    appbase = https://hostname/did/
    ```
2. On the client pc, create an empty installation folder and copy the following files into the folder. The files can be found in the [install](https://github.com/jandebr/amstradPc/tree/main/install) folder
    - `getdown.jar` as the launcher jar
    - `getdown.txt` as the launcher configuration
    - `javacpc.ini` as the initial *Amstrad PC* configuration
3. On the client pc, make sure a Java Runtime Environment (JRE) is available (version 9 or higher)
4. On the client pc, go into the installation folder and run the launcher (see next section)



## Run a distribution
On the client pc, go into the installation folder and run the launcher. Use one of the [run modes](Run-modes) as listed below
- `java -jar getdown.jar . default` for default mode
- `java -jar getdown.jar . kiosk` for kiosk mode
- `java -jar getdown.jar . original` for original *JavaCPC* mode
- `java -jar getdown.jar` to use the mode from previous run



## Update a distribution

To update a distribution, follow these steps

1. Make the changes
2. Create and deploy an updated `dist/amstradPc.war` by following the same steps under [create distribution](#create-a-distribution)
3. Client pc's connected to the internet will automatically update on the next [application run](#run-a-distribution)
