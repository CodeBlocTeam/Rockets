package io.github.codeblocteam.rockets;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.World;

@Plugin(id = "rays_codebloc", name = "Rays", version = "0.1")
public class Rays {
	
	@Listener
	public void onSecondaryClick(InteractItemEvent.Secondary.MainHand event) {
		
		ItemStackSnapshot snapshot = event.getItemStack();
		ItemStack stack = snapshot.createStack();
		ItemType itemType = stack.getItem();
		
		if (! itemType.equals(ItemTypes.NETHER_STAR)) {
			return;
		}
		
		Player player = (Player) event.getCause().first(Player.class).get();
		
		stack.setQuantity(stack.getQuantity() - 1);
		player.setItemInHand(HandTypes.MAIN_HAND, stack);
		
		RayLauncher launcher = new RayLauncher();
		
		BlockRay<World> ray = BlockRay.from(player)
				.skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
				.distanceLimit(90.0)
				.build();
		
		Optional<Text> optionalRayType = stack.get(Keys.DISPLAY_NAME);
		if (! optionalRayType.isPresent()) {
			player.sendMessage(Text.of("Type de rayon non défini"));
			return;
		}
		String rayType = optionalRayType.get().toPlain().toLowerCase().replace("rayon ", "");
		
		
		launcher.rayProcess(ray, player, rayType);
		
	}
}
