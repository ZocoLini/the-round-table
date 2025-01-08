package org.lebastudios.theroundtable.communications;

public class Version implements Comparable<Version>
{
    private enum Weight
    {
        SNAPSHOT,
        ALPHA,
        BETA,
        RC,
        PRERELEASE,
        RELEASE;

        public int getWeight()
        {
            return switch (this)
            {
                case SNAPSHOT -> 0;
                case ALPHA -> 1;
                case BETA -> 2;
                case RC -> 3;
                case PRERELEASE -> 4;
                default -> 5;
            };
        }

        public static Weight fromString(String weight)
        {
            return switch (weight.toUpperCase())
            {
                case "SNAPSHOT" -> SNAPSHOT;
                case "ALPHA" -> ALPHA;
                case "BETA" -> BETA;
                case "RC" -> RC;
                case "PRERELEASE" -> PRERELEASE;
                default -> RELEASE;
            };
        }
    }

    private final int major;
    private final int minor;
    private final int patch;
    private final Weight weight;

    public Version(int major, int minor, int patch, String weight)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.weight = Weight.fromString(weight);
    }

    public Version(String version)
    {
        if (!version.matches("\\d+\\.\\d+\\.\\d+(-\\w+)?"))
        {
            throw new IllegalArgumentException(
                    "Invalid version format. Must be in the format 'major.minor.patch[-weight]'. Provided: " + version
            );
        }
        
        String[] parts = version.split("-");
        
        weight = Weight.fromString(parts.length == 2 ? parts[1] : "RELEASE");
        
        String[] versionNum = parts[0].split("\\.");
        
        major = Integer.parseInt(versionNum[0]);
        minor = Integer.parseInt(versionNum[1]);
        patch = Integer.parseInt(versionNum[2]);
    }

    public boolean isGreaterThan(Version o)
    {
        return compareTo(o) > 0;
    }
    
    public boolean isLessThan(Version o)
    {
        return compareTo(o) < 0;
    }
    
    public boolean isEqualTo(Version o)
    {
        return compareTo(o) == 0;
    }
    
    @Override
    public int compareTo(Version o)
    {
        if (major != o.major) return major - o.major;
        if (minor != o.minor) return minor - o.minor;
        if (patch != o.patch) return patch - o.patch;
        return weight.getWeight() - o.weight.getWeight();
    }
}
