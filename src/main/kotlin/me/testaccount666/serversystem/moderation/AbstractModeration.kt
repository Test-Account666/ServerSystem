package me.testaccount666.serversystem.moderation

import java.util.*

abstract class AbstractModeration {
    val issueTime: Long
    val expireTime: Long
    val reason: String
    val senderUuid: UUID
    val targetUuid: UUID

    protected constructor(b: AbstractModerationBuilder<*, *>) {
        issueTime = b.issueTime
        expireTime = b.expireTime
        reason = b.reason
        senderUuid = b.senderUuid
        targetUuid = b.targetUuid
    }

    val isExpired: Boolean
        get() {
            if (expireTime == -1L) return false

            return System.currentTimeMillis() >= expireTime
        }

    abstract class AbstractModerationBuilder<C : AbstractModeration, B : AbstractModerationBuilder<C, B>> {
        internal var issueTime: Long = 0
        internal var expireTime: Long = 0
        internal lateinit var reason: String
        internal lateinit var senderUuid: UUID
        internal lateinit var targetUuid: UUID

        fun issueTime(issueTime: Long): B {
            this.issueTime = issueTime
            return self()
        }

        fun expireTime(expireTime: Long): B {
            this.expireTime = expireTime
            return self()
        }

        fun reason(reason: String): B {
            this.reason = reason
            return self()
        }

        fun senderUuid(senderUuid: UUID): B {
            this.senderUuid = senderUuid
            return self()
        }

        fun targetUuid(targetUuid: UUID): B {
            this.targetUuid = targetUuid
            return self()
        }

        protected abstract fun self(): B

        abstract fun build(): C

        override fun toString(): String {
            return "AbstractModeration.AbstractModerationBuilder(issueTime=$issueTime, expireTime=$expireTime, reason=$reason, senderUuid=$senderUuid, targetUuid=$targetUuid)"
        }
    }
}
