package com.movie.rate.integration

class TestUtils {

    companion object {
        @JvmStatic
        fun createTestUser(
            email: String = "user.test.${System.currentTimeMillis()}@example.com",
            username: String = "usertest${System.currentTimeMillis()}",
            fullName: String = "User Test ${System.currentTimeMillis()}"
        ): Map<String, Any> {
            return mapOf(
                "email" to email,
                "username" to username,
                "full_name" to fullName
            )
        }
    }
}
