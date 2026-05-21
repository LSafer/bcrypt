package net.lsafer.bcrypt

import at.favre.lib.bytes.Bytes
import kotlin.random.Random
import kotlin.random.asJavaRandom

private typealias PBCrypt = at.favre.lib.crypto.bcrypt.BCrypt

private val hasher = PBCrypt.withDefaults()
private val verifier = PBCrypt.verifyer()

object JvmBCrypt : BCrypt {
    override fun newSalt(): ByteArray {
        return Bytes.random(PBCrypt.SALT_LENGTH).array()
    }

    override fun newSalt(random: Random): ByteArray {
        return Bytes.random(PBCrypt.SALT_LENGTH, random.asJavaRandom()).array()
    }

    override fun hash(password: String, cost: Int): String {
        val pwdBytes = password.toByteArray()
        val out = hasher.hash(cost, pwdBytes)
        return out.decodeToString()
    }

    override fun hash(password: String, salt: ByteArray, cost: Int): String {
        val pwdBytes = password.toByteArray()
        val out = hasher.hash(cost, salt, pwdBytes)
        return out.decodeToString()
    }

    override fun test(password: String, hash: String): Boolean {
        val pwdBytes = password.toByteArray()
        val hashBytes = hash.toByteArray()
        val out = verifier.verify(pwdBytes, hashBytes)
        return out.verified
    }
}
