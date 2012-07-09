import java.util.Properties;

public class PrintProperties
{
	public static void main(String[] args)
	{
		Properties properties = System.getProperties();
		properties.list(System.out);
	}
}
