package cs10;
import java.io.IOException;

public class HuffmanDriver {

	public static void main(String[] args) throws IOException {
		HuffmanEncoding h = new HuffmanEncoding();

		h.compress("files/Hello_World.txt");
		h.deCompress("files/Hello_World_compressed.txt");
		
		h.compress("files/empty.txt");
		h.deCompress("files/empty_compressed.txt");
		
		h.compress("files/repeated_character.txt");
		h.deCompress("files/repeated_character_compressed.txt");
		
		h.compress("files/USConstitution.txt");
		h.deCompress("files/USConstitution_compressed.txt");
		
		h.compress("files/WarAndPeace.txt");
		h.deCompress("files/WarAndPeace_compressed.txt");
		
		h.compress("files/single_character.txt");
		h.deCompress("files/single_character_compressed.txt");
	}

}
