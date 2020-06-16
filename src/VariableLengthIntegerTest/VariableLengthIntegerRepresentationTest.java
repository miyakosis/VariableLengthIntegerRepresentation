package VariableLengthIntegerTest;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import VariableLengthInteger.VariableLengthIntegerRepresentation;

class VariableLengthIntegerRepresentationTest
{
	@Test
	void test_toSignedRepresentaion_long()
	{
		byte[]	ba;
		long	n;
		int	len;

		// 0(0000 0000) <-> [0000 0000]
		n = 0;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 63(0011 1111) <-> [0011 1111]
		n = 63;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 64(0100 0000) <-> [1000 0000, 0100 0000]
		n = 64;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 127(0111 1111) <-> [1000 0000, 0111 1111]
		n = 127;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 128(1000 0000) <-> [1000 0001, 0000 0000]
		n = 128;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("81-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 8191(0001 1111, 1111 1111) <-> [1011 1111, 0111 1111]
		n = 8191;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("bf-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 8192(0010 0000, 0000 0000) <-> [1000 0000, 1100 0000, 0000 0000]
		n = 8192;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("80-c0-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 1048575(0000 1111, 1111 1111, 1111 1111) <-> [1011 1111, 1111 1111, 0111 1111]
		n = 1048575;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("bf-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 1048576(0001 0000, 0000 0000, 0000 0000) <-> [1000 0000, 1100 0000, 1000 0000, 0000 0000]
		n = 1048576;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(4, len);
		assertByteArray("80-c0-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 2147483647(0x7FFFFFFF) <-> [1000 0111, 1111 1111, 1111 1111, 1111 1111, 0111 1111]
		// Integer.MAX_VALUE
		n = 2147483647;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("87-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 2147483648(0x80000000) <-> [1000 1000, 1000 0000, 1000 0000, 1000 0000, 0000 0000]
		// Integer.MAX_VALUE + 1
		n = 2147483648L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("88-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 9223372036854775807(0x7FFF FFFF FFFF FFFF) <-> [1000 0000, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 0111 1111]
		// Long.MAX_VALUE
		n = 9223372036854775807L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("80-ff-ff-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 7バイト目のデータが 0x01, 8バイト目が 0x00, それ以外のバイトおよび9バイト目が 0xff となるデータ(先頭バイトは符号拡張バイトのため 0x00)
		n = 9223372036852695167L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("80-ff-ff-ff-ff-ff-ff-81-80-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// 7バイト目のデータが 0x01, 8バイト目が 0x00, 9バイト目が 0xff、それ以外と先頭1ビット以外は 0x00 となるデータ(先頭バイトは符号拡張バイトのため 0x00)
		n = 4611686018427404415L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("80-c0-80-80-80-80-80-81-80-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -1(1111 1111) <-> [0111 1111]
		n = -1;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -64(1100 0000) <-> [0100 0000]
		n = -64;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -65(1011 1111) <-> [1111 1111, 0011 1111]
		n = -65;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -127(1000 0001) <-> [1111 1111, 0000 0001]
		n = -127;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-01", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -128(1000 0000) <-> [1111 1111, 0000 0000]
		n = -128;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -129(1111 1111, 0111 1111) <-> [1111 1110, 0111 1111]
		n = -129;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("fe-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -8192(1110 0000, 0000 0000) <-> [1100 0000, 0000 0000]
		n = -8192;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("c0-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -8193(1101 1111, 1111 1111) <-> [1111 1111, 1011 1111, 0111 1111]
		n = -8193;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("ff-bf-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -2147483648(0x80000000) <-> [1111 1000, 1000 0000, 1000 0000, 1000 0000, 0000 0000]
		// Integer.MIN_VALUE
		n = -2147483648;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("f8-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -2147483649(0xFF7FFFFFFF) <-> [1111 0111, 1111 1111, 1111 1111, 1111 1111, 0111 1111]
		n = -2147483649L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("f7-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));

		// -9223372036854775808(0x8000 0000 0000 0000) <-> [1111 1111, 1000 0000, 1000 0000, 1000 0000, 1000 0000, 1000 0000, 1000 0000, 1000 0000, 1000 0000, 0000 0000]
		// Long.MIN_VALUE
		n = -9223372036854775808L;
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("ff-80-80-80-80-80-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.signedRepresentaionToInt(ba, 0));
	}

	@Test
	void test_isSignedRepresentaionToInt()
	{
		byte[]	ba;
		// 4 byte
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// 5 byte
		// positive
		// top(sign bit + 1 bit) + 28 bit
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 2 bit) + 28 bit
		ba = new byte[] {(byte)0x83, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 3 bit) + 28 bit
		ba = new byte[] {(byte)0x87, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 4 bit) + 28 bit
		ba = new byte[] {(byte)0x8f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 5 bit) + 28 bit
		ba = new byte[] {(byte)0x9f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 6 bit) + 28 bit
		ba = new byte[] {(byte)0xbf, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// negative
		// top(sign bit + 0 bit) + 28 bit
		ba = new byte[] {(byte)0xff, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 1 bit) + 28 bit
		ba = new byte[] {(byte)0xfe, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 2 bit) + 28 bit
		ba = new byte[] {(byte)0xfc, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 3 bit) + 28 bit
		ba = new byte[] {(byte)0xf8, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 4 bit) + 28 bit
		ba = new byte[] {(byte)0xf0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 5 bit) + 28 bit
		ba = new byte[] {(byte)0xe0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// top(sign bit + 6 bit) + 28 bit
		ba = new byte[] {(byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));

		// 6 byte
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToInt(ba, 0));
	}

	@Test
	void test_isSignedRepresentaionToLong()
	{
		byte[]	ba;

		// 9 byte
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));

		// 10 byte
		// positive
		// top(sign bit + 0 bit) + 63 bit
		ba = new byte[] {(byte)0x80, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));

		// top(sign bit + 1 bit) + 63 bit
		ba = new byte[] {(byte)0x81, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));

		// negative
		// top(sign bit + 0 bit) + 63 bit
		ba = new byte[] {(byte)0xff, (byte)0xbf, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));

		// top(sign bit + 1 bit) + 63 bit
		ba = new byte[] {(byte)0xfe, (byte)0xbf, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));

		// 11 byte
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isSignedRepresentaionToLong(ba, 0));
	}

	@Test
	void test_toSignedRepresentaion_BigInteger()
	{
		byte[]	ba;
		BigInteger	n;
		int	len;

		// 0(0000 0000) <-> [0000 0000]
		n = new BigInteger("0");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 63(0011 1111) <-> [0011 1111]
		n = new BigInteger("63");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 64(0100 0000) <-> [1000 0000, 0100 0000]
		n = new BigInteger("64");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 7bytes -> 8bytes
		n = new BigInteger("7fffffffffffff", 16);
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(8, len);
		assertByteArray("bf-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 8bytes -> 10bytes
		n = new BigInteger("70123456789abcde", 16);
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("80-f0-89-8d-8a-e7-c4-ea-f9-5e", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 9223372036854775808
		// Long.MAX_VALUE + 1
		n = new BigInteger("9223372036854775808");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("81-80-80-80-80-80-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));


		// -1(1111 1111) <-> [0111 1111]
		n = new BigInteger("-1");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// -64(1100 0000) <-> [0100 0000]
		n = new BigInteger("-64");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// -65(1011 1111) <-> [1111 1111, 0011 1111]
		n = new BigInteger("-65");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 7bytes -> 8bytes
		// -18014398509481985 = 0xbf ff ff ff ff ff ff
		n = new BigInteger("-18014398509481985");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(8, len);
		assertByteArray("df-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// 8bytes -> 10bytes
		// -5759483427458204450 = 0x b0123456789abcde
		n = new BigInteger("-5759483427458204450");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("ff-b0-89-8d-8a-e7-c4-ea-f9-5e", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));

		// -9223372036854775809
		// Long.MIN_VALUE - 1
		n = new BigInteger("-9223372036854775809");
		ba = VariableLengthIntegerRepresentation.toSignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("fe-ff-ff-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.signedRepresentaionToBigInteger(ba, 0));
	}


	@Test
	void test_toUnsignedRepresentaion_long()
	{
		byte[]	ba;
		long	n;
		int	len;

		// 0(0000 0000) <-> [0000 0000]
		n = 0;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 63(0011 1111) <-> [0011 1111]
		n = 63;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 64(0100 0000) <-> [0100 0000]
		n = 64;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 127(0111 1111) <-> [0111 1111]
		n = 127;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 128(1000 0000) <-> [1000 0001, 0000 0000]
		n = 128;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("81-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 16383(0011 1111, 1111 1111) <-> [1111 1111, 0111 1111]
		n = 16383;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 16384(0100 0000, 0000 0000) <-> [1000 0001, 1000 0000, 0000 0000]
		n = 16384;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("81-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 2097151(0001 1111, 1111 1111, 1111 1111) <-> [1111 1111, 1111 1111, 0111 1111]
		n = 2097151;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 2097152(0010 0000, 0000 0000, 0000 0000) <-> [1000 0001, 1000 0000, 1000 0000, 0000 0000]
		n = 2097152;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(4, len);
		assertByteArray("81-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 2147483647(0x7FFFFFFF) <-> [1000 0111, 1111 1111, 1111 1111, 1111 1111, 0111 1111]
		// Integer.MAX_VALUE
		n = 2147483647;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("87-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 2147483648(0x80000000) <-> [1000 1000, 1000 0000, 1000 0000, 1000 0000, 0000 0000]
		// Integer.MAX_VALUE + 1
		n = 2147483648L;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("88-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		// unsigned は 範囲外の int については変換結果の一致を保証しない
		assertEquals(0, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// 9223372036854775807(0x7FFF FFFF FFFF FFFF) <-> [1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 1111 1111, 0111 1111]
		// Long.MAX_VALUE
		n = 9223372036854775807L;
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(9, len);
		assertByteArray("ff-ff-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToLong(ba, 0));
		// unsigned は 範囲外の int については変換結果の一致を保証しない
		// assertEquals((int)n, VariableLengthIntegerRepresentation.unsignedRepresentaionToInt(ba, 0));

		// -1(1111 1111) -> exception
		n = -1;
		try
		{
			ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			;
		}

		// -2147483648(0x80000000) -> exception
		// Integer.MIN_VALUE
		n = -2147483648;
		try
		{
			ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			;
		}

		// -9223372036854775808(0x8000 0000 0000 0000) -> exception
		// Long.MIN_VALUE
		n = -9223372036854775808L;
		try
		{
			ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			;
		}
	}

	@Test
	void test_isUnsignedRepresentaionToInt()
	{
		byte[]	ba;
		// 4 byte
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// 5 byte
		// top(1 bit) + 28 bit
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(2 bit) + 28 bit
		ba = new byte[] {(byte)0x83, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(3 bit) + 28 bit
		ba = new byte[] {(byte)0x87, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(4 bit) + 28 bit
		ba = new byte[] {(byte)0x8f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(5 bit) + 28 bit
		ba = new byte[] {(byte)0x9f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(6 bit) + 28 bit
		ba = new byte[] {(byte)0xbf, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// top(7 bit) + 28 bit
		ba = new byte[] {(byte)0xff, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));

		// 6 byte
		ba = new byte[] {(byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToInt(ba, 0));
	}

	@Test
	void test_isUnsignedRepresentaionToLong()
	{
		byte[]	ba;
		// 9 byte = 63bit
		ba = new byte[] {(byte)0xff, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToLong(ba, 0));

		// 10 byte = 63bit
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isUnsignedRepresentaionToLong(ba, 0));
	}

	@Test
	void test_toUnsignedRepresentaion_BigInteger()
	{
		byte[]	ba;
		BigInteger	n;
		int	len;

		// 0(0000 0000) <-> [0000 0000]
		n = new BigInteger("0");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 63(0011 1111) <-> [0011 1111]
		n = new BigInteger("63");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 64(0100 0000) <-> [0100 0000]
		n = new BigInteger("64");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 127(0111 1111) <-> [0111 1111]
		n = new BigInteger("127");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 128(1000 0000) <-> [1000 0001, 0000 0000]
		n = new BigInteger("128");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("81-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 16383(0011 1111, 1111 1111) <-> [1111 1111, 0111 1111]
		n = new BigInteger("16383");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 16384(0100 0000, 0000 0000) <-> [1000 0001, 1000 0000, 0000 0000]
		n = new BigInteger("16384");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("81-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 7bytes -> 8bytes
		n = new BigInteger("ffffffffffffff", 16);
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(8, len);
		assertByteArray("ff-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));

		// 9223372036854775808
		// Long.MAX_VALUE + 1
		n = new BigInteger("9223372036854775808");
		ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
		len = ba.length;
		assertEquals(10, len);
		assertByteArray("81-80-80-80-80-80-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.unsignedRepresentaionToBigInteger(ba, 0));


		// -1(1111 1111) <-> [0111 1111]
		n = new BigInteger("-1");
		try
		{
			ba = VariableLengthIntegerRepresentation.toUnsignedRepresentaion(n);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			;
		}
	}


	@Test
	void test_toNearZeroRepresentaion_long()
	{
		byte[]	ba;
		long	n;
		int	len;

		// 0(0000 0000) <-> [0000 0000]
		n = 0;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 107
		n = 107;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("6b", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 108 <-> [0x6c(108), 0x6c(108)]
		n = 108;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("6c-6c", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 255 <-> [0x6c(108), 1111 1111]
		n = 255;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("6c-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 256 <-> [0x6d(109), 0000 0000]
		n = 256;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("6d-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 511 <-> [0x6d(109), 1111 1111]
		n = 511;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("6d-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1023 <-> [0x6f(111), 1111 1111]
		n = 1023;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("6f-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1024 <-> [0x70(112), 0000 0100, 0000 0000]
		n = 1024;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("70-04-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1279 <-> [0x70(112), 0000 0100, 1111 1111]
		n = 1279;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("70-04-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1280 <-> [0x70(112), 0000 0101, 0000 0000]
		n = 1280;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("70-05-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 65535 <-> [0x70(112), 1111 1111, 1111 1111]
		n = 65535;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("70-ff-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 65536 <-> [0x71(113), 0000 0000, 0000 0000]
		n = 65536;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("71-00-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1048575 <-> [0x7f(127), 1111 1111, 1111 1111]
		n = 1048575;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("7f-ff-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// 1048576 <-> [0x80(-128), 0x80, 0xc0, 0x80, 0x00]
		n = 1048576;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("80-80-c0-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));


		// -1(1111 1111) <-> [1111 1111]
		n = -1;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -107
		n = -107;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(1, len);
		assertByteArray("95", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -108 <-> [0x94(-108), 0x94(-108)]
		n = -108;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("94-94", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -256 <-> [0x94(-108), 0000 0000]
		n = -256;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("94-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -257 <-> [0x93(-109), 1111 1111]
		n = -257;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("93-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -512 <-> [0x93(-109), 0000 0000]
		n = -512;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("93-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1024 <-> [0x91(-111), 0000 0000]
		n = -1024;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("91-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1025 <-> [0x90(-112), 1111 1011, 1111 1111]
		n = -1025;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("90-fb-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1280 <-> [0x90(-112), 1111 1011, 0000 0000]
		n = -1280;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("90-fb-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1281 <-> [0x90(-112), 1111 1010, 1111 1111]
		n = -1281;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("90-fa-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -65536 <-> [0x90(-112), 0000 0000, 0000 0000]
		n = -65536;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("90-00-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -65537 <-> [0x8f(-113), 1111 1111, 1111 1111]
		n = -65537;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("8f-ff-ff", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1048576 <-> [0x81(-127), 0000 0000, 0000 0000]
		n = -1048576;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("81-00-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));

		// -1048577 <-> [0x80(-128), 1111 1111, 1011 1111, 1111 1111, 0111 1111]
		n = -1048577;
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(5, len);
		assertByteArray("80-ff-bf-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToLong(ba, 0));
		assertEquals((int)n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToInt(ba, 0));
	}

	@Test
	void test_isNearZeroRepresentaionToInt()
	{
		byte[]	ba;
		// 3 byte
		ba = new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// 先頭に 0x80 = -128 のバイトがあり、その後に符号付き可変長整数
		// 1 + 4 byte
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// 1 + 5 byte
		// positive
		// top(sign bit + 1 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 2 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0x83, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 3 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0x87, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 4 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0x8f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 5 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0x9f, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 6 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xbf, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// negative
		// top(sign bit + 1 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xff, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 1 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xfe, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 2 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xfc, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 3 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xf8, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 4 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xf0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 5 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xe0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// top(sign bit + 6 bit) + 28 bit
		ba = new byte[] {(byte)0x80, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));

		// 1 + 6 byte
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToInt(ba, 0));
	}

	@Test
	void test_isNearZeroRepresentaionToLong()
	{
		byte[]	ba;
		// 3 byte
		ba = new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// 先頭に 0x80 = -128 のバイトがあり、その後に符号付き可変長整数
		// 1 + 9 byte
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// 1 + 10 byte
		// positive
		// top(sign bit + 0 bit) + 63 bit
		ba = new byte[] {(byte)0x80, (byte)0x80, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// top(sign bit + 1 bit) + 63 bit
		ba = new byte[] {(byte)0x80, (byte)0x81, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// negative
		// top(sign bit + 0 bit) + 63 bit
		ba = new byte[] {(byte)0x80, (byte)0xff, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertTrue(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// top(sign bit + 1 bit) + 63 bit
		ba = new byte[] {(byte)0x80, (byte)0xfe, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));

		// 1 + 11 byte
		ba = new byte[] {(byte)0x80, (byte)0x80, (byte)0xc0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80,  (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x00,};
		assertFalse(VariableLengthIntegerRepresentation.isNearZeroRepresentaionToLong(ba, 0));
	}

	@Test
	void test_toNearZeroRepresentaion_BigInteger()
	{
		byte[]	ba;
		BigInteger	n;
		int	len;

		// 先頭に 0x80 = -128 のバイトがあり、その後に符号付き可変長整数
		// 0(0000 0000) <-> [0x80, 0000 0000]
		n = new BigInteger("0");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 63(0011 1111) <-> [0x80, 0011 1111]
		n = new BigInteger("63");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 64(0100 0000) <-> [0x80, 1000 0000, 0100 0000]
		n = new BigInteger("64");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("80-80-40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 7bytes -> 8bytes
		n = new BigInteger("7fffffffffffff", 16);
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(9, len);
		assertByteArray("80-bf-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 8bytes -> 10bytes
		n = new BigInteger("70123456789abcde", 16);
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(11, len);
		assertByteArray("80-80-f0-89-8d-8a-e7-c4-ea-f9-5e", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 9223372036854775808
		// Long.MAX_VALUE + 1
		n = new BigInteger("9223372036854775808");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(11, len);
		assertByteArray("80-81-80-80-80-80-80-80-80-80-00", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));


		// -1(1111 1111) <-> [0x80, 0111 1111]
		n = new BigInteger("-1");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// -64(1100 0000) <-> [0x80, 0100 0000]
		n = new BigInteger("-64");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(2, len);
		assertByteArray("80-40", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// -65(1011 1111) <-> [1111 1111, 0011 1111]
		n = new BigInteger("-65");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(3, len);
		assertByteArray("80-ff-3f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 7bytes -> 8bytes
		// -18014398509481985 = 0xbf ff ff ff ff ff ff
		n = new BigInteger("-18014398509481985");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(9, len);
		assertByteArray("80-df-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// 8bytes -> 10bytes
		// -5759483427458204450 = 0xb0123456789abcde
		n = new BigInteger("-5759483427458204450");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(11, len);
		assertByteArray("80-ff-b0-89-8d-8a-e7-c4-ea-f9-5e", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));

		// -9223372036854775809
		// Long.MIN_VALUE - 1
		n = new BigInteger("-9223372036854775809");
		ba = VariableLengthIntegerRepresentation.toNearZeroRepresentaion(n);
		len = ba.length;
		assertEquals(11, len);
		assertByteArray("80-fe-ff-ff-ff-ff-ff-ff-ff-ff-7f", ba, len);
		assertEquals(n, VariableLengthIntegerRepresentation.nearZeroRepresentaionToBigInteger(ba, 0));
	}


	public String	toHexString(byte[] ba)
	{
		StringBuilder sb = new StringBuilder();
        for (byte b : ba)
        {
            sb.append(String.format("%02x-", b));
        }
        return	sb.toString();
	}

	public void	assertByteArray(String byteStr, byte[] ba, int len)
	{
		String	s = toHexString(ba).substring(0, len * 3 - 1);
		assertEquals(byteStr, s);
	}

}
