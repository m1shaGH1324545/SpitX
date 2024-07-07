package org.example.pl.spitx

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.LlamaSpit
import org.bukkit.entity.Player
import org.example.pl.spitx.SpitX.Companion.instance
import java.io.File
import java.io.IOException

class SpitCMD(private val config: YamlConfiguration) : TabExecutor {
    private val file = File(instance!!.dataFolder.absolutePath + "/config.yml")
    private val logger = Bukkit.getLogger()
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            if (commandSender is Player) {
                if (commandSender.hasPermission("spit.launch")) {
                    val player = commandSender
                    val pluvok: Entity = player.launchProjectile(LlamaSpit::class.java)
                    pluvok.velocity = player.location.direction.multiply(1f)
                    val world = player.world
                    world.playSound(
                        player.location, Sound.ENTITY_LLAMA_SPIT,
                        instance!!.config.getInt("volume").toFloat(),
                        instance!!.config.getInt("pitch").toFloat()
                    )
                    return true
                } else {
                    commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Для выполнения этого действия вам нужен пермишен spit.launch")
                }
            } else {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Данную команду может использовать только игрок")
            }
        } else if (args.size == 1) {
            if (commandSender.hasPermission("spit.set")) {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Укажите второй аргумент в качестве того что вы хотите поменять (pitch/volume)")
            } else {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Для выполнения этого действия вам нужен пермишен: spit.set")
            }
        } else if (args.size == 2) {
            if ((args[1] == "volume" && commandSender.hasPermission("spit.set.volume")) ||
                (args[1] == "pitch" && commandSender.hasPermission("spit.set.pitch"))
            ) {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Укажите в качестве третьего аргумента число")
            } else {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Для выполнения этого действия вам нужен пермишен: spit.set.volume или spit.set.pitch")
            }
        } else if (args.size == 3 && commandSender.hasPermission("spit.set.volume") || args.size == 3 && commandSender.hasPermission(
                "spit.set.pitch"
            )
        ) {
            try {
                if (args[1] == "volume") {
                    config["volume"] = args[2].toInt()
                } else if (args[1] == "pitch") {
                    config["pitch"] = args[2].toInt()
                } else {
                    commandSender.sendMessage(ChatColor.RED.toString() + "Неверный аргумент. Используйте 'volume' или 'pitch'.")
                    return false
                }

                config.save(file)
                commandSender.sendMessage(ChatColor.GREEN.toString() + "Настройки успешно изменены!")
                logger.warning(commandSender as String + "поменял значение")
            } catch (e: IOException) {
                throw RuntimeException(e)
            } catch (e: NumberFormatException) {
                commandSender.sendMessage(ChatColor.RED.toString() + "Нет! Пока что командой нельзя установить float значение")
            } finally {
                commandSender.sendMessage(ChatColor.DARK_PURPLE.toString() + "Работа по смене настроек завершена.")
            }
        } else {
            return false
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return if (args.size == 1 && sender.hasPermission("spit.set")) {
            listOf("set")
        } else if (args.size == 2 && args[0] == "set" && sender.hasPermission("spit.set")) {
            listOf("volume", "pitch")
        } else if (args.size == 3 && args[1] == "volume" && sender.hasPermission("spit.set.volume")) {
            listOf("Громкость (число)")
        } else if (args.size == 3 && args[1] == "pitch" && sender.hasPermission("spit.set.pitch")) {
            listOf("Питч звука (число)")
        } else {
            emptyList()
        }
    }
}
