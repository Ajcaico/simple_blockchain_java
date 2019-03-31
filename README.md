# simple_blockchain_java

This was for a homework assignment for Distributed Systems class at Carnegie Mellon.

Objective: Create a blockchain where a user can enter in a string value transaction that will be stored in a block and added to a blockchain. A RESTful API was created for the client to interact with the server where the blockchain resides. 

Method: A user (client) will interact with a console prompt to enter in a transaction (ex. Alice pays Bob $10). The client will send a HTTP request to the blockchain server to add create a block with this transaction and add it to the blockchain. A RESTful API was created with GET and PUT requests to interact with the server side blockchain. The scope of this project was limited to one server, so it does not include the distributed consensus protocol typically seen in a blockchain network. 
To create a block based on a transaction received, the server has to find a nonce (unique number) that when combined with the transaction text and other block details, hashes to specific value which is the proof of work(ex. hash starts with 5 leading zeros). Once this is achieved, then the block is created and added to the blockchain. Every block added to the chain has a hash pointer to the previous block. This verifies the validity of the blockchain. If any block in the chain is change or corrupted, the proof of work of the block will fail. 
The blockchain class implemented includes methods to addBlock, verifyBlockchain, and viewBlockchain. The block class had methods to calculateProofOfWork, getNonce, printToJSON. 
