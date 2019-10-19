package part2;

import java.util.ArrayList; 

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;



public class Main {
	
	public static ArrayList<Block> blockChain = new ArrayList<>();//[1,2,3,...]
	public static int difficulty =2;
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();//보내야할 코인에 트랜잭션
	
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;
	public static float minimumTransaction =0.1f; //최소한으로 할 이체할 금액
						
	public static void main(String[] args) {
	
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//1. 지갑 생성
		walletA = new Wallet();
		walletB = new Wallet();  
		Wallet coinbase = new Wallet(); //채굴이 일어났을때 코인을 주는 월렛 
		
		//2. 제너시스 트랜잭션 생성
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);//(보내는사람,받는사람,금액,최초에 있는 금액)
		genesisTransaction.generateSignature(coinbase.privateKey);//개인키 설정
		genesisTransaction.transactionId="0";//ID설정 
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));
		//TransactionOutput의 리스트를 생성
		
		//3. main save utxo
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		//UTXO에 TransactionOutput저장
		//4. genesis 생성
		Block genesisBlock = new Block("0"); //해시값 0인 genesisBlock 생성
		genesisBlock.addTransaction(genesisTransaction);
		addBlock(genesisBlock);
		System.out.println("");
				
		//5.다음 블록(Block1) 생성
		Block block1 = new Block(genesisBlock.hash);//앞의블록키를 가져온다.
		System.out.println("1. walletA.getBalance(): "+walletA.getBalance());
		System.out.println("2. block1 add 전 "+walletA.getBalance());
		
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));//(보내는곳,보내는금액)
		addBlock(block1);
		System.out.println("3. walletA.getBalance(): "+walletA.getBalance());
		System.out.println("4. walletB.getBalance(): "+walletB.getBalance());

	
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockChain.add(newBlock);
	}


}
