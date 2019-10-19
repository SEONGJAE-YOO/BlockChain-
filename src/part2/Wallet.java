package part2;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {        
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	  
	public HashMap<String,TransactionOutput> UTXO_Wallet = new HashMap<String,TransactionOutput>();
	//<아이디,TransactionOutput>
	public Wallet() {
		generateKeyPair();
	}
	// 잔고 확인 기능
	public float getBalance() {
		float total = 0;	
		//main함수안에 UTXO를 끄집어 낸다.
        for (Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();//UTXO의 잔액을 가져온다.
            if(UTXO.isMine(publicKey)) { 
            	UTXO_Wallet.put(UTXO.id,UTXO); //지갑에서 관리하는 UTXO_Wallet이 있다.//TransactionOutput 키와 값을 넣어준다
            	total += UTXO.value ; //UTXO의 잔액을 total에 넣어줘서 잔고를 뽑아낸다.
            }//UTXO의 publicKey는  WalletA의  publicKey와 같다 
        }  
		return total;
	}
	// 송신기능 //(보내는사람,보내는금액)
	public Transaction sendFunds(PublicKey _recipient,float value ) {
		if(getBalance() < value) { //잔액이 보내는 금액보다 작으면 즉 보내는 금액이 잔액보다 크면 
			System.out.println("Not Enough~~!");
			return null; //에러가 나게 null값 보내줌 
		}
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();//TransactionInput생성
		// 
		float total = 0;
		//UTXO_Wallet에서 가져온다
		for (Map.Entry<String, TransactionOutput> item: UTXO_Wallet.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));//TransactionInput에 UTXO.id 넣어줌 즉 TransactionOutputid를 넣어줌
			                                           //Transactionoutputid는 TransactionInput아이디와 같다.
			if(total > value) break;
		}
		//(보내는사람,받는사람,보내는금액,ArrayList<TransactionInput>)
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);//서명인증인 개인키를 만들어준다
		
		for(TransactionInput input: inputs){  //남아있는 잔액을 없애준다
			UTXO_Wallet.remove(input.transactionOutputId);
		}
		
		return newTransaction;
	}
	
	public void generateKeyPair() {//랜덤으로 개인키와 공개키를 만들어준다 
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
	
			keyGen.initialize(ecSpec, random); 
	        KeyPair keyPair = keyGen.generateKeyPair();
	
	        privateKey = keyPair.getPrivate();
	        publicKey = keyPair.getPublic();
	        
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}


