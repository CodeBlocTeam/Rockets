package io.github.codeblocteam.rockets;

import java.util.Collection;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.flowpowered.math.vector.Vector3d;

public class RayLauncher {
	
	private Location<World> rayBrowseAndDisplay(BlockRay<World> ray, Player player, Color color) {
		BlockRayHit<World> lastBlock = null;
		int i = 0;
		while (ray.hasNext()) {
			lastBlock = ray.next();
			player.getWorld().spawnParticles(
					ParticleEffect.builder()
					.type(ParticleTypes.REDSTONE_DUST)
					.option(ParticleOptions.COLOR, color)
					.build()
					, lastBlock.getPosition());
			
			Collection<Entity> entities = lastBlock.getLocation().getExtent().getEntities();
			if (i > 4 && ! entities.isEmpty()) {
				for (Entity entity : entities) {
					if ( (entity instanceof Player) && entity.getLocation().getBlockPosition().distance(lastBlock.getLocation().getBlockPosition()) < 1.1) {
						return entity.getLocation();
					}
				}
			}
			++i;
		}
		return lastBlock.getLocation();
	}
	
	private void explosion(BlockRay<World> ray, Player player) {
		
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.RED).add(0.0, 1.1, 0.0);
		Extent extent = location.getExtent();
		Entity tnt = extent.createEntity(EntityTypes.PRIMED_TNT, location.getPosition());
		tnt.offer(Keys.FUSE_DURATION, 25);
		extent.spawnEntity(tnt, Cause.source(EntitySpawnCause.builder().entity(tnt).type(SpawnTypes.PLUGIN).build()).suggestNamed(player.getName(), player).build());
	}
	
	private void teleportation(BlockRay<World> ray, Player player) {
		
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.BLUE);
		player.setLocationSafely(location);
	}
	
	private void attraction(BlockRay<World> ray, Player player) {
		
		Vector3d position = this.rayBrowseAndDisplay(ray, player, Color.GREEN).getBlockPosition().toDouble();
		
		Player target = null;
		double distance = 3.0;
		
		for (Player tar : player.getWorld().getPlayers()) {
			double dist = tar.getLocation().getPosition().distance(position);
			if ( (! tar.equals(player)) && dist < Double.min(2.8, distance) ) {
				target = tar;
				distance = dist;
			}
		}
		if (target == null)
			return;
		target.setLocationSafely(player.getLocation());
	}
	
	public void rayProcess(BlockRay<World> ray, Player player, String rayType) {
		switch (rayType) {
		case "explosif":
			this.explosion(ray, player);
			break;
		case "téléporteur" :
			this.teleportation(ray, player);
			break;
		case "attracteur" :
			this.attraction(ray,player);
			break;
		default:
			player.sendMessage(Text.of(TextColors.RED, "Type de rayon non reconnu"));
		}
	}
}
