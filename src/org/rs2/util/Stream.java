package org.rs2.util;

public class Stream {

	public byte[] inBuffer;
	public byte[] outBuffer;
	public int inOffset = 0;
	public int outOffset = 0;
	private int frameStackPtr = -1;
	private int frameStackSize = 10;
	private int frameStack[] = new int[frameStackSize];

	public static int bitMaskOut[] = new int[32];

	static {
		for (int i = 0; i < 32; i++) {
			bitMaskOut[i] = (1 << i) - 1;
		}
	}

	public int bitPosition = 0;

	/**
	 * Assigns sizes to the in and out buffers.
	 * @param in In buffer size.
	 * @param out Out buffer size.
	 */
	public Stream(int in, int out) {
		inBuffer = new byte[in];
		outBuffer = new byte[out];
	}

	public void addBit(int bit, int pos) {
		if (pos >= outBuffer.length) {
			expandOutBuffer();
		}
		outBuffer[pos] &= ~bit;
	}

	public void placeBit(int bit, int pos) {
		if (pos >= outBuffer.length) {
			expandOutBuffer();
		}
		outBuffer[pos] |= bit;
	}

	/**
	 * Creates a fixed-size frame.
	 * @param id Frame Id.
	 */
	public void createFrame(int id) {
		writeByte(id);
	}

	/**
	 * Creates a variable-size frame. Always call
	 * {@link Stream#endFrameVarSize(int)} when finished.
	 * @param id Framd Id.
	 */
	public void createFrameVarSize(int id) {
		writeByte(id);
		writeByte(0);

		if (frameStackPtr >= frameStackSize - 1) {
			throw new RuntimeException("Stack overflow");
		} else {
			frameStack[++frameStackPtr] = outOffset;
		}
	}

	public void createFrameVarSizeWord(int id) {
		writeByte(id);
		writeWord(0);

		if (frameStackPtr >= frameStackSize - 1) {
			System.out.println("Stack overflow");
		} else {
			frameStack[++frameStackPtr] = outOffset;
		}
	}

	public void endFrameVarSize() {
		if (frameStackPtr < 0) {
			System.out.println("Stack empty");
		} else {
			writeFrameSize(outOffset - frameStack[frameStackPtr--]);
		}
	}

	public void endFrameVarSizeWord() {
		if (frameStackPtr < 0) {
			System.out.println("Stack empty");
		} else {
			writeFrameSizeWord(outOffset - frameStack[frameStackPtr--]);
		}
	}

	/**
	 * Increase buffer size if it's too small.
	 */
	public void expandOutBuffer() {
		byte[] oldBuffer = outBuffer;

		outBuffer = new byte[oldBuffer.length + 1000];
		System.arraycopy(oldBuffer, 0, outBuffer, 0, oldBuffer.length);
	}

	public void finishBitAccess() {
		outOffset = (bitPosition + 7) / 8;
	}

	public void initBitAccess() {
		bitPosition = outOffset * 8;
	}

	public void readBytes(byte abyte0[], int i, int j) {
		for (int k = j; k < j + i; k++) {
			abyte0[k] = readSignedByte();
		}

	}

	public void readBytes_reverse(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--) {
			abyte0[k] = readSignedByte();
		}
	}

	public void readBytes_reverseA(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--) {
			abyte0[k] = readSignedByteA();
		}
	}

	/**
	 * Read a DWord (int) sent by the client.
	 */
	public int readDWord() {
		inOffset += 4;
		return ((inBuffer[inOffset - 4] & 0xff) << 24) + ((inBuffer[inOffset - 3] & 0xff) << 16)
			+ ((inBuffer[inOffset - 2] & 0xff) << 8) + (inBuffer[inOffset - 1] & 0xff);
	}

	public int readDWord_v1() {
		inOffset += 4;
		return ((inBuffer[inOffset - 2] & 0xff) << 24) + ((inBuffer[inOffset - 1] & 0xff) << 16)
			+ ((inBuffer[inOffset - 4] & 0xff) << 8) + (inBuffer[inOffset - 3] & 0xff);
	}

	public int readDWord_v2() {
		inOffset += 4;
		return ((inBuffer[inOffset - 3] & 0xff) << 24) + ((inBuffer[inOffset - 4] & 0xff) << 16)
			+ ((inBuffer[inOffset - 1] & 0xff) << 8) + (inBuffer[inOffset - 2] & 0xff);
	}

	/**
	 * Read a QWord (long) sent by the client.
	 */
	public long readQWord() {
		long l = (long) readDWord() & 0xffffffffL;
		long l1 = (long) readDWord() & 0xffffffffL;
		return (l << 32) + l1;
	}

	/**
	 * Reads a signed byte sent by the client.
	 */
	public byte readSignedByte() {
		return inBuffer[inOffset++];
	}

	/**
	 * Reads a signed byte Special A sent by the client.
	 */
	public byte readSignedByteA() {
		return (byte) (inBuffer[inOffset++] - 128);
	}

	/**
	 * Reads a signed byte Special C sent by the client.
	 */
	public byte readSignedByteC() {
		return (byte) (-inBuffer[inOffset++]);
	}

	/**
	 * Reads a signed byte Special S sent by the client.
	 */
	public byte readSignedByteS() {
		return (byte) (128 - inBuffer[inOffset++]);
	}

	/**
	 * Reads a signed word sent by the client.
	 */
	public int readSignedWord() {
		inOffset += 2;
		int i = ((inBuffer[inOffset - 2] & 0xff) << 8) + (inBuffer[inOffset - 1] & 0xff);

		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int readSignedWordA() {
		inOffset += 2;
		int i = ((inBuffer[inOffset - 2] & 0xff) << 8) + (inBuffer[inOffset - 1] - 128 & 0xff);

		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int readSignedWordBigEndian() {
		inOffset += 2;
		int i = ((inBuffer[inOffset - 1] & 0xff) << 8) + (inBuffer[inOffset - 2] & 0xff);

		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int readSignedWordBigEndianA() {
		inOffset += 2;
		int i = ((inBuffer[inOffset - 1] & 0xff) << 8) + (inBuffer[inOffset - 2] - 128 & 0xff);

		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public String readString() {
		int i = inOffset;
		while (inBuffer[inOffset++] != 10) {
			;
		}
		return new String(inBuffer, i, inOffset - i - 1);
	}

	/**
	 * Reads an unsigned byte sent by the client.
	 */
	public int readUnsignedByte() {
		return inBuffer[inOffset++] & 0xff;
	}

	/**
	 * Reads an unsigned byte Special A sent by the client.
	 */
	public int readUnsignedByteA() {
		return inBuffer[inOffset++] - 128 & 0xff;
	}

	/**
	 * Reads an unsigned byte Special C sent by the client.
	 */
	public int readUnsignedByteC() {
		return -inBuffer[inOffset++] & 0xff;
	}

	/**
	 * Reads an unsigned byte Special S sent by the client.
	 */
	public int readUnsignedByteS() {
		return 128 - inBuffer[inOffset++] & 0xff;
	}

	public int readUnsignedWord() {
		inOffset += 2;
		return ((inBuffer[inOffset - 2] & 0xff) << 8) + (inBuffer[inOffset - 1] & 0xff);
	}

	public int readUnsignedWordA() {
		inOffset += 2;
		return ((inBuffer[inOffset - 2] & 0xff) << 8) + (inBuffer[inOffset - 1] - 128 & 0xff);
	}

	public int readUnsignedWordBigEndian() {
		inOffset += 2;
		return ((inBuffer[inOffset - 1] & 0xff) << 8) + (inBuffer[inOffset - 2] & 0xff);
	}

	public int readUnsignedWordBigEndianA() {
		inOffset += 2;
		return ((inBuffer[inOffset - 1] & 0xff) << 8) + (inBuffer[inOffset - 2] - 128 & 0xff);
	}

	public void write3Byte(int i) {
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeBits(int numBits, int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);

		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			addBit(bitMaskOut[bitOffset], bytePos);
			placeBit(((value >> (numBits - bitOffset)) & bitMaskOut[bitOffset]), bytePos++);
			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			addBit(bitMaskOut[bitOffset], bytePos);
			placeBit((value & bitMaskOut[bitOffset]), bytePos);
		} else {
			addBit((bitMaskOut[numBits] << (bitOffset - numBits)), bytePos);
			placeBit((value & bitMaskOut[numBits]) << (bitOffset - numBits), bytePos);
		}
	}

	public void writeByte(int i) {
		if (outOffset >= outBuffer.length) {
			expandOutBuffer();
		}
		outBuffer[outOffset++] = (byte) i;
	}

	public void writeByte(int i, int position) {
		if (position >= outBuffer.length) {
			expandOutBuffer();
		}
		outBuffer[position] = (byte) i;
	}

	public void writeByteA(int i) {
		writeByte(i + 128);
	}

	public void writeByteC(int i) {
		writeByte(-i);
	}

	public void writeBytes(byte abyte0[], int i, int j) {
		for (int k = j; k < j + i; k++) {
			writeByte(abyte0[k]);
		}
	}

	public void writeByteS(int i) {
		writeByte(128 - i);
	}

	public void writeBytes_reverse(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--) {
			writeByte(abyte0[k]);
		}
	}

	public void writeBytes_reverseA(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--) {
			writeByte(abyte0[k] + 128);
		}
	}

	public void writeDWord(int i) {
		writeByte(i >> 24);
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);

	}

	public void writeDWord_v1(int i) {
		writeByte(i >> 8);
		writeByte(i);
		writeByte(i >> 24);
		writeByte(i >> 16);
	}

	public void writeDWord_v2(int i) {
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeDWordBigEndian(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);
		writeByte(i >> 24);
	}

	public void writeFrameSize(int i) {
		writeByte(i, (outOffset - i - 1));
	}

	public void writeFrameSizeWord(int i) {
		writeByte(i >> 8, (outOffset - i - 2));
		writeByte(i, (outOffset - i - 1));
	}

	public void writeQWord(long l) {
		writeByte((int) (l >> 56));
		writeByte((int) (l >> 48));
		writeByte((int) (l >> 40));
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);
	}

	public void writeString(String s) {
		/*
		 * for (int i = 0; i < s.length(); i++) { outBuffer[outOffset++] =
		 * (byte) s.charAt(i); } outBuffer[outOffset++] = 10;
		 */

		byte[] stringBytes = s.getBytes();

		for (int i = 0; i < s.length(); i++) {
			writeByte(stringBytes[i]);
		}
		writeByte(10);
	}

	public void writeWord(int i) {
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeWordA(int i) {
		writeByte(i >> 8);
		writeByte(i + 128);
	}

	public void writeWordBigEndian(int i) {
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeWordBigEndian_dup(int i) {
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeWordBigEndianA(int i) {
		writeByte(i + 128);
		writeByte(i >> 8);
	}

	/**
	 * Writes a word if the value is >= 128, and a byte otherwise.
	 * @param i
	 */
	public void writeSmarts(int i) {
		if (i >= 128) {
			writeWord(i + 32768);
		} else {
			writeByte(i);
		}
	}

}
