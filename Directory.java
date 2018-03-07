/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author Justin Wheeler
 */

public class Directory {  
    private boolean endProgram;             //Used to end mainloop & program
    private boolean isDirectorySelected;    //Used to check if conditions are met
    private File file = null;                
    Scanner scan; 
    
    //Setter for endProgram
    public void setEndProgram(boolean endProgram) {
        this.endProgram = endProgram;
    }
    
    public static void main(String[] args) throws IOException{
        
        //Create new directory instance
        Directory directory = new Directory();
        directory.scan = new Scanner(System.in);
        directory.setEndProgram(false);
        
        //Main loop of the program. Ends when 0 is inputted on the menu screen.
        do {
            //Lists the menu options
            directory.listOptions();
            
            System.out.println("Please input your chosen menu option:");
            //Scan for user input.
            String input = directory.scan.next();
            int InputValue;
            
            try {
                //Parses input for int, if non-exist catches error below
                InputValue = Integer.parseInt(input);
                
                //Ensures value is between 0 and 7 before proceeding
                if (InputValue >= 0 && InputValue <=7 ){
                    //Passes user input to method which follow through with their selection
                    directory.optionSelect(InputValue);
                }
                else{
                    System.out.println("Input is not an value between or equal to 0 to 7");
                }
            } catch (NumberFormatException ne) {
                System.out.println("Input is not a number, continue");
            }  
        }while(!directory.endProgram);
        
    }
    
    /** This method uses a switch statement to pass the users selection
     * to the appropriate method.
     * @param option User menu selection. **/
    public void optionSelect(int option) throws IOException{
        
        switch (option) {
            case 0: setEndProgram(true);
                break;
            case 1: selectDirectory();
                break;
            case 2: readDirectory(file);
                break;
            case 3: readDirectoryAll(file, false, ""); 
                break;
            case 4: deleteFile();
                break;
            case 5: readFileAsHex();
                break;
            case 6: fileToBytes(6);
                break;
            case 7: fileToBytes(7);
                break;
            default: System.out.println("Choose only a value from 0 and 7.");
                break;
        }
    }
    
    /** Lists options avaliable to user. **/
    public void listOptions(){
        String options = "0 - Exit\n"
                + "1 - Select directory\n"
                + "2 - List directory content (first level)\n"
                + "3 - List directory content (all levels)\n"
                + "4 - Delete file\n"
                + "5 - Display file (hexadecimal view)\n"
                + "6 - Encrypt file (XOR with password)\n"
                + "7 - Decrypt file (XOR with password)\n";
        
        System.out.print(options);
    }
    
    /** Takes user input **/
    public void selectDirectory(){
        System.out.println("Input the absolute path name...");
        String path = scan.next();
        file = new File(path);
        
        //Check to make sure user doesn't provide a bogus path
        if (file.exists()){
            
            //Set to true to allow other functions to work now that condition is met.
            isDirectorySelected = true;
        }
        else
        {
            //Set to null and false to prevent unpredictable edge cases.
            file = null;
            isDirectorySelected = false;
            System.out.println("The path you entered was not accepted. Returning to menu...");
        }
        
    }
    
    /** Prints directory to console and prints the subdirectories to console recursively.
     * @param f This is the directory selected in selectDirectory()
     * @param subDirectory Boolean value for use recursively. Checks if the 
     * function call is a recursive call or top level.
     * @param tab On each recursive call, this is used to space the printed
     values appropriately **/
    public void readDirectoryAll(File f, boolean subDirectory, String tab) throws IOException{
        //Edge case to prevent user from continuing without directory being selected.
        if (!isDirectorySelected)
        {
            System.out.println("You must select a directory first.");
            return;
        }
        
        //Creates array of the files located at the path specificed
        File[] files = f.listFiles();
        
        //Loops through array and prints to console.
        for(File file : files){
            
            //If file is sub-directory, list its contents also
            if (file.isDirectory()){
                System.out.print(tab+"directory: ");
                System.out.println(file.getCanonicalPath());
                //Calls this function on the sub directory
                readDirectoryAll(file, true, tab+"\t");
            }else {
                    System.out.print(tab+"-file: ");
                    System.out.println(file.getCanonicalPath());
            }       
        }
    }
    
    /** Prints selected directory to console
     * @param f This is the directory selected in selectDirectory()**/
    public void readDirectory(File f) throws IOException{
        //Edge case to prevent user from continuing without directory being selected.        
        if (!isDirectorySelected)
        {
            System.out.println("You must select a directory first.");
            return;
        }
        
        //Creates array of the files located at the path specificed
        File[] files = f.listFiles();
        
        //Loops through array and prints to console.
        for(File file : files){
            if (file.isDirectory()){
                System.out.print("directory: ");
            }else {
                System.out.print("-file: ");
            }
            System.out.println(file.getCanonicalPath());
        }
    }
    
    /** Deleted selected file in specified directory. **/
    public void deleteFile() throws IOException{
        System.out.println("Input the file you want to delete (Not the path name, just file): ");

        //Take user input
        String filename = scan.next();
        
        //Makes sure a directory is selected prior to continuing
        if (isDirectorySelected){
            
            //Adds file name to end of the specified path
            File deleteFile = new File(file.getAbsolutePath()+"\\"+filename);
            
            //If the file exists and isn't protected, it'll be deleted
            if (deleteFile.delete()){
            System.out.println(filename
                       +" was deleted sucessfully."); 
            }else{
                System.out.println("Failed to delete.");
            }  
        }else
            System.out.println("Please Select a directory using option 2 before attempting other steps.");
    }

    /** Takes file, and reads its data in bytes converted to hex **/
    public void readFileAsHex() throws IOException{
        //Declare FileInputStream for file intending to be read
        FileInputStream readFile;
        
        //User specifies file to read
        String filename = scan.next();
        
        //If directory isn't selected return to menu
        if (isDirectorySelected){
            try {
                //Appends filename to the end of the specified directory
                readFile = new FileInputStream(file.getAbsolutePath()+"\\"+filename);
                
                int len;   
                byte data[] = new byte[16];
                
                //Reads file with 20 hex values on each line. Ends while file is read.
                do {
                    len = readFile.read(data);
                    for (int j = 0; j < len; j++) {
                        if (j%20==0){
                            System.out.print("\n");
                        }
                        System.out.printf("%02X ", data[j]);
                    }
                } while (len != -1);
            }
            catch(FileNotFoundException e){
                System.out.println("Please Select a directory using option 2 before attempting other steps.");
            }
            System.out.print("\n\n");
        }else
            System.out.println("Please Select a directory using option 2 before attempting other steps.");        
    }
    
    /** Reads file and converts to bytes to be encrypted or decrypted by a
     * separate method.
     * @param optionSelection Passes the option selected by user to ensure this
     method defers to the appropriate method in the next step.**/
    public void fileToBytes(int optionSelection) throws IOException{
        System.out.println("Please input filename:");
        
        //Takes next user input as filename
        String filename = scan.next();
        
        //Ensures a directory was selected or returns to menu
        if (isDirectorySelected){
            try {
                //Appends filename to directory selected
                Path path = Paths.get(file.getAbsolutePath()+"\\"+filename);
                
                //Reads the specified file in bytes
                byte[] byteArray = Files.readAllBytes(path);
                System.out.println(filename+" found.");
                
                String password;
                
                //Takes in user password for the encryption. Must be smaller 
                // than 256 bytes.
                do{
                    System.out.println("Enter password:");
                    password = scan.next();
                    if (password.getBytes().length > 256){
                        System.out.println("Password can not be more than 256 bytes.");
                    }
                }while(password.getBytes().length > 256);
                
                //Takes user password and converts it to byte array
                byte[] passwordArray = password.getBytes();
                
                //Passes newly created variables to appropriate method chosen by user.
                if (optionSelection == 6) {
                    encryptFile(byteArray, passwordArray, filename);
                } else {
                    decryptFile(byteArray, passwordArray, filename);
                }
            }catch (FileNotFoundException e){
                System.out.println("Your file was not found... Returning to menu.");
            }
        }else {
            System.out.println("A directory was not selected.");
        }
    }
    
    /** Takes two byte arrays and creates a new file with XOR encryption
     * @param byteArray The byte array conversion of the file previously 
     * specified by the user.
     * @param passwordArray The byte array conversion of the password previously
     * specified by the user.
     * @param filename Filename specified by user in previous steps**/
    public void encryptFile(byte[] byteArray, byte[] passwordArray, String filename) throws IOException{
            try {
                //seperate iterator to be used with passwordArray
                int j = 0; 
                
                for (int i = 0; i < byteArray.length; i++) {
                    
                    //If j gets to big, reset the value
                    if(j>passwordArray.length-1){
                        j=0;
                    }
                    //Changes file values to the XOR of the two arrays
                    byteArray[i] = (byte) (byteArray[i] ^ passwordArray[j]);
                    
                    //iterates seperated to ensure no errors
                    j++; 
                }
                //Creates new file
                FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"\\"+"ENC-"+filename);
                
                //Writes and closes newly created file with encrypted data
                try {
                    stream.write(byteArray);
                } finally {
                    stream.close();
                }             
            }
            catch (IOException e){
                System.out.println("Your file was not found... Returning to menu.");
            }    
    }
    
     /** Takes two byte arrays and decrypts a file encrypted with XOR encryption
     * @param byteArray The byte array conversion of the file previously 
     * specified by the user.
     * @param passwordArray The byte array conversion of the password previously
     * specified by the user.
     * @param filename Filename specified by user in previous steps**/
    public void decryptFile(byte[] byteArray, byte[] passwordArray, String filename) throws IOException{
            try {
                //seperate iterator to be used with passwordArray
                int j = 0; 
                for (int i = 0; i < byteArray.length; i++) {
                    
                    //If j gets to big, reset the value
                    if(j>passwordArray.length-1){
                        j=0;
                    }
                    
                    //Changes file values to the XOR of the two arrays
                    byteArray[i] = (byte) (passwordArray[j]^byteArray[i]);
                    //iterates seperated to ensure no errors
                    j++; 
                }
                //Takes specified file for decryption
                FileOutputStream stream = new FileOutputStream(file.getAbsolutePath()+"\\"+filename);
                
                //Writes the new data to the file
                try {
                    stream.write(byteArray);
                } finally {
                    stream.close();
                }             
            }
            catch (IOException e){
                System.out.println("Your file was not found... Returning to menu.");
            }    
    }
   
}

