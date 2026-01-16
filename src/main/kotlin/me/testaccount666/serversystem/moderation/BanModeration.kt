package me.testaccount666.serversystem.moderation

class BanModeration : AbstractModeration {

    private constructor(b: BanModerationBuilder<*>) : super(b)

    abstract class BanModerationBuilder<B : BanModerationBuilder<B>> : AbstractModerationBuilder<BanModeration, B>() {
        abstract override fun self(): B

        abstract override fun build(): BanModeration

        override fun toString() = "BanModeration.BanModerationBuilder(super=${super.toString()})"
    }

    private class BanModerationBuilderImpl : BanModerationBuilder<BanModerationBuilderImpl>() {
        override fun self() = this

        override fun build() = BanModeration(this)
    }

    companion object {
        @JvmStatic
        fun builder(): BanModerationBuilder<*> = BanModerationBuilderImpl()
    }
}
