package com.sagarpaliwal_task.core.util

/**
 * Base class for all failures in the application
 */
sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object UnauthorizedError : Failure()
    data class UnknownError(val message: String) : Failure()
}
