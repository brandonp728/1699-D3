import java.util.*;

public class LaboonHash {

    private static char[] result;
    private static int stringSize;
    
    ////////////////////////////////////////////////////////////////////////
    //////                                                            //////
    //////                                                            //////
    //////                                                            //////
    //////            Compression Function Related Methods            //////
    //////                                                            //////
    //////                                                            //////
    //////                                                            //////
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Compression function done in 3 phases and then hashed to a final
     * hexidecimal string result
     * @param lhs the lefthand side of the assignment
     * @param rhs the righthand side of teh assignment
     * @return The fully hashed hexidemical string after each phase has completed 
     */
    public static String c(String lhs, String rhs) {
        result = new char[4];
        char[] lhsChars = lhs.toCharArray();
        char[] rhsChars = rhs.toCharArray();
        
        phase_one(rhsChars, lhsChars);
        phase_two(rhsChars);
        phase_three();
        
        char[] hashedArray = hashCharArray(result);
        String hashString = new String(hashedArray);
        return hashString;
    }

    /**
     * Phase one of compression, take every value ascending in the 
     * characters of lhs, and every value descending in the characters 
     * of rhs, sum them, and add that sum to the result array
     * @param rhsChars  the character array of the righthand side
     * @param initChars the character array of the lefthand side
     */
    private static void phase_one(char[] rhsChars, char[] lhsChars) {
        int j = 3;
        for(int i = 0; i < 4; i++) {
            int rhsInt = (int)rhsChars[j];
            int lhsInt = (int)lhsChars[i];
            char newChar = (char)(rhsInt + lhsInt);
            result[i] = newChar;
            j--;
        }
    }

    /**
     * Phase two of compression, take every value ascending in the rhs 
     * character array and every value descending in the result character
     * array, XOR them and store that XOR in result
     * @param rhsChars the character array of the righthand side
     */
    private static void phase_two(char[] rhsChars) {
        int j = 7;
        for(int i = 0; i < 4; i++ ) {
            int resultInt = (int)result[i];
            int rhsInt = (int)rhsChars[j];
            char newChar = (char)(rhsInt ^ resultInt);
            result[i] = newChar;
            j--;
        }
    }

    /**
     * Phase three of compression, take every value ascending in the result 
     * character array and every value descending in the result character
     * array, XOR them and store that XOR in result
     */
    private static void phase_three() {
        int j = 3;
        for(int i = 0; i < 4; i++) {
            int resultCharLeft = (int)result[i];
            int resultCharRight = (int)result[j];
            char newChar = (char)(resultCharLeft ^ resultCharRight);
            result[i] = newChar;
            j--;
        }
    }
    
    /**
     * Given a character array, takes every valye modulo 16 and 
     * fills the array with a corresponding hexidecimal character 
     * @param charArray the character array hash
     * @return the character array containing the hexidecimal value
     */
    private static char[] hashCharArray(char[] charArray) {
        for(int i = 0; i < 4; i++) {
            int charValue = (int)charArray[i];
            int newValue = charValue % 16;
            charArray[i] = determineChar(newValue);
        }
        return charArray;
    }

    /**
     * Given an integer value 0...15 return the corresponding 
     * character in hexdecimal 0...F
     * @param value the value to be evaluated
     * @return the corresponding hexidecimal character 
     */
    private static char determineChar(int value) {
        char toReturn = '$';
        switch(value) {
            case 0:
                toReturn = '0';
                break;
            case 1:
                toReturn = '1';
                break;
            case 2:
                toReturn = '2';
                break;
            case 3:
                toReturn = '3';
                break;
            case 4:
                toReturn = '4';
                break;
            case 5:
                toReturn = '5';
                break;
            case 6:
                toReturn = '6';
                break;
            case 7:
                toReturn = '7';
                break;
            case 8:
                toReturn = '8';
                break;
            case 9:
                toReturn = '9';
                break;
            case 10:
                toReturn = 'A';
                break;
            case 11:
                toReturn = 'B';
                break;
            case 12:
                toReturn = 'C';
                break;
            case 13:
                toReturn = 'D';
                break;
            case 14:
                toReturn = 'E';
                break;
            case 15:
                toReturn = 'F';
                break;
        }
        return toReturn;

    }

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
            msg += "\nOther options: -verbose";
            System.out.println(msg);
            System.exit(0);
        }
        String toHash = args[0];
        stringSize = toHash.length();
        String mode = "nothing";
        try { 
            mode = args[1];
        } catch (ArrayIndexOutOfBoundsException e) { }
        
        String[] blocks = getBlocks(toHash);
        String lastBlock = blocks[blocks.length-1];
        if(lastBlock.length() < 8) {
            blocks = pad(blocks);
        }
        if(mode.equalsIgnoreCase("-verbose")) {
            System.out.print("\tPadded string: ");
            printPaddedString(blocks);
        }
        if(mode.equalsIgnoreCase("-verbose")) {
            System.out.println("\tBlocks: ");
            printBlocks(blocks);
        }
        String initVector = "1AB0";
        String prevHash = "";
        for(int i=0; i < blocks.length; i++) {
            if(i == 0) {
                if(mode.equalsIgnoreCase("-verbose")) {
                    prevHash = c(initVector, blocks[i]);
                    System.out.println("\tIterating with " + initVector + " / " + blocks[i] + " = " + prevHash);
                }
                else {
                    prevHash = c(initVector, blocks[i]);
                }
            } else {
                if(mode.equalsIgnoreCase("-verbose")) {
                    String oldHash = new String(prevHash);
                    prevHash = c(prevHash, blocks[i]);
                    System.out.println("\tIterating with " + oldHash + " / " + blocks[i] + " = " + prevHash);
                }
                else {
                    prevHash = c(prevHash, blocks[i]);
                }
            }
        }
        if(mode.equalsIgnoreCase("-verbose")) {
            System.out.println("\tFinal Result: " + prevHash);
        }
        System.out.println("LaboonHash hash = " + prevHash);
    }

    /**
     * Prints the string array of blocks on separate lines
     * @param blocks the array to print
     */
    private static void printBlocks(String[] blocks) {
        for(int i = 0; i < blocks.length; i++) {
            System.out.println("\t" + blocks[i]);
        }
    }

    /**
     * Prints the string array of blocks as one full string
     * @param blocks the array to print
     */
    private static void printPaddedString(String[] blocks) {
        for(int i = 0; i < blocks.length; i++) {
            System.out.print(blocks[i]);
        }
        System.out.println();
    }

    /**
     * Determines a pad for the last block in the block array
     * @param blocks block array
     * @return blocks but now with the last index padded appropriately
     */
    private static String[] pad(String[] blocks) {
        int length = blocks.length;
        int strLen = blocks[length-1].length();
        int sizeToPad = 8 - strLen;
        int modValue = (int)Math.pow(16, sizeToPad);
        int modLen = stringSize % modValue;
        String pad = String.format("%0" + sizeToPad + "X", modLen);
        blocks[length-1] += pad;
        return blocks;
    } 

    /**
     * Splits toFind into blocks of size 8 each
     * @param toFind String to be divided into blocks
     * @return a string array containing all the blocks created from this string
     */
    private static String[] getBlocks(String toFind) {
        if(toFind.length() <= 8) {
            String[] block = new String[1];
            block[0] = toFind;
            return block;
        } else {
            ArrayList<String> blocks = new ArrayList<String>();
            int length = toFind.length();
            String newString = new String(toFind);
            int i=0;
            int beginIndex = 0;
            int endIndex = 0;
            while(i < length) {
                if(i % 8 == 0 && i != 0) {
                    endIndex = i;
                    String block = newString.substring(beginIndex, endIndex);
                    blocks.add(block);
                    beginIndex += block.length();
                }
                i++;
            }
            String block = newString.substring(beginIndex, length);
            blocks.add(block);

            return blocks.toArray(new String[blocks.size()]);
        }
    }


}