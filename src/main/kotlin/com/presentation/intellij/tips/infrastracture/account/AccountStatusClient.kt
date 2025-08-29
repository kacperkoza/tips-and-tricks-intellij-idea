package com.presentation.intellij.tips.infrastracture.account

interface AccountStatusClient {

    fun getAccountStatus(accountId: String): AccountStatus
}

enum class AccountStatus {
    TO_ACTIVATE,
    BLOCKED,
    ACTIVE,
}
