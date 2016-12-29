package io.github.codeblocteam.rockets;

import java.util.Collection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
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
			
			if (color == null) {
				player.getWorld().spawnParticles(
						ParticleEffect.builder()
						.type(ParticleTypes.FLAME)
						.build()
						, lastBlock.getPosition());
			} else {
				player.getWorld().spawnParticles(
						ParticleEffect.builder()
						.type(ParticleTypes.REDSTONE_DUST)
						.option(ParticleOptions.COLOR, color)
						.build()
						, lastBlock.getPosition());
			}
			
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
	
	private void bombard(BlockRay<World> ray, Player player) {
		
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.RED).add(0.,30.,0.);
		
		for (double i=-0.5; i<1.; ++i) {
			for (double j=-0.5; j<1.; ++j) {
				Location<World> loc = location.add(2*i, 0., 2*j);
				Extent extent = loc.getExtent();
				Entity tnt = extent.createEntity(EntityTypes.PRIMED_TNT, loc.getPosition());
				extent.spawnEntity(tnt, Cause.source(EntitySpawnCause.builder().entity(tnt).type(SpawnTypes.PLUGIN).build()).suggestNamed(player.getName(), player).build());
			}
		}
	}
	
	private void teleportation(BlockRay<World> ray, Player player) {
		
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.BLUE);
		player.setLocationSafely(location);
	}
	
	private void attraction(BlockRay<World> ray, Player player) {
		
		Vector3d position = this.rayBrowseAndDisplay(ray, player, Color.PINK).getBlockPosition().toDouble();
		
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
		target.setLocation(player.getLocation());
	}
	
	private void swich(BlockRay<World> ray, Player player) {
		
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.YELLOW);
		Vector3d position = location.getBlockPosition().toDouble();
		
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
		target.setLocation(player.getLocation());
		player.setLocationSafely(location);
	}
	
	private void sticky(BlockRay<World> ray, Player player) {
		Location<World> location = this.rayBrowseAndDisplay(ray, player, Color.LIME);
		for (double i=-3.; i<4.; ++i) {
			for (double j=-3.; j<4.; ++j) {
				for (double k=-3.; k<4.; ++k) {
					double I = (i < 0. ? -i : i);
					double K = (k < 0. ? -k : k);
					if (I+K > 4)
						continue;
					Location<World> target = location.add(i,j,k);
					if ((! target.getBlockType().equals(BlockTypes.AIR)) && target.add(0.0, 1.0, 0.0).getBlockType().equals(BlockTypes.AIR)) {
						target.setBlockType(BlockTypes.SLIME, Cause.source(Sponge.getPluginManager().getPlugin("rays_codebloc").get()).build());
					}
				}
				
			}
		}
	}
	
	private void inferno(BlockRay<World> ray, Player player) {
		Location<World> location = this.rayBrowseAndDisplay(ray, player, null);
		for (double i=-3.; i<4.; ++i) {
			for (double j=-3.; j<4.; ++j) {
				for (double k=-3.; k<4.; ++k) {
					double I = (i < 0. ? -i : i);
					double K = (k < 0. ? -k : k);
					if (I+K > 4)
						continue;
					Location<World> target = location.add(i,j,k);
					if ((! target.getBlockType().equals(BlockTypes.AIR)) && target.add(0.0, 1.0, 0.0).getBlockType().equals(BlockTypes.AIR)) {
						target.setBlockType(BlockTypes.MAGMA, Cause.source(Sponge.getPluginManager().getPlugin("rays_codebloc").get()).build());
					}
				}
				
			}
		}
	}
	
	private void jail(BlockRay<World> ray, Player player) {
		Vector3d position = this.rayBrowseAndDisplay(ray, player, Color.GRAY).getBlockPosition().toDouble();
		
		Player target = null;
		double distance = 3.0;
		
		for (Player tar : player.getWorld().getPlayers()) {
			double dist = tar.getLocation().getPosition().distance(position);
			if ( (! tar.equals(player)) && dist < Double.min(2.8, distance) ) {
				target = tar;
				distance = dist;
			}
		}
		Location<World> targetLocation = target.getLocation();
		for (double i=-1.; i<2; ++i) {
			for (double j=-1.; j<3; ++j) {
				for (double k=-1.; k<2; ++k) {
					targetLocation.add(i,j,k).setBlockType(BlockTypes.GLASS, Cause.source(Sponge.getPluginManager().getPlugin("rays_codebloc").get()).build());
				}
			}
		}
	}
	
	public void rayProcess(BlockRay<World> ray, Player player, String rayType) {
		switch (rayType) {
		case "bombardier":
			this.bombard(ray, player);
			break;
		case "téléporteur" :
			this.teleportation(ray, player);
			break;
		case "attracteur" :
			this.attraction(ray,player);
			break;
		case "gluant" :
			this.sticky(ray,player);
			break;
		case "infernal" :
			this.inferno(ray,player);
			break;
		case "incapacitant" :
			this.jail(ray,player);
			break;
		case "échangeur" :
			this.swich(ray,player);
			break;
		default:
			player.sendMessage(Text.of(TextColors.RED, "Type de rayon non reconnu"));
		}
	}
}
