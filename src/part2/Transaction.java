package part2;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
	public String transactionId;
	public PublicKey sender; //보내는사람
	public PublicKey reciepient; //받는사람
	public float value; //보내는금액
	public byte[] signature; //서명
	
	public ArrayList<TransactionInput> inputs = new ArrayList<>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	public static int sequence =0;
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	public boolean processTransaction() {
		
		//1.verifySignature() //서명검증
		if(verifySignature()==false) {
			return false;  
		}
		
		//2. 메인함수에 임시 저장된 txoutId로 UTXO(사용되지 않은게 맞냐? 확인)
		for(TransactionInput i :inputs) {
			i.UTXO = Main.UTXOs.get(i.transactionOutputId);
		}//wallet의 TransactionInput으로 Main함수 UTXOs의 키를 가져온다. 
		
		//3. 최소단위 0.1f 를 넘는지 체크
		if(getInputValue() < Main.minimumTransaction) {
			return false;
		}
		
		//4. TxOutput 100 value: 40,60
		float leftOver = getInputValue()-value;
		
		transactionId = calculateHash();
		//40코인 송신
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		//나머지 60
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
		//(자기자신,남은금액,아이디)
		
		//5. output to Unspent list 
		for(TransactionOutput o : outputs) {
			Main.UTXOs.put(o.id, o);  //genesisTransaction UTXO를 outputs으로 옮겨짐
		}
		
		// 6. remove Txinput
		for(TransactionInput i : inputs) {
			if(i.UTXO == null)continue;
			Main.UTXOs.remove(i.UTXO.id);
		}//genesisTransaction UTXO를 지워지는 동시에 TransactionInput도 지워진다
	
		return true; //6개의 과정을 다 통과해야 거래가 됨
	}
	
	public String calculateHash() { //calculateHash함수 라이브러리를 사용하여 transactionId생성시킴
		sequence++;
		return BlockUtil.applySha256(BlockUtil.getStringFromKey(sender)+ 
				BlockUtil.getStringFromKey(reciepient)+
				Float.toString(value)+sequence);
	}
	
	public float getInputValue() {
		float total=0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	//서명을 만들것을 확인해준다 
	public boolean verifySignature() {
		String data = BlockUtil.getStringFromKey(sender)+BlockUtil.getStringFromKey(reciepient)+Float.toString(value);
		return BlockUtil.verifyECDSASig(sender, data, signature); //verifyECDSASig암호화된 함수로 검증해 준다
	}
	public void generateSignature(PrivateKey privateKey) {
		String data = BlockUtil.getStringFromKey(sender)+BlockUtil.getStringFromKey(reciepient)+Float.toString(value);
		signature = BlockUtil.applyECDSASig(privateKey, data);//보내는사람+받는사람+보내는금액과 개인키로 암호화시킨다 .
		
	}

}
