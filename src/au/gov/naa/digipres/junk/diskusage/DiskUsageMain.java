package au.gov.naa.digipres.junk.diskusage;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class DiskUsageMain {

	private static final float COST_PER_GB = 500.0f;

	private static boolean useHumanReadable = false;
	private static boolean useKilobytes = false;
	private static boolean showCost = false;

	private static final int KILO_BYTES = 1024;
	private static final int MEGA_BYTES = 1024 * KILO_BYTES;
	private static final int GIGA_BYTES = 1024 * MEGA_BYTES;
	private static final int TERA_BYTES = 1024 * GIGA_BYTES;
	private static final int PETA_BYTES = 1024 * TERA_BYTES;

	private static final String KILO_BYTES_SYMBOL = "KB";
	private static final String MEGA_BYTES_SYMBOL = "MB";
	private static final String GIGA_BYTES_SYMBOL = "GB";
	private static final String TERA_BYTES_SYMBOL = "TB";
	private static final String PETA_BYTES_SYMBOL = "PB";

	private static long getFileSize(File file) {
		if (file.isDirectory()) {
			long length = 0L;
			for (File f : file.listFiles()) {
				length += getFileSize(f);
			}
			return length;
		} else {
			return file.length();
		}
	}

	private static String toHumanReadable(long fileSizeInLong) {
		String result = "";
		String[] end = {"", KILO_BYTES_SYMBOL, MEGA_BYTES_SYMBOL, GIGA_BYTES_SYMBOL, TERA_BYTES_SYMBOL, PETA_BYTES_SYMBOL};
		int postfixIndex = 0;
		double fileSize = new Long(fileSizeInLong).doubleValue();

		while ((fileSize > KILO_BYTES) && (postfixIndex < end.length - 1)) {
			fileSize /= KILO_BYTES;
			postfixIndex++;
		}

		result = String.format("%.2f %s", fileSize, end[postfixIndex]);

		return result;

	}

	private static float calcCost(long fileSize) {
		// Get size in gig
		double sizeAsDouble = new Long(fileSize).doubleValue();
		double gb = sizeAsDouble / GIGA_BYTES;

		return (float) (gb * COST_PER_GB);
	}

	private static void printUsage() {
		System.out.println("Usage: DiskUsage [-h|-k] [-c] <file or folder> [<file or folder> ...]");
		System.out.println("");
		System.out.printf("\t-h\t Human readable.\n");
		System.out.printf("\t-k\t In KB (1024).\n");
		System.out.printf("\t-c\t Calculate cost based on $%.2f per GB.\n", COST_PER_GB);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}

		List<String> filenames = new Vector<String>();

		for (String str : args) {
			if (str.equals("-h")) {
				useHumanReadable = true;
			} else if (str.equals("-k")) {
				useKilobytes = true;
			} else if (str.equals("-c")) {
				showCost = true;
			} else if (str.equals("--help")) {
				printUsage();
				System.exit(0);
			} else {
				filenames.add(str);
			}

		}

		for (String filename : filenames) {
			File file = new File(filename);
			if (!file.exists()) {
				System.out.println("File: '" + filename + "' doesn't exist!");
				continue;
			}

			long byteSize = getFileSize(file);

			float cost = 0.0f;
			if (showCost) {
				cost = calcCost(byteSize);
			}

			if (useKilobytes) {
				System.out.printf("%s: %d\n", filename, (byteSize / KILO_BYTES));
			} else if (useHumanReadable) {
				System.out.printf("%s: %s\n", filename, toHumanReadable(byteSize));
			} else {
				System.out.printf("%s: %d\n", filename, byteSize);
			}

			if (showCost) {
				System.out.printf("Cost: $%.2f\n", cost);
			}
		}

	}

}
