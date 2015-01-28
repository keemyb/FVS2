Website
===========================
The website can be accessed at http://keemyb.github.io/SEPR-LYS-A3 .
If you need to modify it, do so in the gh-pages branch.
Be aware though that changing to this branch will mean you have to re-import the project (atleast in Intellij IDEA) when you revert back to the 'normal' branches. This is because the ".idea" folder is ignored at will not be restored via git.
Alternatives include:

1. Making a seperate clone just for the web branch (bestest option).
2. Editing the index.html directly via github (not so bestest option).

Project Set-up Instructions
===========================

1. Clone the repository.  It helps.  Don't worry about your project files overwriting any other files, Git has been configured to ignore these.  If you're using the GitHub tools, also install good old fashioned Git as it integrated with both Eclipse and IntelliJ IDEA to make managing version control easier.
2. If you will be using eclipse, set your workspace to be the repository root.  You should also install the Gradle IDE from http://dist.springsource.com/snapshot/TOOLS/gradle/nightly.  You may also find the Git plug-in useful if it is not already installed.
3. In Eclipse go to File -> Import -> Gradle -> Gradle Project.  Choose the taxe folder in the repo and then press Build Model.  You should import both Core and Desktop, the taxe project is just a necessary wrapper and will never be used for anything.  Click Import and wait patiently.
4. To set up IntelliJ IDEA, clone the repository into IDEA projects folder. Now open up IDEA and select that you'll import a project from a file/folder.
<br/ >Select build.gradle file from &lt;local repository folder&gt;/taxe/core folder. Now IDEA does some importing and downloading.
<br />To setup run environment:
<br />Click Run -> Edit configurations...
<br />Click on + in upper left.
<br />Select Application and use Desktop for the name.
<br />Working directory: ../&lt;local repository folder&gt;/taxe/core/assets
<br />Classpath of module: desktop
<br />Main class: uk.ac.york.cs.sepr.fvs.taxe.desktop.DesktopLauncher
<br />You can try clicking on Run (Shift+F10) now and you should see the sample program work :)
