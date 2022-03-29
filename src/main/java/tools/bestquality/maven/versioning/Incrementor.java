package tools.bestquality.maven.versioning;

@FunctionalInterface
public interface Incrementor {
    Version next(Version current);
}
