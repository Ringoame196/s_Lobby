package com.github.Ringoame196

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.domains.DefaultDomain
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class WorldGuard {
    private val worldGuard = WorldGuard.getInstance()
    private val regionContainer = worldGuard.platform.regionContainer
    private fun getRegionManager(world: World): RegionManager? {
        return regionContainer.get(BukkitAdapter.adapt(world))
    }
    fun getProtection(player: Player, regionName: String): Boolean {
        val region: ProtectedRegion? = getRegionManager(player.world)?.getRegion(regionName)
        return region != null
    }
    private fun getRegion(world: World?, regionName: String?, location: Location?): ProtectedRegion? {
        return if (regionName != null) { getRegionManager(world ?: return null)?.getRegion(regionName) } else { regionContainer.createQuery().getApplicableRegions(BukkitAdapter.adapt(location)).iterator().next() }
    }
    fun getOwnerOfRegion(location: Location): DefaultDomain? {
        return getRegion(null, null, location)?.owners
    }
    fun delete(player: Player, regionName: String) {
        getRegionManager(player.world)?.removeRegion(getRegion(player.world,regionName,null)?.id)
    }
    fun getMemberOfRegion(location: Location): DefaultDomain? {
        return getRegion(null, null, location)?.members
    }
    fun getName(location: Location): String {
        return getRegion(null, null, location)?.id.toString()
    }
    fun getOwner(world: World, regionName: String): String? {
        return getRegion(world, regionName, null)?.owners?.toPlayersString()
    }
    fun getMember(world: World, regionName: String): DefaultDomain? {
        return getRegion(world, regionName, null)?.members
    }
    fun addOwnerToRegion(regionName: String, player: Player) {
        getRegion(player.world, regionName, null)?.owners?.addPlayer(player.uniqueId)
        getRegionManager(player.world)?.save()
        player.sendMessage("${ChatColor.YELLOW}${regionName}のownerに追加されました")
    }
    fun reset(regionName: String, world: World) {
        getRegion(world, regionName, null)?.owners?.removeAll()
        getRegion(world, regionName, null)?.members?.removeAll()
        getRegionManager(world)?.save()
    }
    fun addMemberToRegion(regionName: String, player: Player) {
        getRegion(player.world, regionName, null)?.members?.addPlayer(player.uniqueId)
        getRegionManager(player.world)?.save()
    }
    fun removeMember(regionName: String, uuid: String, world: World) {
        getRegion(world, regionName, null)?.members?.removePlayer(uuid)
        getRegionManager(world)?.save()
    }
}