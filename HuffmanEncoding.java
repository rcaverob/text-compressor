package cs10;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Class to handle Compression and Decompression of Text files through Huffman Encoding
 *
 * @author Rodrigo A. Cavero Blades, Dartmouth CS 10, Winter 2018
 */

public class HuffmanEncoding {
	private Map<Character, Integer> freqTable;
	private PriorityQueue<BinaryTree<FreqChar>> pQueue;
	private BinaryTree<FreqChar> charTree;
	private HashMap<Character, String> codeMap;
	
	/**
	 * Compresses a text file pathName into a new file pathName_compressed
	 * @throws IOException 
	 */
	public void compress(String pathName) throws IOException  {
		BufferedReader input = null;
		BufferedReader input2 = null;
		BufferedBitWriter bitOut = null;
		try {
			input = new BufferedReader(new FileReader(pathName));
			generateFrequencies(input);
			input.close();
			generatePriorityQueue();
			generateTree();
			generateCodeMap();
			input2 = new BufferedReader(new FileReader(pathName));
			bitOut = new BufferedBitWriter(pathName.substring(0, pathName.length() - 4) +"_compressed.txt");
			for (int curInt = input2.read(); curInt > -1; curInt = input2.read()) {
				char curChar = (char) curInt;
				outputBits(curChar, bitOut);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input!= null) input.close();
			if(input2!= null) input2.close();
			if(bitOut!= null) bitOut.close();
		}
		
	}
	
	/**
	 * Decompresses a text file pathName into a new file pathName_decompressed
	 */
	public void deCompress(String pathName) throws IOException {
		BufferedBitReader bitInput = null;
		BufferedWriter output = null;
		try {
			bitInput = new BufferedBitReader(pathName);
			output = new BufferedWriter(new FileWriter(pathName.substring(0, pathName.length() - 14) +"decompressed.txt")); 
			if (charTree != null) {
				BinaryTree<FreqChar> current = charTree;
				while (bitInput.hasNext()) {
					boolean bit;
					if (current.isInner()) {
						bit = bitInput.readBit();
						if (bit) {current = current.getRight();
						}
						else {current = current.getLeft();
						}
					} else {
						if (charTree.size() == 1) bit = bitInput.readBit();
	
						output.write(current.getData().getCharacter());
						current = charTree;
					}
				}
				if (current != null && charTree.size() > 1) output.write(current.getData().getCharacter());
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bitInput.close();
			output.close();
		}
	}

	
	/**
	 * Generates and stores a Map keeping track of the frequencies of words given a BufferedReader input.
	 */
	public void generateFrequencies(BufferedReader input) throws IOException {
		freqTable = new HashMap<Character, Integer>();
		for (int curInt = input.read(); curInt > -1; curInt = input.read()) {
			char curChar = (char) curInt;
			if (!freqTable.containsKey(curChar)) freqTable.put(curChar, 1);
			else freqTable.put(curChar, freqTable.get(curChar) + 1);
		}
		input.close();
	}
	
	/**
	 * Generates and stores a PriorityQueue used for generation of the Huffman Tree of character frequencies.
	 */
	private void generatePriorityQueue() {
		pQueue = new PriorityQueue<BinaryTree<FreqChar>>( (BinaryTree<FreqChar> b1, BinaryTree<FreqChar> b2 ) 
				-> b1.getData().getFrequency() - b2.getData().getFrequency());
		
		for (Character c : freqTable.keySet()) {
			FreqChar fc = new FreqChar(c, freqTable.get(c));
			pQueue.add(new BinaryTree<FreqChar>(fc));
		}
	}
	
	/**
	 * Generates and stores a Huffman Tree based on the character frequencies on the stored frequency table.
	 */
	private void generateTree() {
		charTree = null;
		while (pQueue.size() > 1) {
			BinaryTree<FreqChar> b1 = pQueue.remove();
			BinaryTree<FreqChar> b2 = pQueue.remove();
			int rootFrequency = b1.getData().getFrequency() + b2.getData().getFrequency();
			BinaryTree<FreqChar> merged = new BinaryTree<FreqChar>(new FreqChar(rootFrequency), b1, b2);
			pQueue.add(merged);
		}
		if (!pQueue.isEmpty()) charTree = pQueue.remove();
	}
	
	
	/**
	 * Generates and stores a Map which keeps track of which code is assigned to which character.
	 */
	private void generateCodeMap() {
		codeMap = new HashMap<Character, String>();
		encodeTree(charTree, "");
	}

	
	/**
	 * Assigns each character within a given Huffman Tree representation a unique code.
	 */
	private void encodeTree(BinaryTree<FreqChar> tree, String code) {
		if (tree != null) {
			if (tree.isLeaf()) {
				if (code == "") code = "1";
				codeMap.put(tree.getData().getCharacter(), code);
			}
			else {
				if (tree.hasLeft()) encodeTree(tree.getLeft(), code + "0");
				if (tree.hasRight()) encodeTree(tree.getRight(), code + "1");
			}
		}
	}

	
	/**
	 * Outputs the code of a given character as bits to a file using a given BufferedBitWriter.
	 */
	private void outputBits(char curChar, BufferedBitWriter bitOut) throws IOException {
		String code = codeMap.get(curChar);
		for (int i = 0; i < code.length(); i ++) {
			char c = code.charAt(i);
			bitOut.writeBit(c == '1');
		}
			
		}
	
}
