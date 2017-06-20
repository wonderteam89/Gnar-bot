package xyz.gnarbot.gnar.commands;

import net.dv8tion.jda.core.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stores the initial data of a command class upon instantiation.
 *
 * @see CommandExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    /** @return The aliases of this command. */
    String[] aliases();

    /** @return The description of this command. */
    String description() default "No description provided.";

    /** @return The usage of this command. */
    String usage() default "";

    /** @return If the command requires the user to be the bot administrator */
    boolean admin() default false;

    /** @return If the command requires the user to be a donator. */
    boolean donor() default false;

    /** @return If the command requires the member to be the guild's owner. */
    boolean guildOwner() default false;

    /** @return If the command can be disabled. */
    boolean toggleable() default true;

    /** @return The category of the command. */
    Category category() default Category.GENERAL;

    /** @return The intended location where the command is used.. */
    Scope scope() default Scope.GUILD;

    /** @return The Discord permissions the member requires. */
    Permission[] permissions() default {};
}
