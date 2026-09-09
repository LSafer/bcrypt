package net.lsafer.bcrypt

import kotlin.random.Random

@OptIn(ExperimentalWasmJsInterop::class)
private val bcryptjs: BCryptJS = js("require('bcryptjs') || window.bcrypt")

private external interface BCryptJS {
    fun genSaltSync(rounds: Int): String
    fun hashSync(password: String, salt: String): String
    fun hashSync(password: String, salt: String, rounds: Int): String
    fun compareSync(password: String, hash: String): Boolean
}

object WebBCrypt : BCrypt {
    override fun newSalt(): ByteArray {
        return Random.nextBytes(16)
    }

    override fun newSalt(random: Random): ByteArray {
        return random.nextBytes(16)
    }

    override fun hash(password: String, cost: Int): String {
        val config = bcryptjs.genSaltSync(cost)
        return bcryptjs.hashSync(password, config, cost)
    }

    override fun hash(password: String, salt: ByteArray, cost: Int): String {
        val costString = cost.toString().padStart(2, '0')
        val saltString = encodeRadix64(salt).decodeToString()
        val config = "$2a$${costString}$${saltString}"
        return bcryptjs.hashSync(password, config)
    }

    override fun test(password: String, hash: String): Boolean {
        return bcryptjs.compareSync(password, hash)
    }
}
