package io.github.revxrsal.cub;

import io.github.revxrsal.cub.ParameterResolver.ContextResolver;
import io.github.revxrsal.cub.ParameterResolver.ValueResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a {@link ContextResolver} for specific types of parameters. These are
 * most useful in the following cases:
 * <ul>
 *     <li>Creating a context resolver for only a specific type of parameters,
 *     for example those with a specific annotation</li>
 *     <li>Creating context resolvers for a common interface or class</li>
 * </ul>
 * <p>
 * Example: We want to register a special context resolver for org.bukkit.Locations that
 * are annotated with a specific annotation, in which the argument will be fed with the player's
 * target location
 * <pre>{@code
 *
 *  @Target(ElementType.PARAMETER)
 *  @Retention(RetentionPolicy.RUNTIME)
 *  public @interface LookingLocation {
 *
 *  }
 *
 *  public class LookingLocationFactory implements ContextResolverFactory {
 *
 *       @Override public ParameterResolver.ContextResolver<?> create(CommandParameter parameter, HandledCommand command, CommandHandler handler) {
 *           if (parameter.getType() != Location.class) return null;
 *           if (!parameter.hasAnnotation(LookingLocation.class)) return null;
 *           return (args, subject, parameter1) -> {
 *               Player player = ((BukkitCommandSubject) subject).requirePlayer();
 *               return player.getTargetBlock(null, 200).getLocation();
 *           };
 *       }
 *    }
 * }</pre>
 * <p>
 * Note that {@link ContextResolverFactory}ies must be registered
 * with {@link CommandHandler#registerContextResolverFactory(ContextResolverFactory)}.
 */
public interface ContextResolverFactory extends ResolverFactory<ContextResolver<?>> {

    /**
     * Creates a context resolver for the specified type, or {@code null} if this type
     * is not supported by this factory.
     *
     * @param parameter The parameter to create for
     * @param command   The declaring command
     * @param handler   The command handler
     * @return The {@link ContextResolver}, or null if not supported.
     */
    @Nullable ContextResolver<?> create(@NotNull CommandParameter parameter, @NotNull HandledCommand command, @NotNull CommandHandler handler);

    /**
     * Creates a {@link ContextResolverFactory} that will return the same
     * resolver for all parameters that match a specific type
     *
     * @param type     Type to check for
     * @param resolver The value resolver to use
     * @param <T>      The resolver value type
     * @return The resolver factory
     */
    static <T> @NotNull ContextResolverFactory forType(Class<T> type, ContextResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type ? resolver : null;
    }

    /**
     * Creates a {@link ContextResolverFactory} that will return the same
     * resolver for all parameters that match or extend a specific type
     *
     * @param type     Type to check for
     * @param resolver The value resolver to use
     * @param <T>      The resolver value type
     * @return The resolver factory
     */
    static <T> @NotNull ContextResolverFactory forHierarchyType(Class<T> type, ContextResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type
                || parameter.getType().isAssignableFrom(type) ? resolver : null;
    }
}
