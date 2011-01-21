package net.cattaka.hk.uki2win.json;

import java.util.ArrayList;
import java.util.List;

public class BlockedCharSequence implements CharSequence {
	private int blockSize;
	private List<char[]> charArrayList;
	private char[] lastCharArray;
	private int nextCharArrayPos;
	
	public BlockedCharSequence(int blockSize) {
		this.blockSize = blockSize;
		this.charArrayList = new ArrayList<char[]>();
		
		this.lastCharArray = new char[blockSize];
		this.nextCharArrayPos = 0;
		this.charArrayList.add(lastCharArray);
	}

	public void append(char c) {
		char[] lastCharArray = charArrayList.get(charArrayList.size() - 1);
		lastCharArray[nextCharArrayPos] = c;
		nextCharArrayPos++;
		if (nextCharArrayPos >= blockSize) {
			lastCharArray = new char[blockSize];
			nextCharArrayPos = 0;
			charArrayList.add(lastCharArray);
		}
	}

	public void append(char[] ca, int cstart, int cend) {
		int len = this.length();
		int start = len;
		int end = len + cend - cstart;
		int startBlockIndex = start / blockSize;
		int endBlockIndex = end / blockSize;
		int startIndex = start % blockSize;
		int endIndex = end % blockSize;
		
		if (startBlockIndex == endBlockIndex) {
			System.arraycopy(ca, cstart, charArrayList.get(startBlockIndex), startIndex, cend);
		} else {
			int bi = cstart;
			for (int i=startBlockIndex;i<= endBlockIndex;i++) {
				if (this.charArrayList.size() <= i) {
					lastCharArray = new char[blockSize];
					charArrayList.add(lastCharArray);
				}
				if (i == startBlockIndex) {
					System.arraycopy(ca, bi, charArrayList.get(i), startIndex, blockSize - startIndex);
					bi += blockSize - startIndex;
				} else if (i == endBlockIndex) {
					System.arraycopy(ca, bi, charArrayList.get(i), 0, endIndex);
					bi += endIndex;
				} else {
					System.arraycopy(ca, bi, charArrayList.get(i), 0, blockSize);
					bi += blockSize;
				}
			}
		}
		this.nextCharArrayPos = endIndex;
	}

	public void append(String str) {
		for (int i=0;i<str.length();i++) {
			this.append(str.charAt(i));
		}
	}

	public char charAt(int arg0) {
		int blockIndex = arg0 / blockSize;
		int index = arg0 % blockSize;
		if (blockIndex >= this.charArrayList.size()) {
			throw new IndexOutOfBoundsException(""+arg0);
		} else if (blockIndex+1 == this.charArrayList.size() && index >= nextCharArrayPos) {
			throw new IndexOutOfBoundsException(""+arg0);
		}
		
		return this.charArrayList.get(blockIndex)[index];
	}

	public int length() {
		return blockSize * charArrayList.size() - (blockSize - nextCharArrayPos);
	}

	public CharSequence subSequence(int start, int end) {
		return this.substring(start, end);
	}

	public String substring(int start) {
		return this.substring(start, this.length());
	}
	public String substring(int start, int end) {
		StringBuilder sb = new StringBuilder(end - start);
		int startBlockIndex = start / blockSize;
		int endBlockIndex = end / blockSize;
		int startIndex = start % blockSize;
		int endIndex = end % blockSize;
		
		if (startBlockIndex == endBlockIndex) {
			sb.append(charArrayList.get(startBlockIndex), startIndex, endIndex - startIndex);
		} else {
			for (int i=startBlockIndex;i<= endBlockIndex;i++) {
				if (i == startBlockIndex) {
					sb.append(charArrayList.get(i), startIndex, blockSize - startIndex);
				} else if (i == endBlockIndex) {
					sb.append(charArrayList.get(i), 0, endIndex);
				} else {
					sb.append(charArrayList.get(i));
				}
			}
		}
		
		return sb.toString();
	}
	
	public int indexOf(char c, int start) {
		int startBlockIndex = start / blockSize;
		int startIndex = start % blockSize;
		for (int i=startBlockIndex;i<this.charArrayList.size();i++) {
			char[] charArray = this.charArrayList.get(i);
			int j = (i == startBlockIndex) ? startIndex : 0;
			for (;j<blockSize;j++) {
				if (charArray[j] == c) {
					return i * blockSize + j;
				}
			}
		}
		
		return -1;
	}
	
	public int indexOf(String subString, int start) {
		int startBlockIndex = start / blockSize;
		int startIndex = start % blockSize;
		for (int i=startBlockIndex;i<this.charArrayList.size();i++) {
			char[] charArray = this.charArrayList.get(i);
			int j = (i == startBlockIndex) ? startIndex : 0;
			for (;j<blockSize;j++) {
				if (charArray[j] == subString.charAt(0)) {
					int startPos =  i * blockSize + j;
					boolean goflag = true;
					for (int k=1;k<subString.length();k++) {
						if (this.charAt(startPos + k) != subString.charAt(k)) {
							goflag = false;
							break;
						}
					}
					if (goflag) {
						return i * blockSize + j;
					}
				}
			}
		}
		
		return -1;
	}

	@Override
	public String toString() {
		return this.substring(0);
	}
}
