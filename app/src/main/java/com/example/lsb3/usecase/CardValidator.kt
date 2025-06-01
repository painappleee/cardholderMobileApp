package com.example.lsb3.usecase

object CardValidator {
    fun validate(name: String, shtr: String, isDisc: Boolean, disc: String): Boolean {
        if (name.isEmpty() || shtr.isEmpty()) return false
        if (isDisc) {
            val discValue = disc.toIntOrNull() ?: return false
            if (discValue >= 100 || discValue<=0) return false
        }
        return true
    }
}
