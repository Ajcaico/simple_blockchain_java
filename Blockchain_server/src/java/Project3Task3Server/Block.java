package Project3Task3Server;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 *
 * @author ajcai
 * This class represents a simple Block. 
 * Each Block object has an index - the position of the block on the chain. The first block (the so called Genesis block) has an index of 0. 
 * Each block has a timestamp - a Java Timestamp object, it holds the time of the block's creation.
 * Each block has a field named data - a String holding the block's single transaction details.
 * Each block has a String field named previousHash - the SHA256 hash of a block's parent. This is also called a hash pointer.
 * Each block holds a nonce - a BigInteger value determined by a proof of work routine. This has to be found by the proof of work logic. 
 * It has to be found so that this block has a hash of the proper difficulty. The difficulty is specified by a small integer representing the number of leading hex zeroes the hash must have.
 * Each block has a field named difficulty - it is an int that specifies the exact number of left most hex digits needed by a proper hash. 
 * The hash is represented in hexadecimal. If, for example, the difficulty is 3, the hash must have three leading hex 0's (or,1 and 1/2 bytes). Each hex digit represents 4 bits. 
 */
public class Block {
    
    private int index, difficulty;
    
    @SerializedName("time stamp ") //serialized name for printing JSON object
    private Timestamp timestamp;
    
    @SerializedName("PrevHash") //serialized name for printing JSON object
    private String previousHash;
    
    @SerializedName("Tx ") //serialized name for printing JSON object
    private String data;
    
    private BigInteger nonce;
    
    //Constructor Method
    Block(int index, Timestamp timestamp, String data, int difficulty) {
        setIndex(index);
        setTimestamp(new Timestamp(System.currentTimeMillis()));
        setData(data);
        setDifficulty(difficulty);
    }
    
    
    public String calculateHash(){
        
        //Create string of block's values to hash
        String stringToHash = String.valueOf(getIndex()) + getTimestamp().toString() + getData() + getPreviousHash() + getNonce().toString() + String.valueOf(getDifficulty());
        
        //Perform hexadecimal hash using SHA256
          try { 
            MessageDigest digest; // Create a SHA256 digest
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes; // allocate room for the result of the hash
            digest.update(stringToHash.getBytes("UTF-8"), 0, stringToHash.length()); // perform the hash
            hashBytes = digest.digest(); // collect result
            byte[] data = hashBytes;
        
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < data.length; i++) { 
                int halfbyte = (data[i] >>> 4) & 0x0F;
                int two_halfs = 0;
                do { 
                    if ((0 <= halfbyte) && (halfbyte <= 9)) 
                        buf.append((char) ('0' + halfbyte));
                    else 
                        buf.append((char) ('a' + (halfbyte - 10)));
                    halfbyte = data[i] & 0x0F;
                } while(two_halfs++ < 1);
            }            
            
            return buf.toString();

          }
        catch (NoSuchAlgorithmException nsa) {System.out.println("No such algorithm exception thrown " + nsa);}
        catch (UnsupportedEncodingException uee ) {System.out.println("Unsupported encoding exception thrown " + uee);}
                  
        return null; //return null if there is an exception
    }
    
    //This method returns the nonce for this block. The nonce is a number that has been found to cause the hash of this block to have the correct number of leading hexadecimal zeroes. 
    public BigInteger getNonce(){
        return nonce;
    }
    
    
    //The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
    //This method calls calculateHash() to compute a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty. 
    //If the hash has the appropriate number of leading hex zeroes, it is done and returns that proper hash. 
    //If the hash does not have the appropriate number of leading hex zeroes, it increments the nonce by 1 and tries again. 
    //It continues this process, burning electricity and CPU cycles, until it gets lucky and finds a good hash.
    public String proofOfWork(){
        
        nonce = BigInteger.ZERO; //initialize nonce to zero
        String hashValue = calculateHash(); //holds hashed value
        
        //create string of zeros based on difficulty to check proofOfWork
        String zeroString = "";
        for (int i = 0; i < getDifficulty(); i++){
            zeroString +="0";
        }
        
        //check if substring of hash value is all zeros, continues to increment nonce by 1 until leading values of hash match zero string
        while (!zeroString.equals(hashValue.substring(0, difficulty))){
            nonce = nonce.add(new BigInteger("1"));
            hashValue = calculateHash();
        }  
        return hashValue;
    }
    
    //Getter method for difficulty
    public int getDifficulty(){
        return difficulty;
    }
    
    //Setter method for difficulty
    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }
    
    //JSON string representation of all this block's data
    @Override
    public String toString(){
        
        //Use GSON to convert this block object to JSON representation
        Gson gson = new Gson();
        String blockJson = gson.toJson(this);
        return blockJson;
        
    }
    
    //Setter method for previous hash (pointer to hash of previous block)
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    //Getter method for previous hash
    public String getPreviousHash(){
        return previousHash;
    }
    
    //Getter method for index
    public int getIndex(){
        return index;
    }
    
    //Setter method for index
    public void setIndex(int index){
        this.index = index;
    }
    
    //Setter method for time stamp
    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }
    
    //Getter method for time stamp
    public Timestamp getTimestamp(){
        return timestamp;
    }
    
    //Getter method for data
    public String getData(){
        return data;
    }
    
    //Setter method for data
    public void setData(String data){
        this.data = data;
    }
    
    //main method, use for testing if needed
    public static void main(String[] args){
        
    }
}
