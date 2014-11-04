package dom.usuarioshiro;

import org.apache.commons.codec.binary.Base64;

public class TestPass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] encodedBytes = Base64.encodeBase64("Test".getBytes());
		System.out.print("encodedBytes " + new String(encodedBytes));
		byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
		System.out.println("decodedBytes " + new String(decodedBytes));
	}

}
