package common.util;

public class Util
{
    private Util() {}

    public static String parseClassName(String filename)
    {
        return filename.replace(".java", "").replace("/", ".");
    }
}
