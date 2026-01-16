package me.testaccount666.serversystem.moderation

class MuteModeration : AbstractModeration {
    val isShadowMute: Boolean

    private constructor(b: MuteModerationBuilder<*>) : super(b) {
        isShadowMute = b.isShadowMute
    }

    abstract class MuteModerationBuilder<B : MuteModerationBuilder<B>> : AbstractModerationBuilder<MuteModeration, B>() {
        internal var isShadowMute = false

        fun isShadowMute(isShadowMute: Boolean): B {
            this.isShadowMute = isShadowMute
            return self()
        }

        abstract override fun self(): B

        abstract override fun build(): MuteModeration

        override fun toString() = "MuteModeration.MuteModerationBuilder(super=${super.toString()}, isShadowMute=$isShadowMute)"
    }

    private class MuteModerationBuilderImpl : MuteModerationBuilder<MuteModerationBuilderImpl>() {
        override fun self() = this

        override fun build() = MuteModeration(this)
    }

    companion object {
        @JvmStatic
        fun builder(): MuteModerationBuilder<*> = MuteModerationBuilderImpl()
    }
}
