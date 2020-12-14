package ambit2.sln.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.io.SLN2ChemObject;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;


public class SLNCli {

	private static final String title = "SLN Command Line App";

	public String inputFileName = null;
	public String outputFileName = null;
	public String inputSmiles = null;
	public String sln = null;
	public String operationString = null;
	public _operation operation = _operation.convert;
	public String outFormatString = null;
	public _out_format outFormat = _out_format.smiles;
	
	public SLNParser slnParser = new SLNParser();
	public SLNHelper slnHelper = new SLNHelper();
	public SLN2ChemObject slnConverter = new SLN2ChemObject();
	public IsomorphismTester isoTester = new IsomorphismTester();
	
	public SLNContainer slnContainer = null;
	public IAtomContainer inputMol = null;
	
	
	
	public static void main(String[] args) 
	{
		SLNCli sclCli = new SLNCli();
		sclCli.run(args);
	}

	protected static Options createOptions() {
		Options options = new Options();
		for (_option o: _option.values()) {
			options.addOption(o.createOption());
		}
		return options;
	}

	protected static void printHelp(Options options,String message) {
		if (message!=null) System.out.println(message);
		System.out.println(title);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( SLNCli.class.getName(), options );
	}

	enum _option {
		sln {
			@Override
			public String getArgName() {
				return "sln";
			}
			@Override
			public String getDescription() {
				return "Input SLN string (specify molecule or query)";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},
		smiles {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Input single molecule as smiles";
			}
			@Override
			public String getShortName() {
				return "m";
			}
		},

		input {
			@Override
			public String getArgName() {
				return "input";
			}
			@Override
			public String getDescription() {
				return "Input molecule file";
			}
			@Override
			public String getShortName() {
				return "i";
			}
		},

		output {
			@Override
			public String getArgName() {
				return "output";
			}
			@Override
			public String getDescription() {
				return "Output file name";
			}
			@Override
			public String getShortName() {
				return "o";
			}
		},
		
		operation {
			@Override
			public String getArgName() {
				return "operation";
			}
			@Override
			public String getDescription() {
				return "Operation: convert, ss_match";
			}
			@Override
			public String getShortName() {
				return "p";
			}
		},
		
		out_format {
			@Override
			public String getArgName() {
				return "out_format";
			}
			@Override
			public String getDescription() {
				return "Output format: smiles, ct, extended_ct";
			}
			@Override
			public String getShortName() {
				return "f";
			}
		},

		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return title;
			}
			@Override
			public String getShortName() {
				return "h";
			}
			@Override
			public String getDefaultValue() {
				return null;
			}
			public Option createOption() {
				Option option   = OptionBuilder.withLongOpt(name())
						.withDescription(getDescription())
						.create(getShortName());
				return option;
			}
		};	

		public abstract String getArgName();
		public abstract String getDescription();
		public abstract String getShortName();
		public String getDefaultValue() { return null; }

		public Option createOption() {
			String defaultValue = getDefaultValue();
			Option option   = OptionBuilder.withLongOpt(name())
					.hasArg()
					.withArgName(getArgName())
					.withDescription(String.format("%s %s %s",getDescription(),defaultValue==null?"":"Default value: ",defaultValue==null?"":defaultValue))
					.create(getShortName());

			return option;
		}
	}
	
	enum _operation {
		convert, ss_match;
		
		public static _operation fromString(String text) {
	        for (_operation  x : _operation.values()) {
	            if (x.name().equalsIgnoreCase(text)) {
	                return x;
	            }
	        }
	        return null;
	    }
	}
	
	enum _out_format {
		smiles, ct, extended_ct;
		
		public static _out_format fromString(String text) {
	        for (_out_format  x : _out_format.values()) {
	            if (x.name().equalsIgnoreCase(text)) {
	                return x;
	            }
	        }
	        return null;
	    }
	}

	public void setOption(_option option, String argument) throws Exception 
	{
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		case sln: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			sln = argument;
			break;
		}
		case smiles: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputSmiles = argument;
			break;
		}
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputFileName = argument;
			break;
		}
		case output: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			outputFileName = argument;
			break;
		}
		case operation: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			operationString = argument;
			operation = _operation.fromString(operationString);
			break;
		}
		case out_format: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			outFormatString = argument;
			outFormat = _out_format.fromString(outFormatString);
			break;
		}

		}	
	}
	
	public int run(String[] args) 
	{		
		Options options = createOptions();
		
		if (args == null || args.length == 0)
		{
			printHelp(options, null);
			return 0;
		}		
		
		final CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse( options, args,false );
			if (line.hasOption(_option.help.name())) {
				printHelp(options, null);
				return -1;
			}

			for (_option o: _option.values()) 
				if (line.hasOption(o.getShortName())) try {
					setOption(o,line.getOptionValue(o.getShortName()));
				} catch (Exception x) {
					printHelp(options,x.getMessage());
					return -1;
				}

			return runSLN();	

		} catch (Exception x ) {
			System.out.println("**********" + x.getMessage());
			x.printStackTrace();
			//printHelp(options,x.getMessage());
			return -1;
		} finally {
			try { 
				//run whatever cleanup is needed
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}
	
	
	protected int runSLN() throws Exception
	{		
		if (operation == null)
		{
			System.out.println("Incorrect operation: " + operationString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (outFormat == null)
		{
			System.out.println("Incorrect out format: " + outFormatString);
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
		if (sln == null && inputFileName == null && inputSmiles == null)
		{
			System.out.println("No input specified.\n"
					+ "Specify input SLN, ipnput SMILES or input files with molecules");
			System.out.println("Use option '-h' for help.");
			return -1;
		}
		
				
		if (inputSmiles != null) {
			try {
				inputMol = SmartsHelper.getMoleculeFromSmiles(inputSmiles);
			}
			catch (Exception x) {
				System.out.println("Smiles parsing error: " + x.getMessage());
				return -1;
			}
		}
		
		int res = 0;
				
		
		if (sln != null) 
		{	
			//SLN (-s/--sln) option takes precedence over Input file (-i/--input) 
			slnContainer = slnParser.parse(sln);
			if (!slnParser.getErrorMessages().equals(""))
			{
				System.out.println("Original sln:    " + sln); 
				System.out.println("SLN Parser errors:\n" + slnParser.getErrorMessages());			
				return -1;
			}

			switch (operation) {
			case convert:
				res = convert(slnContainer);
				break;
			
			case ss_match:
				if (inputFileName == null)
				{
					if (inputSmiles == null)
					{
						System.out.println("Neither SMILES nor Input File is specified for conversion!"); 
						return -1;
					}
					else
					{
						//TODO
					}
				}
				else
				{
					res = iterateInputMoleculesFile();
				}
				break;
			}
		}
		else
		{
			//SLN is not specified
			switch (operation) {
			case convert:
				if (inputFileName == null)
				{
					res = convertToSLN(inputMol);
				}
				else
				{
					res = iterateInputFileWithSLNs();
				}
				break;
			
			case ss_match:
				System.out.println("SLN is not specified. Can not perform substructure matching!");
				return -1;
				
			}
			
			
		}
		
		return res;
	}
	
	public int convert(SLNContainer container) 
	{				 
		System.out.println("Input  sln: " + sln); 
		switch (outFormat)
		{
			case ct:
				System.out.println(SLNHelper.getCTString(container));
				break;
			case extended_ct:
				printExtendedCT(container);
				break;
			case smiles:
				IAtomContainer mol = slnConverter.slnContainerToAtomContainer(container);
				if (slnConverter.hasConversionErrors())
				{	
					System.out.println("Conversion errors:");
					System.out.println(slnConverter.getAllErrors());
				}	
				else
				{	
					try {
						String smiles = SmartsHelper.moleculeToSMILES(mol, true);
						System.out.println(smiles);
					}
					catch (Exception x) {
						System.out.println("Error generating SMILES: " + x.getMessage());
					}
				}
				break;
		}		
		
		return 0;
	}
	

	public int convertToSLN(IAtomContainer container) 
	{		
		slnHelper.FlagPreserveOriginalAtomID = false;
		SLNContainer slnCon = slnConverter.atomContainerToSLNContainer(container);
		if (slnConverter.hasConversionErrors())
		{	
			System.out.println("Conversion errors:");
			System.out.println(slnConverter.getAllErrors());
			return -1;
		}
		
		System.out.println("Input  smiles: " + inputSmiles); 
		System.out.println(slnHelper.toSLN(slnCon));
		
		return 0;
	}
	
	
	public void printExtendedCT(SLNContainer container)
	{	 
		System.out.println("Atom list:");		
		System.out.println(SLNHelper.getAtomsAttributes(container));
		System.out.println("Bond list:");
		System.out.println(SLNHelper.getBondsAttributes(container));
		if (container.getAttributes().getNumOfAttributes() > 0)
		{
			System.out.println("Molecule attributes:");
			System.out.println(SLNHelper.getMolAttributes(container));
		}
	}
	
	public int iterateInputFileWithSLNs() throws Exception
	{
		//TODO
		return 0;
	}
	
	
	public int iterateInputMoleculesFile() throws Exception
	{
		int records_read = 0;
		int records_error = 0;
		
		File file = new File(inputFileName);
		
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		try 
		{
			reader = getReader(in,file.getName());
			while (reader.hasNext()) 
			{
				IAtomContainer molecule  = reader.next();
				records_read++;
				
				if (molecule==null) {
					records_error++;
					System.out.println("Unable to read chemical object #" + records_read);
					continue;
				}
				
				if (molecule.getAtomCount() == 0)
				{
					records_error++;
					System.out.println("Empty chemical object #" + records_read);
					continue;
				}
				
				performTask(molecule);
				
			}
			
		}
		catch (Exception x1) {
			System.out.println("Error: " + x1.getMessage());
		} 
		finally {
			try { reader.close(); } catch (Exception x) {}
		}
		
		return records_error;
	}
	
	
	public void performTask(IAtomContainer mol)
	{
		//TODO
	}
	
	public boolean ssMatch(SLNContainer query, IAtomContainer mol) throws Exception
	{
		isoTester.setQuery(query);
		SmartsParser.prepareTargetForSMARTSSearch(true, true, true, true, true, true, mol); //flags are set temporary
		return isoTester.hasIsomorphism(mol);
	}
	
	
	public IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
		FileInputState instate = new FileInputState();
		IIteratingChemObjectReader<IAtomContainer> reader ;
		if (extension.endsWith(FileInputState._FILE_TYPE.SDF_INDEX.getExtension())) {
			reader = new InteractiveIteratingMDLReader(in,SilentChemObjectBuilder.getInstance());
			((InteractiveIteratingMDLReader) reader).setSkip(true);
		} else reader = instate.getReader(in,extension);
		
		reader.setReaderMode(Mode.RELAXED);
		reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
				System.out.println(message);
			}
			
			@Override
			public void handleError(String message, Exception exception) {
				exception.printStackTrace();				
			}
			
			@Override
			public void handleError(String message) {
				System.out.println(message);
			}
		});
		return reader;
	}
	
	

}
