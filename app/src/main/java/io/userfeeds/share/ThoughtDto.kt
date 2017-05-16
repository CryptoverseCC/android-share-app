package io.userfeeds.share

data class ThoughtDto(
        val context: String,
        val type: List<String>,
        val claim: Claim,
        val credits: List<Credit>,
        val signature: Signature
)

data class Claim(val target: String, val labels: List<String>?)

data class Credit(val type: String, val value: String)

data class Signature(val type: String)
