package ua.imfoxter.mc.foxanarchy.command.sundry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import ua.imfoxter.mc.foxanarchy.command.CustomCommand;
import ua.imfoxter.mc.foxanarchy.command.Permissions;
import ua.imfoxter.mc.foxanarchy.language.Messages;
import ua.imfoxter.mc.foxanarchy.utils.covers.TriggerSlot;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandClear extends CustomCommand<Player> {
    private final List<String> tabArgs = List.of("*", "armor" ,"inventory");
    public CommandClear() {
        super("clear",
                Permissions.COMMAND_CLEAR.getPermission(),
                "Очищає інвентар.");
    }

    @Override
    protected void execute(@NotNull Player player, String @NotNull [] args) {
        if (args.length < 1 || args.length > 2) {
            Messages.COMMAND_CLEAR_USAGE.sendMessage(player);
            return;
        }

        final String type = args[0].toLowerCase();

        if (this.hasPermission(player, type, args.length)) {
            Messages.NO_PERMISSION.sendMessage(player);
            return;
        }

        final Player target = (args.length == 2) ?
                Bukkit.getPlayer(args[1]) : player;
        if (target == null) {
            Messages.NO_PLAYER.sendMessage(player,
                    "{player}", args[1]);
            return;
        }

        if (!target.equals(player) && player.hasPermission(
                Permissions.COMMAND_CLEAR_UNTOUCHABLE.getPermission())
        ) {
            Messages.IS_BYPASS.sendMessage(player,
                    "{player}", target.getName());
            return;
        }

        if (this.tabArgs.get(0).equalsIgnoreCase(args[0])) this.executeClearAll(player, target);
        else if (this.tabArgs.get(1).equalsIgnoreCase(args[0])) this.executeClearArmor(player, target);
        else if (this.tabArgs.get(2).equalsIgnoreCase(args[0])) this.executeClearInventory(player, target);
        else Messages.NO_ARGUMENT.sendMessage(player);
    }

    @Override
    protected List<String> complete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) return this.tabArgs.stream()
                .filter(arg -> sender.hasPermission(Permissions.COMMAND_CLEAR_ARGS.getPermission()
                        .concat(arg.replace(this.tabArgs.get(0), "all")))).toList();

        if (args.length == 2 && sender.hasPermission(Permissions.COMMAND_CLEAR_OTHER.getPermission()))
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> !sender.equals(player))
                    .map(Player::getName)
                    .collect(Collectors.toList());
        return null;
    }

    /**
     * Виконує очищення інвентаря гравця.
     *
     * @param player гравець, якому надсилається повідомлення
     * @param target гравець, інвентар якого очищається
     */
    private void executeClearAll(@NotNull Player player, @NotNull Player target) {
        target.getInventory().clear();
        if (target.equals(player)) {
            Messages.COMMAND_CLEAR_ALL.sendMessage(player);
        } else {
            Messages.COMMAND_CLEAR_ALL_PLAYER.sendMessage(player,
                    "{player}", target.getName());
            Messages.COMMAND_CLEAR_ALL_OTHER.sendMessage(target,
                    "{player}", player.getName());
        }
    }

    /**
     * Виконує очищення броні гравця.
     *
     * @param player гравець, якому надсилається повідомлення
     * @param target гравець, броню якого очищається
     */
    private void executeClearArmor(@NotNull Player player, @NotNull Player target) {
        clearArmor(target);
        if (target.equals(player)) {
            Messages.COMMAND_CLEAR_ARMOR.sendMessage(player);
        } else {
            Messages.COMMAND_CLEAR_ARMOR_PLAYER.sendMessage(player,
                    "{player}", target.getName());
            Messages.COMMAND_CLEAR_ARMOR_OTHER.sendMessage(target,
                    "{player}", player.getName());
        }
    }

    /**
     * Виконує очищення інвентаря гравця.
     *
     * @param player гравець, якому надсилається повідомлення
     * @param target гравець, інвентар якого очищається
     */
    private void executeClearInventory(@NotNull Player player, @NotNull Player target) {
        clearInventory(target);
        if (target.equals(player)) {
            Messages.COMMAND_CLEAR_INVENTORY.sendMessage(player);
        } else {
            Messages.COMMAND_CLEAR_INVENTORY_PLAYER.sendMessage(player,
                    "{player}", target.getName());
            Messages.COMMAND_CLEAR_INVENTORY_OTHER.sendMessage(target,
                    "{player}", player.getName());
        }
    }

    /**
     * Очищає броню гравця, видаляючи всі предмети.
     *
     * @param player Гравець, чию броню очищається.
     */
    private void clearArmor(@NotNull Player player) {
        final PlayerInventory inventory = player.getInventory();
        Arrays.stream(TriggerSlot.values())
                .forEach(trigger -> inventory
                        .setItem(trigger.getSlot(), null));
    }

    /**
     * Очищає інвентар гравця, видаляючи всі предмети.
     *
     * @param player Гравець, чий інвентар очищається.
     */
    private void clearInventory(@NotNull Player player) {
        final PlayerInventory inventory = player.getInventory();
        Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .forEach(inventory::remove);
    }

    /**
     * Перевіряє дозвіл для виконання команди залежно від типу команди та кількості аргументів.
     *
     * @param player гравець, для якого перевіряється дозвіл
     * @param type тип команди ("*", "armor", "inventory")
     * @param length кількість аргументів команди
     * @return true, якщо гравець не має необхідного дозволу для виконання команди, false - якщо має
     */
    private boolean hasPermission(@NotNull Player player, @NotNull String type, int length) {
        return ((type.equalsIgnoreCase(this.tabArgs.get(0)) && !player.hasPermission(Permissions.COMMAND_CLEAR_ALL.getPermission())) ||
                (type.equalsIgnoreCase(this.tabArgs.get(1)) && !player.hasPermission(Permissions.COMMAND_CLEAR_ARMOR.getPermission())) ||
                (type.equalsIgnoreCase(this.tabArgs.get(2)) && !player.hasPermission(Permissions.COMMAND_CLEAR_INVENTORY.getPermission())) ||
                (type.equalsIgnoreCase(this.tabArgs.get(0)) && length == 2 && !player.hasPermission(Permissions.COMMAND_CLEAR_OTHER.getPermission())) ||
                (type.equalsIgnoreCase(this.tabArgs.get(1)) && length == 2 && !player.hasPermission(Permissions.COMMAND_CLEAR_OTHER.getPermission())) ||
                (type.equalsIgnoreCase(this.tabArgs.get(2)) && length == 2 && !player.hasPermission(Permissions.COMMAND_CLEAR_OTHER.getPermission()))
        );
    }
}
