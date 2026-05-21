package net.lsafer.bcrypt

private val D_MAP = byteArrayOf(
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 54, 55, 56, 57,
    58, 59, 60, 61, 62, 63, -1, -1, -1, -2, -1, -1, -1, 2, 3, 4, 5, 6, 7,
    8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
    26, 27, -1, -1, -1, -1, -1, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
    38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
)

private val E_MAP = charArrayOf(
    '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
    'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9'
).map { it.code.toByte() }

internal fun decodeRadix64(input: ByteArray): ByteArray? {
    // Ignore trailing '=' padding and whitespace from the input.
    var limit = input.size
    while (limit > 0) {
        val c = input[limit - 1]
        if (c != '='.code.toByte() && c != '\n'.code.toByte() && c != '\r'.code.toByte() && c != ' '.code.toByte() && c != '\t'.code.toByte()) {
            break
        }
        limit--
    }

    // If the input includes whitespace, this output array will be longer than necessary.
    val out = ByteArray((limit * 6L / 8L).toInt())
    var outCount = 0
    var inCount = 0

    var word = 0
    for (pos in 0..<limit) {
        val c = input[pos]

        val bits: Int
        if (c == '.'.code.toByte() || c == '/'.code.toByte() || (c >= 'A'.code.toByte() && c <= 'z'.code.toByte()) || (c >= '0'.code.toByte() && c <= '9'.code.toByte())) {
            bits = D_MAP[c.toInt()].toInt()
        } else if (c == '\n'.code.toByte() || c == '\r'.code.toByte() || c == ' '.code.toByte() || c == '\t'.code.toByte()) {
            continue
        } else {
            throw IllegalArgumentException("invalid character to decode: $c")
        }

        // Append this char's 6 bits to the word.
        word = (word shl 6) or bits.toByte().toInt()

        // For every 4 chars of input, we accumulate 24 bits of output. Emit 3 bytes.
        inCount++
        if (inCount % 4 == 0) {
            out[outCount++] = (word shr 16).toByte()
            out[outCount++] = (word shr 8).toByte()
            out[outCount++] = word.toByte()
        }
    }

    val lastWordChars = inCount % 4
    if (lastWordChars == 1) {
        // We read 1 char followed by "===". But 6 bits is a truncated byte! Fail.
        return ByteArray(0)
    } else if (lastWordChars == 2) {
        // We read 2 chars followed by "==". Emit 1 byte with 8 of those 12 bits.
        word = word shl 12
        out[outCount++] = (word shr 16).toByte()
    } else if (lastWordChars == 3) {
        // We read 3 chars, followed by "=". Emit 2 bytes for 16 of those 18 bits.
        word = word shl 6
        out[outCount++] = (word shr 16).toByte()
        out[outCount++] = (word shr 8).toByte()
    }

    // If we sized our out array perfectly, we're done.
    if (outCount == out.size) return out

    // Copy the decoded bytes to a new, right-sized array.
    return out.copyOf(outCount)
}

internal fun encodeRadix64(input: ByteArray): ByteArray {
    val length = 4 * (input.size / 3) + (if (input.size % 3 == 0) 0 else input.size % 3 + 1)
    val out = ByteArray(length)
    var index = 0
    val end = input.size - input.size % 3
    var i = 0
    while (i < end) {
        out[index++] = E_MAP[(input[i].toInt() and 0xff) shr 2]
        out[index++] = E_MAP[((input[i].toInt() and 0x03) shl 4) or ((input[i + 1].toInt() and 0xff) shr 4)]
        out[index++] = E_MAP[((input[i + 1].toInt() and 0x0f) shl 2) or ((input[i + 2].toInt() and 0xff) shr 6)]
        out[index++] = E_MAP[(input[i + 2].toInt() and 0x3f)]
        i += 3
    }
    when (input.size % 3) {
        1 -> {
            out[index++] = E_MAP[(input[end].toInt() and 0xff) shr 2]
            out[index] = E_MAP[(input[end].toInt() and 0x03) shl 4]
        }

        2 -> {
            out[index++] = E_MAP[(input[end].toInt() and 0xff) shr 2]
            out[index++] = E_MAP[((input[end].toInt() and 0x03) shl 4) or ((input[end + 1].toInt() and 0xff) shr 4)]
            out[index] = E_MAP[((input[end + 1].toInt() and 0x0f) shl 2)]
        }
    }
    return out
}
