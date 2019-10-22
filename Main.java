import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final String FIGMA_RES = "C:\\Users\\Artem\\Desktop\\figma_res";
    private static final String APP_RES = "C:\\GitProjects\\NadymProjects\\EG_Kids\\app\\src\\main\\res";
    private static final String DRAWABLE = "\\drawable";
    private static final String SW_600 = "-sw600dp";
    private static final String NODPI = "-nodpi";

    //x1, x1.5, x2, x3, x4
    private String[] figma = {"", "@2x", "@2x-1", "@3x", "-1"};
    private String[] destination = {"-mdpi", "-hdpi", "-xhdpi", "-xxhdpi", "-xxxhdpi"};

    //all figma schemes, etc. ingonred, move everything from FIGMA_RES to drawable-nodpi
    private boolean goNodpi = false;

    //no need to change it to false if tablet folder is empty, but optionally you can
    private boolean includeTablets = true;

    //starting with 0.5 from original, e.g. x0.5, x0.75, x1, x1.5, x2
    private boolean useFigmaV2Scheme = false;
    private String[] figmaV2 = {"", "-1", "-2", "@2x", "@2x-1"};


    private String[] scheme;

    /*
    Script flow:

    1) changes names to newNames, if there's any specified
    2) replaces all " "  with "_" in names
    3) converts names to lowercase
    4) changes prefixesToChange to newPrefixes, if there's any specified
    5) attaches a newNamePrefix to each name if specified
    6) if file already exists, overwrite/skip question will be asked in console
     */

    //change one name to another
    private String[] names = {

    };
    private String[] newNames = {

    };

    //lowercase only !!!  all strings is gonna be converted to lowercase by now
    //for example "pic_"  to "ic_"
    private String[] prefixesToChange = {

    };
    private String[] newPrefixes = {

    };

    //attach name prefix to all
    private String newNamePrefix = null;

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
        System.out.println("run!");
        scheme = useFigmaV2Scheme ? figmaV2 : figma;
        try {
            File figmaDir = new File(FIGMA_RES);
            for (File file : figmaDir.listFiles()) {
                moveFile(file, false);
            }

            if (includeTablets) {
                File figmaTabletsDir = new File(FIGMA_RES+SW_600);
                for (File file : figmaTabletsDir.listFiles()) {
                    moveFile(file, true);
                }
            }

            System.out.println("finish!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR!");
        }
    }

    private ArrayList<String> namesToOverwrite = new ArrayList<>();
    private ArrayList<String> namesToSkip = new ArrayList<>();

    private void moveFile(File file, boolean forTablets) {
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf(".");
        String name = fileName.substring(0, extensionIndex);
        String extension = fileName.substring(extensionIndex);

        int index = getIndex(name);
        String cleanName = getCleanName(name, index);
        String newName = getNewName(cleanName);
        File destination = getDestination(index, forTablets);

        if (!destination.exists()) {
            destination.mkdir();
        }

        File newFile = new File(destination + "\\" + newName + extension);

        if (newFile.exists()) {

            for(String skip : namesToSkip) {
                if (skip.equals(newName)) {
                    System.out.println(newName + " skip");
                    return;
                }
            }

            boolean shouldBeOverwritten = false;

            for (String overwrite : namesToOverwrite) {
                if (overwrite.equals(newName)) {
                    shouldBeOverwritten = true;
                    break;
                }
            }

            if (shouldBeOverwritten) {
                boolean deleted = newFile.delete();

                if (!deleted) {
                    System.out.println("STOPPED WITH ERROR: couldn't delete file: ");
                    System.out.println(newFile.toString());
                    System.exit(0);
                }
            } else {
                System.out.println(newFile.toString());
                System.out.println("is already Exists!");
                System.out.print("Do you want to overwrite it and alternative files? (skip otherwise): (Y/N)");

                try {
                    Scanner scanner = new Scanner(System.in);
                    // get their input as a String
                    String answer = scanner.next();

                    if (answer.equalsIgnoreCase("N")) {
                        namesToSkip.add(newName);
                        System.out.println("skip");
                        System.out.println("---");
                        return;
                    } else if (!answer.equalsIgnoreCase("Y")) {
                        System.out.println("STOPPED WITH ERROR: expected \"y\" or \"n\" ");
                        System.exit(0);
                    }

                    boolean deleted = newFile.delete();

                    if (!deleted) {
                        System.out.println("STOPPED WITH ERROR: couldn't delete file: ");
                        System.out.println(newFile.toString());
                        System.exit(0);
                    }

                    namesToOverwrite.add(newName);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            }
        }

        boolean result = file.renameTo(newFile);
        if (result) {
            System.out.println("SUCCESS:");
            System.out.println(fileName + " moved as " + newName + " to:");
            System.out.println(newFile.toString());
        }
    }

    private int getIndex(String name) {
        if (useFigmaV2Scheme) {
            //from right to left cause there's -1 at right side and @2-1 at left side
            for (int i = scheme.length - 1; i > 0; i--) {
                if (name.endsWith(scheme[i])) {
                    return i;
                }
            }
        } else {
            for (int i = 1; i < scheme.length; i++) {
                if (name.endsWith(scheme[i])) {
                    return i;
                }
            }

        }
        return 0;
    }

    private String getCleanName(String name, int index) {
        if (index > 0) {
            return name.substring(0, name.length() - scheme[index].length());
        }
        return name;
    }

    private String getNewName(String name) {
        String newName = name;
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                newName =  newNames[i];
            }
        }

        newName = newName.replaceAll(" ", "_");
        newName = newName.toLowerCase();

        for (int i = 0; i < prefixesToChange.length; i++) {
            newName = newName.replace(prefixesToChange[i], newPrefixes[i]);
        }

        if (newNamePrefix != null) {
            newName = newNamePrefix + newName;
        }

        return newName;
    }

    private File getDestination(int index, boolean tablets) {
        if (goNodpi) {
            return new File(APP_RES + DRAWABLE + NODPI);
        }
        StringBuilder sb = new StringBuilder(APP_RES);
        sb.append(DRAWABLE);
        if (tablets) {
            sb.append(SW_600);
        }
        sb.append(destination[index]);
        return new File(sb.toString());
    }
}
