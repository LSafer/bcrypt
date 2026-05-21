package net.lsafer.bcrypt

import kotlin.random.Random

interface BCrypt {
    fun newSalt(): ByteArray

    fun newSalt(random: Random): ByteArray

    /**
     * Hashes given password with the OpenBSD bcrypt schema.
     * The cost factor will define how expensive the hash will
     * be to generate.
     */
    fun hash(password: String, cost: Int): String

    /**
     * Hashes given password with the OpenBSD bcrypt schema.
     * The cost factor will define how expensive the hash will
     * be to generate.
     */
    fun hash(password: String, salt: ByteArray, cost: Int): String

    /**
     * Verify given bcrypt hash, which includes salt and cost
     * factor with given raw password.
     */
    fun test(password: String, hash: String): Boolean
}
