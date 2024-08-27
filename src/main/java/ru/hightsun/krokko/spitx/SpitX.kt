package ru.hightsun.krokko.spitx

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class SpitX : JavaPlugin() {
    override fun onEnable() {

        instance = this
        saveDefaultConfig()
        getCommand("spit")!!.setExecutor(SpitCMD(config as YamlConfiguration))
        getCommand("spit")!!.tabCompleter = SpitCMD(config as YamlConfiguration)
        Bukkit.getLogger().fine("SpitX был успешно запущен")

    }
    companion object {
        @JvmStatic
        var instance: SpitX? = null
            private set
    }
} 
