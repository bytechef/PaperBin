package dev.binclub.paperbin.utils

import dev.binclub.paperbin.PaperBinInfo
import org.objectweb.asm.Type
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.io.PrintStream
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * @author cookiedragon234 23/Apr/2020
 */
val <T: Any> KClass<T>.internalName: String
	get() = Type.getInternalName(this.java)

fun InsnList.add(opcode: Int) = add(InsnNode(opcode))

fun ldcInt(int: Int): AbstractInsnNode {
	return if (int == -1) {
		InsnNode(ICONST_M1)
	} else if (int == 0) {
		InsnNode(ICONST_0)
	} else if (int == 1) {
		InsnNode(ICONST_1)
	} else if (int == 2) {
		InsnNode(ICONST_2)
	} else if (int == 3) {
		InsnNode(ICONST_3)
	} else if (int == 4) {
		InsnNode(ICONST_4)
	} else if (int == 5) {
		InsnNode(ICONST_5)
	} else if (int >= -128 && int <= 127) {
		IntInsnNode(BIPUSH, int)
	} else if (int >= -32768 && int <= 32767) {
		IntInsnNode(SIPUSH, int)
	} else {
		LdcInsnNode(int)
	}
}

fun InsnList.printlnAsm() {
	add(FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
	add(SWAP)
	add(MethodInsnNode(INVOKEVIRTUAL, PrintStream::class.internalName, "println", "(Ljava/lang/Object;)V", false))
}

fun InsnBuilder.printlnAsm() {
	+FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
	+SWAP.insn()
	+MethodInsnNode(INVOKEVIRTUAL, PrintStream::class.internalName, "println", "(Ljava/lang/Object;)V", false)
}

fun InsnList.printlnAsm(text: String) {
	add(MethodInsnNode(INVOKESTATIC, PaperBinInfo::class.internalName, "getLogger", "()Ljava/util/logging/Logger;", false))
	add(LdcInsnNode(text))
	add(MethodInsnNode(INVOKEVIRTUAL, Logger::class.internalName, "info", "(Ljava/lang/String;)V", false))
}
fun InsnBuilder.printlnAsm(text: String) {
	+MethodInsnNode(INVOKESTATIC, PaperBinInfo::class.internalName, "getLogger", "()Ljava/util/logging/Logger;", false)
	+LdcInsnNode(text)
	+MethodInsnNode(INVOKEVIRTUAL, Logger::class.internalName, "info", "(Ljava/lang/String;)V", false)
}

fun InsnList.printlnIntAsm() {
	add(FieldInsnNode(GETSTATIC, System::class.internalName, "out", "Ljava/io/PrintStream;"))
	add(InsnNode(SWAP))
	add(MethodInsnNode(INVOKEVIRTUAL, PrintStream::class.internalName, "println", "(I)V", false))
}
fun InsnBuilder.printlnIntAsm() {
	+FieldInsnNode(GETSTATIC, System::class.internalName, "out", "Ljava/io/PrintStream;")
	+SWAP.insn()
	+MethodInsnNode(INVOKEVIRTUAL, PrintStream::class.internalName, "println", "(I)V", false)
}
