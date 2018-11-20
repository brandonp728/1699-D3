import java.util.ArrayList;

public class LaboonCrypt {

    ////////////////////////////////////////////////////////////////////////
    //////                                                            //////
    //////                                                            //////
    //////                                                            //////
    //////                   Main and Related Methods                 //////
    //////                                                            //////
    //////                                                            //////
    //////                                                            //////
    ////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        if(args.length > 3 || args.length < 1) {
            String msg = "Verbosity flag can be omitted for hash output only";
            msg += "\nOther options: -verbose -veryverbose -ultraverbose";
            System.out.println(msg);
            System.exit(0);
        }
        String input = args[0];
        String mode = "nothing";
        try {
            mode = args[1];
        } catch(IndexOutOfBoundsException e) { }
        int intMode = generateModeInt(mode);
        
        String[] blocks = LaboonHash.getBlocks(input);
        String lastBlock = blocks[blocks.length-1];
        if(lastBlock.length() < 8) {
            blocks[blocks.length-1] = LaboonHash.pad(lastBlock, input.length());
        }

        if(intMode == 3) {
            System.out.print("\tPadded string: ");
            LaboonHash.printPaddedString(blocks);
            System.out.println("\tBlocks: ");
            LaboonHash.printBlocks(blocks);
        }
        
        String initVector = "1AB0";
        String prevHash = "";
        for(int i=0; i < blocks.length; i++) {
            if(i == 0) {
                if(intMode == 3) {
                    prevHash = LaboonHash.c(initVector, blocks[i]);
                    System.out.println("\tIterating with " + initVector + " / " + blocks[i] + " = " + prevHash);
                }
                else {
                    prevHash = LaboonHash.c(initVector, blocks[i]);
                }
            } else {
                if(intMode == 3) {
                    String oldHash = new String(prevHash);
                    prevHash = LaboonHash.c(prevHash, blocks[i]);
                    System.out.println("\tIterating with " + oldHash + " / " + blocks[i] + " = " + prevHash);
                }
                else {
                    prevHash = LaboonHash.c(prevHash, blocks[i]);
                }
            }
        }
        if(intMode == 3) {
            System.out.println("\tFinal Result: " + prevHash);
        }
        String[][] matrix = fillArrayAndCompressInput(prevHash, intMode);
        if(intMode >= 1) { 
            System.out.println("Inital Array:");
            printArray(matrix);
        }
        matrix = rereadMatrixAndReHash(matrix, input, intMode);
        if(intMode >= 1) { 
            System.out.println("Final Array:");
            printArray(matrix);
        }
        String hashedMatrix = concatAndHashMatrix(matrix, input, intMode);
        System.out.println("LaboonHash hash = " + hashedMatrix);

    }

    /**
     * Does the initial hashing and filling of the matrix
     * @param prevHash the hash made from the user input string
     * @param intMode the level of verbosity to use
     * @return a filled matrix
     */
    private static String[][] fillArrayAndCompressInput(String prevHash, int intMode) {
        String initVector = "1AB0";
        String[][] matrix = new String[12][12];
        for(int i=0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                if(i == 0 && j == 0) {
                    matrix[i][j] = prevHash;
                } else {
                    if (intMode == 3) {
                        String toHash = LaboonHash.pad(prevHash, 4);
                        prevHash = LaboonHash.c(initVector, toHash);
                        matrix[i][j] = prevHash;
                        System.out.println("\tPadded string: " + toHash);
                        System.out.println("\tBlocks: \n\t" + toHash);
                        System.out.println("\tIterating with " + initVector + " / " + toHash + " = " + prevHash);
                        System.out.println("\tFinal Result: " + prevHash);
                    } else {
                        String toHash = LaboonHash.pad(prevHash, 4);
                        prevHash = LaboonHash.c(initVector, toHash);
                        matrix[i][j] = prevHash;
                    }
                    
                }
            }
        }
        return matrix;
    }

    /**
     * Given a 2D Array, print out the contents
     * @param matrix the array to print
     */
    private static void printArray(String[][] matrix) {
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Takes the verbosity level and assigns it an integer
     * -verbose         -> 1
     * -veryverbose     -> 2
     * -ultraverbose    -> 3
     * @param mode the verbosity level
     * @return an integer 1-3 representing the verbosity level, or -1 if not verbose
     */
    private static int generateModeInt(String mode) {
        int toReturn = -1;
        switch(mode.toLowerCase()) {
            case "-verbose" :
                toReturn = 1;
                break;
            case "-veryverbose":
                toReturn = 2;
                break;
            case "-ultraverbose":
                toReturn = 3;
                break;
        }
        return toReturn;
    }

    /**
     * This hashes certain indexes depending on the ascii values of each 
     * character of the initial user string
     * @param matrix the matrix to be editted
     * @param input the initial string input of the user
     * @param intMode the verbosity level in integer form
     * @return a new array with the correct indexes editted
     */
    private static String[][] rereadMatrixAndReHash(String[][] matrix, String input, int intMode) {
        char[] inputChars = input.toCharArray();
        String initVector = "1AB0";
        String prevHash = "";
        int prevX = 0;
        int prevY = 0;
        for(int i = 0; i < inputChars.length; i++) {
            int asciiValue = (int) inputChars[i];
            int jumpY = ((asciiValue + 3) * 7);
            int jumpX = (asciiValue * 11);
            int newX = (jumpX + prevX) % 12;
            int newY = (jumpY + prevY) % 12;
            prevX = newX;
            prevY = newY;
            String oldHash = matrix[newX][newY];
            String toHash = LaboonHash.pad(oldHash, 4);
            prevHash = LaboonHash.c(initVector, toHash);
            matrix[newX][newY] = prevHash;
            if(intMode >= 2) {
                if(intMode == 3) {
                    System.out.println("\tPadded string: " + toHash);
                    System.out.println("\tBlocks: \n\t" + toHash);
                    System.out.println("\tIterating with " + initVector + " / " + toHash + " = " + prevHash);
                    System.out.println("\tFinal Result: " + prevHash);
                }
                System.out.print("Moving " + jumpX + " down " + " and " + jumpY + " right ");
                System.out.print(" - " + "modifying " + "[" + newX + ", " + newY+ "] ");
                System.out.print("from " + oldHash + " to " + prevHash + "\n");
            }
        }
        return matrix;
    }

    /**
     * This concatenates the entire 2D array into a string, hashes the string
     * and returns the result as a string to the user
     * @param matrix the matrix to be concatenated
     * @param input initial user input
     * @param intMode the verbosity level in integer form
     * @return the hashed string of the entire matrix
     */
    private static String concatAndHashMatrix(String[][] matrix, String input, int intMode) {
        String toHash = "";
        String prevHash = "";
        String initVector = "1AB0";
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                toHash += matrix[i][j];
            }
        }
        String[] blocks = LaboonHash.getBlocks(toHash);
        String lastBlock = blocks[blocks.length-1];
        if(lastBlock.length() < 8) {
            blocks[blocks.length-1] = LaboonHash.pad(lastBlock, input.length());
        }

        if(intMode == 3) {
            System.out.print("\tPadded string: ");
            LaboonHash.printPaddedString(blocks);
            System.out.println("\tBlocks: ");
            LaboonHash.printBlocks(blocks);
        }

        for(int i = 0; i < blocks.length; i++) {
            if(i == 0) {
                prevHash = LaboonHash.c(initVector, blocks[i]);
                if(intMode == 3) {
                    System.out.println("\tIterating with " + initVector + " / " + blocks[i] + " = " + prevHash);
                }
            }
            else {
                String oldHash = new String(prevHash);
                prevHash = LaboonHash.c(oldHash, blocks[i]);
                if(intMode == 3) {
                    System.out.println("\tIterating with " + oldHash + " / " + blocks[i] + " = " + prevHash);
                }
            }
        }
        if(intMode == 3) {
            System.out.println("\tFinal Result: " + prevHash);
        }
        return prevHash;
    }
}