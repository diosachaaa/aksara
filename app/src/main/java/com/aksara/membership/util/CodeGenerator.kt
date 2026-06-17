package com.aksara.membership.util

import kotlin.random.Random

/** Membuat kode voucher acak, contoh: AKS-7K2P-9XQ4. */
object CodeGenerator {
    private const val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun voucherCode(): String {
        fun block() = (1..4).map { CHARS[Random.nextInt(CHARS.length)] }.joinToString("")
        return "AKS-${block()}-${block()}"
    }
}
