class NativePlatform : Platform {
    override val name: String = "Native MacOS-64(Arm)"
}

actual fun getPlatform(): Platform = NativePlatform()