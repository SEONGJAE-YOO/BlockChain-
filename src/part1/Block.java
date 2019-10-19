package part1;

import java.util.Date;

public class Block {
	
	public String hash;
	public String previousHash; 
	private String data; 
	private long timeStamp; 
	private int nonce;
	
	
	public Block(String data,String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash(); 
	}
	
	  
	public String calculateHash() {
		String calculatedhash = BlockUtil.applySha256(   
				previousHash +
				Long.toString(timeStamp) + 
				Integer.toString(nonce) +  //nonce 형변환 integer->string
				data 
				); //BlockUtil클래스에서 applySha256함수에다 previousHash+timeStamp+nonce+data를 넣어줘서 해시값을 구한다.
		return calculatedhash;
	}
	
	//difficulty=2, 해쉬 앞자리가 00 즉 target=00이다 
	public void mineBlock(int difficulty) { //마이닝 기능 적용 시킴
		String target = BlockUtil.getDifficultyString(difficulty); 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++; //나올때까지 nonce 증가시킴
			hash = calculateHash();
		}//substring으로 문자열 추출//해쉬값이 앞자리 0부터 difficulty의 숫자만큼 target과 똑같은 때까지 계속 소스를 돌린다.
		System.out.println("Block Mined!!! : " + hash);
	}
	
}
