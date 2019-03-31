package project3task3client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 *
 * @author ajcai
 * Client that gets user inputs from the blockchain menu
 * Client makes REST HTTP requests to the blockchain API to interact with the server side blockchain
 * doGet method used to verify or view the blockchain
 * doPut method used to modify the blockchain and add new blcoks
 */

//Class to hold the result from the HTTP request
class Result {
    String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}

//Client class to ask for user input and communicate with API
public class Project3Task3Client {

    public static void main(String[] args) {
        
        //Recursive Block chain menu until option 4 (exit) is chosen
        int option = 0;
        while(option != 4){
            System.out.println("\nBlock Chain Menu\n"
                    + "1. Add a transaction to the blockchain.\n"
                    + "2. Verify the blockchain.\n"
                    + "3. View the blockchain.\n"
                    + "4. Exit.");

            //Get user input
            Scanner input = new Scanner(System.in);
            option = input.nextInt();
            input.nextLine(); 

            int difficulty = 0;
            String data = "";

            //Get user input if selected to add new block
            if(option == 1) {
                System.out.println("Enter Difficulty > 0");
                difficulty = input.nextInt();
                input.nextLine();

                System.out.println("Enter transaction:");
                data = input.nextLine();
            }


            //switch statement based on option chosen to print correct information
            switch(option){
                case 1:
                    //Create JSON object based on inputs
                    JSONObject messageJSON = new JSONObject();
                    messageJSON.put("operation", String.valueOf(option));
                    messageJSON.put("difficulty", String.valueOf(difficulty));
                    messageJSON.put("data", data);
            
                    long startTime = System.currentTimeMillis();
                    doPut(messageJSON.toString()); //calls doPut method to send request to API
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    System.out.println("Total execution time to add this block was " + String.valueOf(duration) + " milliseconds");
                    break;
                case 2:
                    startTime = System.currentTimeMillis();
                    String response = retrieve("verify"); //calls retrieve method to send request to API
                    System.out.println("Verifying Entire Chain");
                    System.out.println("Chain Verification: " + response);
                    endTime = System.currentTimeMillis();
                    duration = endTime - startTime;
                    System.out.println("Total execution time required to verify the chain was " + duration + " milliseconds");
                    break;
                case 3:
                    response = retrieve("view"); //calls doPut method to send request to API
                    System.out.println("View the Blockchain");
                    System.out.println(response); 
                    break;
                default:
                    break;
            }
        }
    }
    
    // retrieves a response from the server through the doGet method
    // return either the value read or an error message
    public static String retrieve(String message) {
        Result r = new Result();
        int status;
        if((status = doGet(message,r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }
    
    //doGet method used to either verify or view the blockchain through HTTP request
    public static int doGet(String message, Result r){
        
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;
         
         try {  
                // pass the message on the URL line
		URL url = new URL("http://localhost:8090/Project3Task3Server/" + "//"+message);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
                // tell the server what format we want back
		conn.setRequestProperty("Accept", "text/plain");
 	
                // wait for response
                status = conn.getResponseCode();
                
                // If things went poorly, don't try to read any response, just return.
		if (status != 200) {
                    // not using msg
                    String msg = conn.getResponseMessage();
                    return conn.getResponseCode();
                }
                String output = "";
                // things went well so return the response
                BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 		
		while ((output = br.readLine()) != null) {
			response += output;
         
		}
		conn.disconnect();
 
	    } 
                catch (MalformedURLException e) {e.printStackTrace();}
	        catch (IOException e) {e.printStackTrace();}
            
         // return value from server 
         // set the response object
         r.setValue(response);
         // return HTTP status to caller
         return status;
        
    }
    
    //doPut request to modify the blockchain by adding a new block through a HTTP request
    public static int doPut(String message){
            
         int status = 0;
         try {  
		URL url = new URL("http://localhost:8090/Project3Task3Server/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("PUT");
		conn.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(message); //sends JSON of details to create new block
                out.close();
		status = conn.getResponseCode(); // get status
                conn.disconnect();
	     } 
         
        catch (MalformedURLException e) {e.printStackTrace();} 
        catch (IOException e) {e.printStackTrace();}
         
        return status; // returns status to caller
    }
}
