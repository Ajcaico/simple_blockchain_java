package Project3Task3Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 *
 * @author ajcai
 * Blockchain REST API that receives HTTP requests with GET and PUT methods
 * Performs operations on the blockchain from the request and returns response
 * 
 */
@WebServlet(name = "Project3Task3Server", urlPatterns = {"/*"})
public class BlockChainService extends HttpServlet{
    
    BlockChain bc = new BlockChain(); //instantiate new blockchain object
    
    //GET method to either view or verify the blockchain and return a response
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    
        System.out.println("Console: doGET visited");
        String result;
        
        // The name is on the path /name so skip over the '/'
        String message = (request.getPathInfo()).substring(1);
        
        // swtich statement based on message in the URL to view or verify 
        switch (message) {
            case "verify":
                {
                    response.setStatus(200); // set status to 200
                    result = String.valueOf(bc.isChainValid()); //perform blockchain validation
                    PrintWriter out = response.getWriter();
                    out.println(result); //write results
                    break;
                }
            case "view":
                {
                    response.setStatus(200); // set status to 200
                    result = String.valueOf(bc.toString()); //get blockchain view
                    PrintWriter out = response.getWriter();
                    out.println(result); //write results
                    break;
                }
            default:
                response.setStatus(400); //set status 400 bad request if not a view or verify value was entered
                break;
        }
    }

    //PUT method to modify the blockchain by adding a new block
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("Console: doPut visited");
                
        // Read what the client has placed in the PUT data area
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String putMessage = br.readLine();
        
        //parse JSON message received and populate variables
        JSONObject messageJSON = (JSONObject) JSONValue.parse(putMessage);
        String operationString = (String) messageJSON.get("operation");
        int operation = Integer.parseInt(operationString); //convert string to int
        String difficultyString = (String) messageJSON.get("difficulty");
        int difficulty = Integer.parseInt(difficultyString); //convert string to int
        String data = (String) messageJSON.get("data");
        
        //create new block and add to blockchain
        Block newBlock = new Block(bc.getChainSize(), new Timestamp(System.currentTimeMillis()), data, difficulty); //instantiate new block object
        newBlock.setPreviousHash(bc.chainHash); // set previous hash as the latest chain hash 
        bc.chainHash = newBlock.proofOfWork(); //caculate prrof of work and set hash as the new chain hash
        bc.addBlock(newBlock); //add new block to array list of blocks in the block chain
                
        response.setStatus(200); //return success status code              
    }
}

