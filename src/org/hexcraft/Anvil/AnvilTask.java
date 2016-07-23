package org.hexcraft.Anvil;

import org.hexcraft.HexScribe;
import org.hexcraft.hexscribe.compat.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Timer task to handle updating anvil results
 * - You should not use this class -
 */
public class AnvilTask extends BukkitRunnable {

    private ItemStack[] contents;
    private AnvilView anvil;
    private HexScribe plugin;
    private Player player;

    /**
     * @param plugin plugin reference
     * @param view   anvil view
     */
    public AnvilTask(HexScribe plugin, AnvilView view, Player player) {
    	this.plugin = plugin;
        this.anvil = view;
        this.player = player;
        contents = view.getInputSlots();
        runTaskTimer(plugin, 2, 2);
    }

    /**
     * Gets the view that the timer is handling
     *
     * @return anvil view
     */
    public AnvilView getView() {
        return anvil;
    }

    /**
     * Updates the anvil output
     */
    public void run() {
        ItemStack[] input = anvil.getInputSlots();

        if (!contents[0].equals(input[0]) || !contents[1].equals(input[1])) {
            if (anvil instanceof MainAnvil_v1_10_R1)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_10_R1) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_9_R2)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_9_R2) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_9_R1)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_9_R1) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_8_R3)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_8_R3) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_8_R2)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_8_R2) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_8_R1)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_8_R1) anvil).getNameText(), plugin, player);
            else if (anvil instanceof MainAnvil_v1_7_R4)
                AnvilMechanics.updateResult(anvil, input, ((MainAnvil_v1_7_R4) anvil).getNameText(), plugin, player);
            else
                AnvilMechanics.updateResult(anvil, input, plugin, player);
            contents = input;
        }
    }
}
