package net.lsafer.bcrypt

private val E_MAP = charArrayOf(
    '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
    'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9'
).map { it.code.toByte() }

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
