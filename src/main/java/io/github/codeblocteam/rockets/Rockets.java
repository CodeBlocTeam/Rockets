package io.github.codeblocteam.rockets;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.World;

@Plugin(id = "rockets_codebloc", name = "Rockets", version = "0.1")
public class Rockets {
	
	@Listener
	public void onSecondaryClick(InteractItemEvent.Secondary.MainHand event) {
		
		ItemStackSnapshot snapshot = event.getItemStack();
		ItemStack stack = snapshot.createStack();
		ItemType type = stack.getItem();
		
		if (! type.equals(ItemTypes.BLAZE_ROD)) {
			return;
		}
		
		Player player = (Player) event.getCause().first(Player.class).get();
		
		stack.setQuantity(stack.getQuantity() - 1);
		player.setItemInHand(HandTypes.MAIN_HAND, stack);
		
		BlockRay<World> blockRay = BlockRay.from(player)
				.skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
				.distanceLimit(90.0)
				.build();
		
		if (! blockRay.hasNext()) {
			player.sendMessage(Text.of("PAS DE BLOC TROUVE"));
			return;
		}
		
		Cause.Builder causeBuilder = Cause.builder();
		
		blockRay.end().get().getLocation()
		.setBlockType(BlockTypes.REDSTONE_BLOCK, causeBuilder.reset().named("rockets", Sponge.getPluginManager().getPlugin("rockets_codebloc").get()).build());
		
		
	}
}
