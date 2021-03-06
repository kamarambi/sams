package specfile;

import specfile.impl.*;

import java.io.*;

/**
 * This class gets ISpectrumFile objects.
 * It works on the following file types:
 * <ul>
 *	<li> "GER"
 *	<li> Ascii ASD, "ASD"
 *	<li> Binary ASD, "ASDb"
 *	<li> Only a column with reflectance values, "Only-Reflectance" (INCOMPLETE)
 * </ul>
 * Call getFileTypes() to get the list of recognized file types.
 *
 * @see ISpectrumFile
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public final class SpectrumFileManager {
	// the list of currently recognized file types:
	private static final String[] filetypes = 
		{"GER", "ASD", "ASDb", "CSV", "Only-Reflectance"};

	/**
	 * Gets the list of recognized file types.
	 *
	 * @return The array of String's containing the type codes.
	 */
	public static String[] getFileTypes() {
		return filetypes;
	}
		
	/**
	 * Opens a spectrum file.
	 *
	 * This results in the creation of an object according to
	 * the format of the file.
	 *
	 * @param filename The spectrum file name.
	 * @param filetype Assumed file type.
	 *                  If null, an automatic recognition is attempted.
	 *
	 * @return The ISpectrumFile object.
	 *
	 * @throws FileNotFoundException
	 * @throws InvalidSpectrumFormatException
	 * @throws IOException
	 */
	public static ISpectrumFile openSpectrumFile(String filename, String filetype)
	throws FileNotFoundException, InvalidSpectrumFormatException, IOException
	{
		if ( filetype != null ) {
			if ( filetype.equals("GER") )
				return new GERFile(filename);
			if ( filetype.equals("ASDb") )
				return new ASDBinaryFile(filename);
			if ( filetype.equals("ASD") )
				return new ASDFile(filename);
			if ( filetype.equals("CSV") )
				return new CSVFile(filename);
			if ( filetype.equals("Only-Reflectance") )
				return new OnlyReflectanceFile(filename);
			
			throw new InvalidSpectrumFormatException(filetype+ ": file type not recognized");
		}

		// automatically recognize the format:

		ISpectrumFile sf = null;
		
		//
		// Some preference seems reasonable to be applied 
		// according to file name extension.
		// However this is still ad hoc since there are no standard
		// extensions for most of the considered formats.
		// Note also that this could be improved more properly with
		// a factory design pattern.
		//
		
		// .csv if currently the only extension considered in advance
		if ( filename.toLowerCase().endsWith(".csv") ) {
			try {
				sf = new CSVFile(filename);
				return sf;
			}
			catch ( Exception ex ) {
				// ignore; try the formats below.
			}
		}

		try {
			// try GER:
			sf = new GERFile(filename);
		}
		catch ( Exception ex1 ) {
			try {
				// try ASDb:
				sf = new ASDBinaryFile(filename);
			}
			catch ( Exception ex2 ) {
				try {
					// try ASD:
					sf = new ASDFile(filename);
				}
				catch ( Exception ex3 ) {
					try {
						// try OnlyReflectanceFile:
						sf = new OnlyReflectanceFile(filename);
					}
					catch ( Exception ex4 ) {
						// 
						// Include here for other formats ...
						//
						
						throw new InvalidSpectrumFormatException(
							new File(filename).getName()+
							": Cannot recognize the format"
						);
					}
				}
			}
		}

		return sf;
	}

	// Non-instanciable
	private SpectrumFileManager() {}

}