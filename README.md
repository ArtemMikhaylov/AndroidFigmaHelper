# AndroidFigmaHelper
Script to move exported .png pictures from figma to android drawable folders.

You export pictures for mobile in all required densities into one directory. 
If you need, you also can export pictures for tablets the same way into another directory. 

Specify directories of those folders and your project, and just run the script.

General script flow goes this way: 

    1) changes names to newNames, if there's any specified
    2) replaces all " "  with "_" in names
    3) converts names to lowercase
    4) changes prefixesToChange to newPrefixes, if there's any specified
    5) attaches a newNamePrefix to each name if specified
    6) if file already exists, overwrite/skip question will be asked in console


For more info, please investigate the code. 

You also can run it as java module from Android Studio
https://stackoverflow.com/questions/16626810/can-android-studio-be-used-to-run-standard-java-projects
