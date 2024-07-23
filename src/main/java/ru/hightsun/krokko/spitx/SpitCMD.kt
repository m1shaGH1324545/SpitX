package ru.hightsun.krokko.spitx

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.LlamaSpit
import org.bukkit.entity.Player
import ru.hightsun.krokko.spitx.SpitX.Companion.instance;
import java.io.File
import java.io.IOException

class SpitCMD(private val config: YamlConfiguration) : TabExecutor {
    private val file = File(instance!!.dataFolder.absolutePath + "/config.yml")
    private val logger = Bukkit.getLogger()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when (args.size) {
            0 -> {
                if (sender is Player && sender.hasPermission("spit.launch")) {
                    val player = sender
                    player.launchProjectile(LlamaSpit::class.java).velocity = player.location.direction.multiply(1f)
                    player.world.playSound(
                        player.location, Sound.ENTITY_LLAMA_SPIT,
                        config.getInt("volume").toFloat(), config.getInt("pitch").toFloat()
                    )
                    return true
                }
                sender.sendMessage(ChatColor.RED.toString() + "Нет! Данную команду может использовать только игрок с пермишеном spit.launch")
            }
            1 -> {
                if (sender.hasPermission("spit.set")) {
                    sender.sendMessage(ChatColor.RED.toString() + "Нет! Укажите второй аргумент (pitch/volume)")
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "Нет! Нужен пермишен: spit.set")
                }
            }
            2 -> {
                if ((args[1] == "volume" && sender.hasPermission("spit.set.volume")) ||
                    (args[1] == "pitch" && sender.hasPermission("spit.set.pitch"))
                ) {
                    sender.sendMessage(ChatColor.RED.toString() + "Нет! Укажите третьим аргументом число")
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "Нет! Нужен пермишен: spit.set.volume или spit.set.pitch")
                }
            }
            3 -> {
                if ((args[1] == "volume" && sender.hasPermission("spit.set.volume")) ||
                    (args[1] == "pitch" && sender.hasPermission("spit.set.pitch") && args[0] == "set")
                ) {
                    try {
                        config[args[1]] = args[2].toInt()
                        config.save(file)
                        sender.sendMessage(ChatColor.GREEN.toString() + "Настройки успешно изменены!")
                        logger.warning("${sender.name} изменил ${args[1]} на ${args[2]}")
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    } catch (e: NumberFormatException) {
                        sender.sendMessage(ChatColor.RED.toString() + "Нет! Аргумент должен быть целым числом")
                    } finally {
                        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "Работа по смене настроек завершена.")
                    }
                    return true
                }
            }
            else -> return false
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return when {
            args.size == 1 && sender.hasPermission("spit.set") -> listOf("set")
            args.size == 2 && args[0] == "set" && sender.hasPermission("spit.set") -> listOf("volume", "pitch")
            args.size == 3 && args[1] == "volume" && sender.hasPermission("spit.set.volume") -> listOf("Громкость (число)")
            args.size == 3 && args[1] == "pitch" && sender.hasPermission("spit.set.pitch") -> listOf("Питч звука (число)")
            else -> emptyList()
        }
    }
}
