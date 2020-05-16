package dev.binclub.paperbin.transformers

import dev.binclub.paperbin.PaperBinConfig
import dev.binclub.paperbin.PaperBinInfo
import dev.binclub.paperbin.PaperFeature
import dev.binclub.paperbin.utils.internalName
import net.minecraft.server.v1_12_R1.EntityHuman
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.MinecraftServer
import net.minecraft.server.v1_12_R1.PortalTravelAgent
import org.bukkit.PortalType
import org.bukkit.craftbukkit.v1_12_R1.CraftServer
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * @see net.minecraft.server.v1_12_R1.MinecraftServer
 *
 * @author cookiedragon234 11/May/2020
 */
object TickCounter: PaperFeature {
	override fun registerTransformers() {
		register("net.minecraft.server.v1_12_R1.MinecraftServer") { classNode ->
			var count = 0
			for (method in classNode.methods) {
				if (method.name == "D" && method.desc == "()V") {
					val list = InsnList().apply {
						add(VarInsnNode(ALOAD, 0))
						add(FieldInsnNode(GETFIELD, "net/minecraft/server/v1_12_R1/MinecraftServer", "ticks", "I"))
						add(MethodInsnNode(INVOKESTATIC, TickCounter::class.internalName, "onServerTick", "(I)V", false))
					}
					method.instructions.insert(list)
					count += 1
				}
				if (method.name == "getServerModName" && method.desc == "()Ljava/lang/String;") {
					for (insn in method.instructions) {
						if (insn is LdcInsnNode) {
							insn.cst = "PaperBin"
							count += 1
							break
						}
					}
				}
			}
			if (count != 2) {
				error("Couldnt find target")
			}
		}
		register("org.bukkit.craftbukkit.v1_12_R1.CraftServer") { classNode ->
			for (method in classNode.methods) {
				if (method.name == "getName" && method.desc == "()Ljava/lang/String;") {
					for (insn in method.instructions) {
						if (insn is LdcInsnNode) {
							insn.cst = "PaperBin"
							return@register
						}
					}
				}
			}
			error("Couldnt find target")
		}
	}
	
	@JvmStatic
	fun onServerTick(ticks: Int) {
		PaperBinInfo.ticks = ticks
		if (PaperBinConfig.debug) {
			Thread.sleep(250) // simulate low tps enviroment
		}
		
		if (!PaperBinInfo.started) {
			PaperBinInfo.onStartup()
		}
	}
}
