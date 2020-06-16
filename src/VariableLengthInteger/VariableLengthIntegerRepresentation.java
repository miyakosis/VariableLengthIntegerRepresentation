package VariableLengthInteger;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 可変長整数表現
 */
public class VariableLengthIntegerRepresentation
{
	/**
	 * 可変長整数表現のバイト数を返す
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	可変長整数表現のバイト数
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static int	representationLen(byte[] ba, int pos)
	{
		int	len = 0;
		while ((ba[pos + len++] & 0x80) != 0x00)
		{
			;
		}
		return	len;
	}

// SignedRepresentaion
	/**
	 * long を 符号付き可変長整数 に変換して返す
	 * @param n	変換する long
	 * @return	符号付き可変長整数
	 */
	public static byte[]	toSignedRepresentaion(long n)
	{
		byte[]	ba = new byte[10];	// 8 bit * 8 byte < 7 bit * 10 byte であるため、変換後は最大10バイト

		// 先頭より7ビット単位でその範囲にデータが存在するか調べ(n >= threshold, n <= threshold)、あればそこから7ビットのデータを切り出して出力バイト列に詰めていく
		int	len = 0;
		int	shiftAmount = 7 * 9;
		if (n == 0)
		{
			ba[len++] = (byte)0x00;
		}
		else if (n > 0)
		{
			for (long threshold = (long)0x01 << 7 * 9 - 1; threshold > 0; threshold >>= 7)
			{
				if (n >= threshold)
				{
					ba[len++] = (byte)((n >> shiftAmount) | 0x80);
				}
				shiftAmount -= 7;
			}
			ba[len++] = (byte)(n & 0x7f);
		}
		else
		{
			for (long threshold = ~((long)0x01 << 7 * 9 - 1); threshold < -1; threshold >>= 7)
			{
				if (n <= threshold)
				{
					ba[len++] = (byte)((n >> shiftAmount) | 0x80);
				}
				shiftAmount -= 7;
			}
			ba[len++] = (byte)(n & 0x7f);
		}

		return	Arrays.copyOf(ba, len);
	}


	/**
	 * BigInteger を 符号付き可変長整数 に変換して返す
	 * @param n	変換する BigInteger
	 * @return	符号付き可変長整数
	 * @throws	NullPointerException	n に null が渡された場合
	 */
	public static byte[]	toSignedRepresentaion(BigInteger n)
	{
		byte[] bai = n.toByteArray();
		boolean	isPositive = isPositive(bai[0]);

		int	len;	// len = bai.length * 8 / 7 + 1;		// 8bit * n -> 7 bit * len
		// たとえば 1 バイトのデータは 7bit化後は2バイトとなる。
		// しかし、0x20 などのように、先頭から符号ビットと同じビットが続く場合、7bit化後のデータを1バイトで表現できる
		// 0x40 などは、7 ビット化した際の先頭ビット(1)と符号ビット(0)が異なるため、1バイトで表現できず2バイトが必要となる
		// すなわち、1バイトの場合は先頭 2 bit、2バイトの場合は先頭 3 bit ... が同じである場合、7bit化後のデータを1バイト小さくできる

		// データが 0x0020 のように冗長なバイト配列の場合はより小さく表現できるが、BigInteger.toByteArray() は最短のバイト配列を返すため考慮しない
		int	signCheckBitLen = bai.length * 8 % 7 + 1;
		boolean	canShrink = true;
		for (int i = 0; i < signCheckBitLen; ++i)
		{
			// exor を使えば 正負条件判定を省略できるが、後の判定がやや複雑になるのでしない
			if (isPositive)
			{
				if ((bai[0] & (0x80 >> i)) != 0x00)
				{	// 立っているビットがあるため省略できない
					canShrink = false;
					break;
				}
			}
			else
			{
				if ((bai[0] & (0x80 >> i)) == 0x00)
				{	// 立っていないビットがあるため省略できない
					canShrink = false;
					break;
				}
			}
		}
		len = bai.length * 8 / 7 + (canShrink ? 0 : 1);
		byte[] bao = new byte[len];

		//		7byte(56bit)の数値を8byte(56bit)で可変長数値表現する。
		//		数値を A-G の7バイト、可変長数値表現を a-h の8バイトで表すと、以下のように移送を表現できる。
		//
		//		h =       GL7
		//		g = GR1 + FL6
		//		f = FR2 + EL5
		//		e = ER3 + DL4
		//		d = DR4 + CL3
		//		c = CR5 + BL2
		//		b = BR6 + AL1
		//		a = AR7
		//
		//		A-GではGが上位バイト、Aが下位バイトとする。
		//		数値バイトを二つに分けた左側をL、右側をR、移送するビット数を数で表現している。
		//		a = AR7 は、可変長数値表現バイト a に 数値バイト A の右側7ビットを移送することを示している。

		// [0]:現在バイトのビット左シフト量 [1]:現在バイトのビットマスク [2]: 前のバイトからのキャリービット右シフト量 [3]: 前のバイトのキャリービットマスク
		int[][]	bitTable =
			{
					{0, 0x7f, -1, 0x00},
					{1, 0x3f, 7, 0x01},
					{2, 0x1f, 6, 0x03},
					{3, 0x0f, 5, 0x07},
					{4, 0x07, 4, 0x0f},
					{5, 0x03, 3, 0x1f},
					{6, 0x01, 2, 0x3f},
					{-1, 0x00, 1, 0x7f},
			};
		int	tableIndex = 0;

		int	pi = bai.length - 1;	// position in
		int	po = len - 1;			// position out

		// 出力先の末端バイトから前方に向けて順に設定していく
		for (; po >= 0; po -= 1)
		{
			int[]	table = bitTable[tableIndex];
			int	currentBits;
			if (pi < 0)
			{	// 先頭バイトのため、符号拡張する
				currentBits = isPositive ? 0x00 : 0xff;
			}
			else
			{
				currentBits = bai[pi];
			}
			if (table[0] >= 0)
			{
				currentBits = (currentBits << table[0]) & 0x7f;
			}
			else
			{
				currentBits = 0x00;
			}

			int	carryBits;
			if (table[2] >= 0)
			{
				carryBits = (bai[pi + 1] >> table[2]) & table[3];
			}
			else
			{
				carryBits = 0x00;
			}
			bao[po] = (byte)(0x80 | currentBits | carryBits);

			tableIndex = (tableIndex + 1) % bitTable.length;
			pi -= (tableIndex == 0) ? 0 : 1;	// 次に参照するのが bitTable[0] の場合、bai[pi] のデータは未参照状態であるため、デクリメントをスキップする
		}
		bao[len - 1] = (byte)(bao[len - 1] & 0x7f);

		return	bao;
	}

	/**
	 * 符号付き可変長整数 の最大下位 32bit を int として解釈して返す
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	int
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static int	signedRepresentaionToInt(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);

		// 下位 32bit を int として解釈して返す
		int	result = isPositiveVLI(ba[pos]) ? 0 : ~0;	// 符号ビットで初期設定

		int p = pos;
		if (len >= 5)
		{
			p += (len - 5);
			result <<= 4;	// filling zero
			result |= (ba[p++] & 0x0f);	// 4 bit
			len = 4;	// 4 * 7 = 28 bit
		}

		for (; len > 0; len -= 1)
		{
			result <<= 7;
			result |= (ba[p++] & 0x7f);
		}

		return	result;
	}

	/**
	 * 符号付き可変長整数 の最大下位 64bit を long として解釈して返す
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	long
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static long	signedRepresentaionToLong(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);

		// 下位 64bit を long として解釈して返す
		long	result = isPositiveVLI(ba[pos]) ? 0L : ~0L;	// 符号ビットで初期設定

		int p = pos;
		if (len >= 10)
		{
			p += (len - 10);
			result <<= 1;	// filling zero
			result |= (ba[p] & 0x01);	// 1 bit
			p += 1;
			len = 9;	// 9 * 7 = 63 bit
		}

		for (; len > 0; len -= 1)
		{
			result <<= 7;
			result |= (ba[p++] & 0x7f);
		}

		return	result;
	}


	/**
	 * 符号付き可変長整数 を BigInteger として解釈して返す
	 * @param bai	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	BigInteger
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static BigInteger	signedRepresentaionToBigInteger(byte[] bai, int pos)
	{
		boolean	isPositive = isPositiveVLI(bai[pos]);

		int	len = representationLen(bai, pos);
		int	lenBao = (len * 7 + 7) / 8;

		byte[]	bao = new byte[lenBao];


		//		8byte(56bit)の可変長数値表現を7byte(56bit)の数値に変換する
		//		数値を A-G の7バイト、可変長数値表現を a-h の8バイトで表すと、以下のように移送を表現できる。
		//
		//		G = h7  + gL1
		//		F = gR6 + fL2
		//		E = fR5 + eL3
		//		D = eR4 + dL4
		//		C = dR3 + cL5
		//		B = cR2 + bL6
		//		A = bR1 + a7
		//
		//		A-GではGが上位バイト、Aが下位バイトとする。
		//		数値バイトを二つに分けた左側をL、右側をR、移送するビット数を数で表現している。
		//		A = bL1 + aR7 は、数値バイト A に可変長数値表現バイト bの右側1ビットと a の7ビットを移送することを示している。

		// [0]: 次バイト左シフト量 [1]: 次バイトのビットマスク [2]:現在バイトのビット右シフト量 [3]:現在バイトのビットマスク
		int[][]	bitTable =
			{
					{7, 0x80, 0, 0x7f},
					{6, 0xc0, 1, 0x3f},
					{5, 0xe0, 2, 0x1f},
					{4, 0xf0, 3, 0x0f},
					{3, 0xf8, 4, 0x07},
					{2, 0xfc, 5, 0x03},
					{1, 0xfe, 6, 0x01},
			};
		int	tableIndex = 0;

		int	pi = pos + len - 1;		// position in
		int	po = lenBao - 1;		// position out

		// 出力先の末端バイトから前方に向けて順に設定していく
		for (; po >= 0; po -= 1)
		{
			int[]	table = bitTable[tableIndex];
			int	nextBits;
			if (pi - 1 < pos)
			{	// 先頭バイトのため、符号拡張する
				nextBits = isPositive ? 0x00 : 0xff;
			}
			else
			{
				nextBits = bai[pi - 1];
			}
			nextBits = (nextBits << table[0]);

			int	currentBits = (bai[pi] >> table[2]) & table[3];

			bao[po] = (byte)(nextBits | currentBits);

			tableIndex = (tableIndex + 1) % bitTable.length;
			pi -= (tableIndex == 0) ? 2 : 1;
		}

		return	new BigInteger(bao);
	}

	/**
	 * 符号付き可変長整数 が int で表現できるかどうか(データが32bit以内か)を返す。
	 * データが [0x80, 0x80, ... 0x01 ] と32bitより大きく冗長表現されている場合は厳密には int で表現できるが、可変長整数表現メソッドは冗長表現しない仕様であることより、このメソッドはデータが32bit以内かどうかで判定する。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: int で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isSignedRepresentaionToInt(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);
		if (len < 5)
		{
			return	true;
		}
		else if (len == 5)
		{
			// 7 bit * 4 byte = 28 bit
			// 先頭バイト の 7bit 中、左 4 bit が一致してる(=符号を含めて下位 4 bitをそのまま移送できる)なら int で表現できる
			int	b = (ba[pos] >> 3) & 0x0f;
			return	(b == 0x00 || b == 0x0f);
		}
		else
		{
			return	false;
		}
	}

	/**
	 * 符号付き可変長整数 が long で表現できるかどうか(データが64bit以内か)を返す。
	 * データが [0x80, 0x80, ... 0x01 ] と64bitより大きく冗長表現されている場合は厳密には long で表現できるが、可変長整数表現メソッドは冗長表現しない仕様であることより、このメソッドはデータが64bit以内かどうかで判定する。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: long で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isSignedRepresentaionToLong(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);
		if (len < 10)
		{
			return	true;
		}
		else if (len == 10)
		{
			// 7 bit * 9 byte = 63 bit
			// 先頭バイト の 7 bit が一致してる(=下位 1 bitのみを符号ビットとして移送できる)なら long で表現できる
			int	b = ba[pos] & 0x7f;
			return	(b == 0x00 || b == 0x7f);
		}
		else
		{
			return	false;
		}
	}


// UnsignedRepresentaion
	/**
	 * long を 符号なし可変長整数 に変換して返す
	 * @param n	変換する long
	 * @return	符号なし可変長整数
	 * @throws	IllegalArgumentException	n に 負の値 が渡された場合
	 */
	public static byte[]	toUnsignedRepresentaion(long n)
	{
		byte[]	ba = new byte[9];	// 63 bit <= 7 bit * 9 byte であるため、変換後は最大9バイト

		// 先頭より7ビット単位でデータが存在するか調べ(n >= threshold, n <= threshold)、あればそこから7ビットのデータを切り出して出力バイト列に詰めていく
		int	len = 0;
		int	shiftAmount = 7 * 8;
		if (n == 0)
		{
			ba[len++] = (byte)0x00;
		}
		else if (n > 0)
		{
			for (long threshold = (long)0x01 << 7 * 8; threshold > 1; threshold >>= 7)
			{
				if (n >= threshold)
				{
					ba[len++] = (byte)((n >> shiftAmount) | 0x80);
				}
				shiftAmount -= 7;
			}
			ba[len++] = (byte)(n & 0x7f);
		}
		else
		{
			throw new IllegalArgumentException();
		}

		return	Arrays.copyOf(ba, len);
	}

	/**
	 * BigInteger を 符号なし可変長整数 に変換して返す
	 * @param n	変換する BigInteger
	 * @return	符号なし可変長整数
	 * @throws	NullPointerException	n に null が渡された場合
	 * @throws	IllegalArgumentException	n に 負の値 が渡された場合
	 */
	public static byte[]	toUnsignedRepresentaion(BigInteger n)
	{
		byte[] bai = n.toByteArray();
		boolean	isPositive = isPositive(bai[0]);

		if (isPositive == false)
		{
			throw new IllegalArgumentException();
		}

		byte[]	result = toSignedRepresentaion(n);
		if (result[0] == (byte)0x80)
		{	// 符号のみのバイトを除外する
			result = Arrays.copyOfRange(result, 1, result.length);
		}
		return	result;
	}

	/**
	 * 符号なし可変長整数 の最大下位 31bit を int として解釈して返す
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	int
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static int	unsignedRepresentaionToInt(byte[] ba, int pos)
	{
		int		len = representationLen(ba, pos);

		// 下位 31bit を int として変換して返す
		int	result = 0;
		int p = pos;
		if (len >= 5)
		{
			p += (len - 5);
			result <<= 3;	// filling zero
			result |= (ba[p++] & 0x07);	// 3 bit
			len = 4;	// 4 * 7 = 28 bit
		}

		for (; len > 0; len -= 1)
		{
			result <<= 7;
			result |= (ba[p++] & 0x7f);
		}

		return	result;
	}

	/**
	 * 符号なし可変長整数 の最大下位 63bit を int として解釈して返す
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	int
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static long	unsignedRepresentaionToLong(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);

		// 下位 63bit を long として変換して返す
		long	result = 0;
		int p = pos;
		if (len >= 10)
		{
			p += (len - 10);
			len = 9;	// 9 * 7 = 63 bit
		}

		for (; len > 0; len -= 1)
		{
			result <<= 7;
			result |= (ba[p++] & 0x7f);
		}

		return	result;
	}

	/**
	 * 符号なし可変長整数 を BigInteger として解釈して返す
	 * @param bai	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	BigInteger
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static BigInteger	unsignedRepresentaionToBigInteger(byte[] bai, int pos)
	{
		boolean	isPositive = isPositiveVLI(bai[0]);

		if (isPositive == false)
		{	// add sign byte
			byte[]	baiORg = bai;
			bai = new byte[bai.length + 1];
			bai[0] = (byte)0x80;
			System.arraycopy(baiORg, 0, bai, 1, baiORg.length);
		}
		return	signedRepresentaionToBigInteger(bai, 0);
	}

	/**
	 * 符号なし可変長整数 が int で表現できるかどうか(データが31bit以内か)を返す。
	 * データが [0x80, 0x80, ... 0x01 ] と31bitより大きく冗長表現されている場合は厳密には int で表現できるが、可変長整数表現メソッドは冗長表現しない仕様であることより、このメソッドはデータが31bit以内かどうかで判定する。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: int で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isUnsignedRepresentaionToInt(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);
		if (len < 5)
		{
			return	true;
		}
		else if (len == 5)
		{
			// 7 bit * 4 byte = 28 bit
			// 先頭バイト の 7bit 中、下位 3 bit以外にビットが立っていないなら int で表現できる
			int	b = (ba[pos] >> 3) & 0x0f;
			return	b == 0x00;
		}
		else
		{
			return	false;
		}
	}

	/**
	 * 符号なし可変長整数 が long で表現できるかどうか(データが63bit以内か)を返す。
	 * データが [0x80, 0x80, ... 0x01 ] と63bitより大きく冗長表現されている場合は厳密には long で表現できるが、可変長整数表現メソッドは冗長表現しない仕様であることより、このメソッドはデータが63bit以内かどうかで判定する。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: long で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isUnsignedRepresentaionToLong(byte[] ba, int pos)
	{
		int	len = representationLen(ba, pos);
		return	(len < 10);
	}


// nearZero
	/**
	 * long を 符号付き0近傍最適化可変長整数 に変換して返す
	 * @param n	変換する long
	 * @return	符号付き0近傍最適化可変長整数
	 */
	public static byte[]	toNearZeroRepresentaion(long n)
	{
		if (-107 <= n && n <= 107)
		{
			return	new byte[] { (byte)n };
		}
		else if (108 <= n && n <= 1023)
		{
			int	b1Offset = (int)(n >> 8);
			byte b1 = (byte)(108 + b1Offset);

			return	new byte[] { b1, (byte)n };
		}
		else if (-1024 <= n && n <= -108)
		{
			int	b1Offset = (int)(n >> 8);
			byte b1 = (byte)(-107 + b1Offset);

			return	new byte[] { b1, (byte)n };
		}
		else if (1024 <= n && n <= 1048575)
		{
			int	b1Offset = (int)(n >> 16);
			byte b1 = (byte)(112 + b1Offset);

			return	new byte[] { b1, (byte)(n >> 8), (byte)n };
		}
		else if (-1048576 <= n && n <= -1025)
		{
			int	b1Offset = (int)(n >> 16);
			byte b1 = (byte)(-111 + b1Offset);

			return	new byte[] { b1, (byte)(n >> 8), (byte)n };
		}
		else
		{
			byte[]	ba = toSignedRepresentaion(n);
			byte[]	result = new byte[ba.length + 1];
			result[0] = (byte)0x80;
			System.arraycopy(ba, 0, result, 1, ba.length);
			return	result;
		}
	}

	/**
	 * BigInteger を 符号付き0近傍最適化可変長整数 に変換して返す
	 * @param n	変換する BigInteger
	 * @return	 符号付き0近傍最適化可変長整数
	 * @throws	NullPointerException	n に null が渡された場合
	 */
	public static byte[]	toNearZeroRepresentaion(BigInteger n)
	{
		byte[]	ba = toSignedRepresentaion(n);
		byte[]	result = new byte[ba.length + 1];
		result[0] = (byte)0x80;
		System.arraycopy(ba, 0, result, 1, ba.length);
		return	result;
	}

	/**
	 * 符号付き0近傍最適化可変長整数 を int として解釈して返す。32bit以上の場合は signedRepresentaionToInt() の変換の値を返す。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	int
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static int	nearZeroRepresentaionToInt(byte[] ba, int pos)
	{
		int	n = ba[pos];
		if (-107 <= n && n <= 107)
		{
			return	n;
		}
		else if (108 <= n && n <= 111)
		{
			int	b1 = n - 108;
			return	(b1 << 8) | (ba[pos + 1] & 0xff);
		}
		else if (-111 <= n && n <= -108)
		{
			int	b1 = n + 107;
			return	(b1 << 8) | (ba[pos + 1] & 0xff);
		}
		else if (112 <= n)
		{
			int	b1 = n - 112;
			return	(b1 << 16) | ((ba[pos + 1] & 0xff) << 8) | (ba[pos + 2] & 0xff);
		}
		else if (-127 <= n)
		{
			int	b1 = n + 111;
			return	(b1 << 16) | ((ba[pos + 1] & 0xff) << 8) | (ba[pos + 2] & 0xff);
		}
		else
		{
			return	signedRepresentaionToInt(ba, pos + 1);
		}
	}

	/**
	 * 符号付き0近傍最適化可変長整数 を long として解釈して返す。32bit以上の場合は signedRepresentaionToLong() の変換の値を返す。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	long
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static long	nearZeroRepresentaionToLong(byte[] ba, int pos)
	{
		int	n = ba[pos];
		if (-107 <= n && n <= 107)
		{
			return	n;
		}
		else if (108 <= n && n <= 111)
		{
			int	b1 = n - 108;
			return	(b1 << 8) | (ba[pos + 1] & 0xff);
		}
		else if (-111 <= n && n <= -108)
		{
			int	b1 = n + 107;
			return	(b1 << 8) | (ba[pos + 1] & 0xff);
		}
		else if (112 <= n)
		{
			int	b1 = n - 112;
			return	(b1 << 16) | ((ba[pos + 1] & 0xff) << 8) | (ba[pos + 2] & 0xff);
		}
		else if (-127 <= n)
		{
			int	b1 = n + 111;
			return	(b1 << 16) | ((ba[pos + 1] & 0xff) << 8) | (ba[pos + 2] & 0xff);
		}
		else
		{
			return	signedRepresentaionToLong(ba, pos + 1);
		}
	}

	/**
	 * 符号付き0近傍最適化可変長整数 を BigInteger として解釈して返す。
	 * @param bai	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	BigInteger
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static BigInteger	nearZeroRepresentaionToBigInteger(byte[] bai, int pos)
	{
		if (bai[pos] == -128)
		{
			return	signedRepresentaionToBigInteger(bai, 1);
		}
		else
		{
			return	new BigInteger(Integer.toString(nearZeroRepresentaionToInt(bai, 1)));
		}
	}

	/**
	 * 符号付き0近傍最適化可変長整数 が int で表現できるかどうか(データが32bit以内か)を返す。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: int で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isNearZeroRepresentaionToInt(byte[] ba, int pos)
	{
		return	(ba[pos] != -128) || isSignedRepresentaionToInt(ba, pos + 1);
	}

	/**
	 * 符号付き0近傍最適化可変長整数 が long で表現できるかどうか(データが64bit以内か)を返す。
	 * @param ba	データバイト配列
	 * @param pos	可変長整数表現開始位置
	 * @return	true: long で表現できる false: できない(オーバーフローする)
	 * @throws	IndexOutOfBoundsException	データバイト配列が可変長整数表現ではない場合
	 * @throws	NullPointerException	ba に null が渡された場合
	 */
	public static boolean	isNearZeroRepresentaionToLong(byte[] ba, int pos)
	{
		return	(ba[pos] != -128) || isSignedRepresentaionToLong(ba, pos + 1);
	}



// private
	/**
	 * 数値 が正か負かを返す
	 * @param b
	 * @return	true:符号ビット(MSB) が立っていない(positive) false:立っている(negative)
	 */
	private static boolean	isPositive(byte b)
	{
		return	(b & 0x80) == 0x00;
	}

	/**
	 * 可変長数値表現 が正か負かを返す
	 * @param b
	 * @return	true:符号ビット(MSB + 1) が立っていない(positive) false:立っている(negative)
	 */
	private static boolean	isPositiveVLI(byte b)
	{
		return	(b & 0x40) == 0x00;
	}
}

