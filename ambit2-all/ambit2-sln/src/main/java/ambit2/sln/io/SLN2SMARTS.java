package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNParser;
import ambit2.smarts.SmartsHelper;
/**
 * 
 * @author nick
 * Conversion from SLN notation to SMILES/SMARTS/SMIRKS notations
 */
public class SLN2SMARTS 
{	
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();
	
	private SLNParser slnParser = new SLNParser();
	private SLN2ChemObject slnConverter = new SLN2ChemObject();
	
	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	protected void reset() {
		conversionErrors.clear();
		conversionWarnings.clear();
	}
	
	public String slnToSmiles(String sln) throws Exception
	{
		reset();		
		SLNContainer container = slnParser.parse(sln);
		String parserErr = slnParser.getErrorMessages(); 
		if (!parserErr.equals(""))
		{
			conversionErrors.add(parserErr);			
			return null;
		}
		
		IAtomContainer mol = slnConverter.slnContainerToAtomContainer(container);
		String smiles = SmartsHelper.moleculeToSMILES(mol, false);
		
		return smiles;
	}
	
	public String slnToSmarts(String sln)
	{
		//TODO
		return null;
	}
	
	public String slnToSmirks(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmilesToSLN(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmartsToSLN(String sln)
	{
		//TODO
		return null;
	}
	
	public String SmirksToSLN(String sln)
	{
		//TODO
		return null;
	} 
	
}
