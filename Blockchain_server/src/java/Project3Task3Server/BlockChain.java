package Project3Task3Server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ajcai
 * This class represents a simple block chain
 * It will hold an array list of blocks each time a new transaction is added to the chain
 */

public class BlockChain {
    ArrayList<Block> blocks; //Array list to hold blocks
    String chainHash; // hold a SHA256 hash of the most recently added block
    
    //Constructor method 
    public BlockChain(){
        blocks = new ArrayList<>();
        chainHash = "";
        
        //create initial genesis block
        Block genesis = new Block(0, new Timestamp(System.currentTimeMillis()), "Genesis", 2);
        genesis.setPreviousHash("");
        chainHash = genesis.proofOfWork();
        addBlock(genesis);
        
    }
    
    //Get current system time
    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }
    
    //returns the last block added to the chain
    public Block getLatestBlock(){
        return blocks.get(getChainSize()-1); //minus 1 to convert size to last index
    }
    
    //Get size of chain based on number of blocks
    public int getChainSize(){
        return blocks.size();
    }
    
    //hashes per second of the computer holding this chain. It uses a simple string - "00000000" to hash.
    public int hashesPerSecond(){
        String text = "00000000";
        long startTime = System.currentTimeMillis();
        int hashesPerSecond = 0;
        while (System.currentTimeMillis() - startTime < 1000){
        
        //Perform hexadecimal hash using SHA256
          try { 
            MessageDigest digest; // Create a SHA256 digest
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes; // allocate room for the result of the hash
            digest.update(text.getBytes("UTF-8"), 0, text.length()); // perform the hash
            hashBytes = digest.digest(); // collect result
            byte[] data = hashBytes;
            //converts to hex
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
        String hash = buf.toString(); //holds hashed value
        hashesPerSecond++; //increment every hash performed
          }
        catch (NoSuchAlgorithmException nsa) {System.out.println("No such algorithm exception thrown " + nsa);}
        catch (UnsupportedEncodingException uee ) {System.out.println("Unsupported encoding exception thrown " + uee);}
        }
        return hashesPerSecond;
    }
    
    //A new Block is being added to the BlockChain. This new block's previous hash must hold the hash of the most recently added block. 
    //After this call on addBlock, the new block becomes the most recently added block on the BlockChain. 
    //The SHA256 hash of every block must exhibit proof of work, i.e., have the requisite number of leftmost 0's defined by its difficulty.
    //Suppose our new block is x. And suppose the old blockchain was a <-- b <-- c <-- d then the chain after addBlock completes is a <-- b <-- c <-- d <-- x. 
    //Within the block x, there is a previous hash field. This previous hash field holds the hash of the block d. The block d is called the parent of x. The block x is the child of the block d. 
    //It is important to also maintain a hash of the most recently added block in a chain hash.
    public void addBlock(Block newBlock){
        blocks.add(newBlock); //add new block to array list of blocks in the blockhain
    }
    
    //Creates s JSON string representation of the entire block chain
    //It calls the toString() method of each block in the chain
    public String toString(){
        
        //create beginning of JSON text for ds chain
        String blockChainJson = "{\"ds_chain\" :[";
        
        //add JSON of each block to blockchain JSON
        for (Block block : blocks){
            blockChainJson += block.toString() + ",";
        }
        
        blockChainJson = blockChainJson.substring(0, blockChainJson.length()-1); //Removes extra comma at the end of the string
        
        //add ending and chain hash to blockchain JSON
        blockChainJson += "],\"chainHash\":\"" + chainHash + "\"}";
        
        return blockChainJson;
    }
    
    //If the chain only contains one block, the genesis block at position 0, this routine computes the hash of the block and checks that the hash has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field. 
    //It also checks that the chain hash is equal to this computed hash. If either check fails, return false. Otherwise, return true. 
    //If the chain has more blocks than one, begin checking from block one. Continue checking until you have validated the entire chain. 
    //The first check will involve a computation of a hash in Block 0 and a comparison with the hash pointer in Block 1. If they match and if the proof of work is correct, go and visit the next block in the chain.
    //At the end, check that the chain hash is also correct.
    public boolean isChainValid(){
        
        //loop through and verify each block in list
        for (Block block: blocks){ 
        
        //create string to hash based on the block's values
        String stringToHash = String.valueOf(block.getIndex()) + block.getTimestamp().toString() + block.getData()
                + block.getPreviousHash() + block.getNonce().toString() + String.valueOf(block.getDifficulty());
        
        //Perform hexadecimal hash using SHA256
          try { 
            MessageDigest digest; // Create a SHA256 digest
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes; // allocate room for the result of the hash
            digest.update(stringToHash.getBytes("UTF-8"), 0, stringToHash.length()); // perform the hash
            hashBytes = digest.digest(); // collect result
            byte[] data = hashBytes;
        
            StringBuilder buf = new StringBuilder(); //Create hex hash
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
            
            String blockHash = buf.toString(); //holds hashed value
           
            //create string of zeros based on difficulty to check proofOfWork
            String zeroString = "";
            for (int i = 0; i < block.getDifficulty(); i++){
                zeroString +="0";
            }
            
            //Veify hash value with difficulty
            //Take leading requisite numbers of hash based on difficulty and check if it's all 0s   
            if (!zeroString.equals(blockHash.substring(0, block.getDifficulty()))){
                System.out.println("Improper hash on node " + block.getIndex() + ", does not begin with " + zeroString);
                return false; // return false if verification fails
            }

            //Check if this block is last block in the chain
            //Check if the chain hash matches the last block in the chain
            if(block.getIndex() + 1 == blocks.size()){ 
                if (!chainHash.equals(blockHash)) {
                    System.out.println("Improper hash on node " + block.getIndex() + ", current block hash does not equal chain hash");
                    return false;
                } // return false if chain hash is not the same as latest block hash 
            }
            
            //If first two verifications pass and blockchain only has 1 block, return true
            if (blocks.size() == 1)return true;

            //If chain size > 1, check hashpointers of blocks
            else {
               //if not last block in chain, check if current hash is hash pointer in the next block
               if (block.getIndex()+ 1 != blocks.size()){ 
                   //if current hash is not equal to hash pointer of next block return false                        
                   if (!blockHash.equals(blocks.get(block.getIndex()+1).getPreviousHash())){
                       System.out.println("Improper hash on node " + block.getIndex() + ", current block hash does not equal pointer on next block");
                       return false;
                   }
               }
               
               //if block is last block in chain, check if it's hash matches the chain hash
               else{
                   if (!chainHash.equals(blockHash)) {
                       System.out.println("Improper hash on node " + block.getIndex() + ", current block hash does not equal chain hash");
                       return false; // return false if chain hash is not the same as latest block hash 
                   } 
               }
            }
          }
        catch (NoSuchAlgorithmException nsa) {System.out.println("No such algorithm exception thrown " + nsa);}
        catch (UnsupportedEncodingException uee ) {System.out.println("Unsupported encoding exception thrown " + uee);}       
        }
        
        //if this point is reached, none of the verifications failed for all blocks so return true
        return true;
    }
    
    //This method repairs the chain. It checks the hashes of each block and ensures that any illegal hashes are recomputed. 
    //After this routine is run, the chain will be valid. The routine does not modify any difficulty values. It computes new proof of work based on the difficulty specified in the Block.
    public void repairChain(){
     
        //check each block if it is invalid, recompute proof of work if invalid 
        for (Block block : blocks){
            
            boolean invalidBlock = false; // flag for invalid block
            
            //create sting to hash based on block's values
            String stringToHash = String.valueOf(block.getIndex()) + block.getTimestamp().toString() + block.getData()
                + block.getPreviousHash() + block.getNonce().toString() + String.valueOf(block.getDifficulty());
        
        
        //Perform hexadecimal hash using SHA256
          try { 
            MessageDigest digest; // Create a SHA256 digest
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes; // allocate room for the result of the hash
            digest.update(stringToHash.getBytes("UTF-8"), 0, stringToHash.length()); // perform the hash
            hashBytes = digest.digest(); // collect result
            byte[] data = hashBytes;
        
            StringBuilder buf = new StringBuilder(); //Create hex hash
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
            
            String blockHash = buf.toString(); //holds hashed value
            
            //create string of zeros based on difficulty to check proofOfWork
            String zeroString = "";
            for (int i = 0; i < block.getDifficulty(); i++){
                zeroString +="0";
            }
            
            //Veify hash value with difficulty
            //Take leading requisite numbers of hash based on difficulty and check if it's all 0s   
            if (!zeroString.equals(blockHash.substring(0, block.getDifficulty()))){
                invalidBlock = true; //set flag to true if verification fails
            }

            //Check if this block is last block in the chain
            //Check if the chain hash matches the last block in the chain
            if(block.getIndex() + 1 == blocks.size()){ 
                if (!chainHash.equals(blockHash)) {
                    invalidBlock = true; //set flag to true if verification fails
                } 
            }
            
               if (block.getIndex()+ 1 != blocks.size()){ 
                   //if current hash is not equal to hash pointer of next block set flag to true
                   if (!blockHash.equals(blocks.get(block.getIndex()+1).getPreviousHash())){
                       invalidBlock = true;
                   }
               }
               //if block is last block in chain, check if it's hash matches the chain hash
               else{
                   if (!chainHash.equals(blockHash)) {
                       invalidBlock = true; //set flag to true if verification fails
                   } 
               }
            
               
            //If block failed any verifications, recompute proof of work and set pointer of next block to the recomputed hash
            if (invalidBlock){
                String proofOfWork = block.proofOfWork();
                
                //if not last block in chain, set the pointer of the next block to recomputed hash
                if(block.getIndex()+1 != blocks.size()) blocks.get(block.getIndex()+1).setPreviousHash(proofOfWork);
                
                //If this is last block in the chain, set chainHash to the recomputed hash of the last block
                else chainHash = proofOfWork;
            }  
        }
        catch (NoSuchAlgorithmException nsa) {System.out.println("No such algorithm exception thrown " + nsa);}
        catch (UnsupportedEncodingException uee ) {System.out.println("Unsupported encoding exception thrown " + uee);}       

        }
    }
    
    //This routine acts as a test driver for your Blockchain. It will begin by creating a BlockChain object and then adding the Genesis block to the chain. 
    //The Genesis block will be created with an empty string as the pervious hash and a difficulty of 2. 
    //All blocks added to the Blockchain will have a difficulty passed in to the program by the user at run time. 
    //All hashes will have the proper number of zero hex digits representing the most significant nibbles in the hash. 
    //If the difficulty is specified as three, then all hashes will begin with 3 or more zero hex digits (or 3 nibbles, or 12 zero bits). 
    //It is menu driven and will continously provide the user with seven options.
    public static void main(String[] args){
        BlockChain bc = new BlockChain();//instantiate the block chain
        
        //create initial genesis block
        Block genesis = new Block(0, new Timestamp(System.currentTimeMillis()), "Gensis", 2);
        genesis.setPreviousHash("");
        bc.chainHash = genesis.proofOfWork();
        bc.addBlock(genesis);
        
        //Recursive Block chain menu until option 6 (exit) is chosen
        int option = 0;
        while(option != 6){
        System.out.println("\nBlock Chain Menu\n"
                + "0. View basic blockchain status\n"
                + "1. Add a transaction to the blockchain.\n"
                + "2. Verify the blockchain.\n"
                + "3. View the blockchain.\n"
                + "4. Corrupt the chain.\n"
                + "5. Hide the corruption by repairing the chain.\n"
                + "6. Exit.");
        
        //Get user input
        Scanner input = new Scanner(System.in);
        option = input.nextInt();
        input.nextLine();
        
        //View blockchain status
        if (option == 0){
            System.out.println("Current Size of Chain: " + String.valueOf(bc.getChainSize()));
            System.out.println("Current Hashes per Second: " + String.valueOf(bc.hashesPerSecond()));
            System.out.println("Difficulty of most recent block: " + String.valueOf(bc.blocks.get((bc.blocks.size())-1).getDifficulty()));
            System.out.println("Nonce for most recent block: " + String.valueOf(bc.blocks.get((bc.blocks.size())-1).getNonce()));
            System.out.println("Chain Hash: " + bc.chainHash);
        }
        
        //Add a transaction to the blockchain
        //Average time to add a block of difficulty 4 is 239 milliseconds
        //Average time to add a block of difficulty 5 is 5134 milliseconds
        if (option == 1){
            
            System.out.println("Enter Difficulty > 0");
            int difficulty = input.nextInt();
            input.nextLine();
            
            System.out.println("Enter transaction:");
            String message = input.nextLine();
            
            long startTime = System.currentTimeMillis();
            
            Block newBlock = new Block(bc.getChainSize(), new Timestamp(System.currentTimeMillis()), message, difficulty); //instantiate new block object
            newBlock.setPreviousHash(bc.chainHash); // set previous hash as the latest chain hash 
            bc.chainHash = newBlock.proofOfWork(); //caculate prrof of work and set hash as the new chain hash
            bc.addBlock(newBlock); //add new block to array list of blocks in the block chain
            
            long endTime = System.currentTimeMillis(); // get current time
            long duration = endTime - startTime; // calculate duration it took to add a new block 
            System.out.println("Total execution time to add this block was " + String.valueOf(duration) + " milliseconds");
        }
        
        //Verify the blockchain
        //Average time to verify a chain with 5 blocks of difficulty 4 is 1 millisecond
        //Average time to verify a chain with 5 blocks of difficulty 5 is 1 millisecond
        if (option == 2){
            System.out.println("Verifying Entire Chain");
            long startTime = System.currentTimeMillis();
            System.out.println("Chain Verification: " + bc.isChainValid());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Total execution time required to verify the chain was " + duration + " milliseconds");
        }
     
        //View the blockchain
        if (option == 3){
            System.out.println("View the Blockchain");
            System.out.println(bc.toString());
        }
        
        //corrupt the chain by changing the transaction in a block
        if (option == 4){
            
            System.out.println("Corrupt the Blockchain");
            System.out.println("Enter Block ID of block to corrupt");
            int blockToCorrupt = input.nextInt();
            input.nextLine();
            
            //select which block to corrupt
            System.out.println("Enter new data for block " + blockToCorrupt);
            String newData = input.nextLine();
            
            bc.blocks.get(blockToCorrupt).setData(newData); //change the data in a block
            
            System.out.println("Block " + blockToCorrupt + " now holds " + newData);
        }
        
        
        //Hide corruption by repairing the chain
        //Average time to repair a corrupted blockchain with 5 blocks of difficulty 4 is 240 milliseconds
        //Average time to repair a corrupted blockchain with 5 blocks of difficulty 5 is 4323 milliseconds
        if (option == 5){
            
            System.out.println("Repairing the entire chain");
            long startTime = System.currentTimeMillis();
            
            bc.repairChain();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Total execution time required to repair the chain was " + duration + " milliseconds");
            
        }
        
        //Exit
        if (option == 6){
            break;
        }
        }
    }
}
