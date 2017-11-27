package ambit2.tautomers.test;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.TopLayer;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.rules.CustomTautomerRegion;
import ambit2.tautomers.rules.TautomerRegion;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTautomerRegion extends TestCase
{
	public LoggingTool logger;
	public TautomerManager tman = null;
	public boolean FlagPrintTautomers = false;
	
	public TestTautomerRegion()
	{
		logger = new LoggingTool(this);		
		tman = AbstractTautomerTest.getDefaultTautomerManager();
	}
	
	public static Test suite() {
		return new TestSuite(TestTautomerRegion.class);
	}
	
	int checkNitroGroupPositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getNitroGroupPositions(mol);
		
		if (positions.size() == 0 && expectedAtIndices.size() == 0)
			return -1; //special case
		
		int nMatchedPositions = 0;
		for (IAtom[] pos: positions)
		{
			int atInd[] = getAtomIndices(pos, mol);
			for (int[] expAtInd : expectedAtIndices)			
				if (checkEqualIndices(atInd, expAtInd))
				{
					nMatchedPositions++;
					break;
				}
		}
		return nMatchedPositions;
	}
	
	int checkNitroxidePositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getNitroxidePositions(mol);
		
		if (positions.size() == 0 && expectedAtIndices.size() == 0)
			return -1; //special case
		
		int nMatchedPositions = 0;
		for (IAtom[] pos: positions)
		{
			int atInd[] = getAtomIndices(pos, mol);
			for (int[] expAtInd : expectedAtIndices)			
				if (checkEqualIndices(atInd, expAtInd))
				{
					nMatchedPositions++;
					break;
				}
		}
		return nMatchedPositions;
	}
	
	boolean checkTautomerExcludeRegion(TautomerRegion tautoReg, String smiles, int[] expectedExcludeIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		tautoReg.calculateRegion(mol);
		List<Integer> excludeIndices = tautoReg.getExcludeAtomIndices();
		return checkEqualIndices(expectedExcludeIndices, excludeIndices);
	}
		
	int[] getAtomIndices(IAtom atoms[], IAtomContainer mol)
	{
		int indices[] = new int[atoms.length];
		for (int i = 0; i < atoms.length; i++)
			indices[i] = mol.getAtomNumber(atoms[i]);
		return indices;
	}
	
	boolean checkEqualIndices(int pos1[], int pos2[])
	{
		if (pos1.length != pos2.length)
			return false;
		int nMatches = 0;
		for (int i = 0; i < pos1.length; i++)
		{
			for (int k = 0; k < pos2.length; k++)
				if (pos1[i] == pos2[k])
				{
					nMatches++;
					continue;
				}
		}
		return (nMatches == pos1.length);
	}
	
	boolean checkEqualIndices(int pos1[], List<Integer> pos2)
	{
		if (pos1.length != pos2.size())
			return false;
		
		int nMatches = 0;
		for (int i = 0; i < pos1.length; i++)
		{
			for (int k = 0; k < pos2.size(); k++)
				if (pos1[i] == pos2.get(k).intValue())
				{
					nMatches++;
					continue;
				}
		}
		return (nMatches == pos1.length);
	}
	
	
	public void testNitroGroups() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN([O-])=O"; //Incorrect nitro group: expecting -1
		expectedPos.clear();
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, -1, res);
		
		smiles = "CCCCN"; //No nitro groups: expecting -1
		expectedPos.clear();
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, -1, res);
		
		smiles = "CCCCN(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=NCCCCN(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {6,7,8});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "CCCC[N+](=O)[O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=N(=O)CCCC[N+](=O)[O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1,2});
		expectedPos.add(new int[] {7,8,9});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
	}
	
	public void testNitroxides() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN"; //Incorrect nitro group: expecting -1
		expectedPos.clear();
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitroxide positions for " + smiles, -1, res);
		
		smiles = "CCCCN=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		smiles = "CCCC[N+][O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=NCCCC[N+][O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1});
		expectedPos.add(new int[] {6,7});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		//only one nitroxide is expected (nitro group is not considered as a nitroxide)
		smiles = "O=N(=O)CCCC[N+][O-]"; 
		expectedPos.clear();
		expectedPos.add(new int[] {7,8});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		//no nitroxide is expected (nitro group is not considered as a nitroxide)
		smiles = "O=N(=O)CCCC[N+](=O)[O-]"; 
		expectedPos.clear();
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, -1, res);
		
	}
	
	public void testTautomerRegion() throws Exception
	{
		String smiles;
		TautomerRegion tautoReg = new TautomerRegion();
		int expectedExclInd[]; 
		boolean res;
		
		//(1) Only nitro groups are excluded		
		tautoReg.setExcludeNitroGroup(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "O=NCCCC[N+](=O)[O-]";
		expectedExclInd = new int[] {6,7,8};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		
		//(2) Nitro groups and nitroxides are excluded	
		tautoReg.setExcludeNitroxides(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "O=NCCCC[N+](=O)[O-]";
		expectedExclInd = new int[] {0,1,6,7,8};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN(=O)=O";
		expectedExclInd = new int[] {8,9,10};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		
		//(3) Nitro groups, nitroxides and aromatic systems are excluded	
		tautoReg.setExcludeAromaticSystems(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN(=O)=O";
		expectedExclInd = new int[] {0,1,2,3,4,5,8,9,10};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN=O";
		expectedExclInd = new int[] {0,1,2,3,4,5,8,9};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
	}
	
		
	public void test_TautomerRegionGeneration_01() throws Exception {
		tman.tautomerRegion.setUseRegion(true);
		int res = TestTautomers.testCase("OC1=CC=CC=C1",
				new String[] { "Oc1=cc=cc=c1", "O=C1C=CC=CC1", "O=C1C=CCC=C1" }, FlagPrintTautomers, tman);

		Assert.assertEquals(0, res);
	}
}
