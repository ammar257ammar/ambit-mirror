package ambit2.test.smarts;

import java.util.Stack;
import java.util.Vector;
import java.io.RandomAccessFile;

import ambit2.smarts.*;

import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;


public class TestUtilities 
{	
	static SmartsParser sp = new SmartsParser();
	//static SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	static SmartsManager man = new SmartsManager();
	static IsomorphismTester isoTester = new IsomorphismTester();
	
	
	
	public static int boolSearch(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return -1;
		}		
		boolean res = man.searchIn(mol);
		if (res)
			return(1);
		else
			return(0);
	}
	
	public void testSmartsManagerBoolSearch(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}		
		boolean res = man.searchIn(mol);
		System.out.println("Man_search " + smarts + " in " + smiles + "   --> " + res);
	}
	
	public void testIsomorphismTester(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		QueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}
						
		System.out.println("IsomorphismTester: " + smarts  + "  in  " + smiles);
		isoTester.setQuery(query);
		boolean res = checkSequence(query,isoTester.getSequence());
		//isoTester.printDebugInfo();
		System.out.println("sequnce check  -- > " + res);		
	}
	
	
	public void testAtomSequencingFromFile(String fname)
	{
		int nError = 0;
		int nTests = 0;
		int nParserFails = 0;
		try
		{
			RandomAccessFile f = new RandomAccessFile(fname, "r");
			
			long length = f.length();
			while (f.getFilePointer() < length)
			{	
				String line = f.readLine();								
				String frags[] = SmartsHelper.getCarbonSkelletonsFromString(line);
				for (int k = 0; k < frags.length; k++)
				{
					//System.out.println("frag="+frags[k]);
					
					QueryAtomContainer query  = sp.parse(frags[k].trim());
					sp.setNeededDataFlags();
					String errorMsg = sp.getErrorMessages();
					if (!errorMsg.equals(""))
					{
						System.out.println("line="+line);						
						System.out.println("Smarts Parser errors: " + errorMsg);
						
						nParserFails++;
						continue;
					}
					isoTester.setQuery(query);
					boolean res = checkSequence(query,isoTester.getSequence());			
					System.out.println(frags[k].trim() + " -- > " + (res?"OK":"FAILED"));
					if (!res)
						nError++;	
					nTests++;
				}
			}
			f.close();
		}
		catch (Exception e)
		{	
			System.out.println(e.getMessage());
		}
		System.out.println("\nNumber of test = " + nTests);
		System.out.println("Number of errors = " + nError);
		System.out.println("Number of parser fails = " + nParserFails);
	}
	
	public void testAtomSequencing(String smarts[])
	{					
		int nError = 0;		
		for (int i = 0; i < smarts.length; i++)
		{
			QueryAtomContainer query  = sp.parse(smarts[i]);
			sp.setNeededDataFlags();
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{
				System.out.println("Smarts Parser errors:\n" + errorMsg);			
				continue;
			}
			isoTester.setQuery(query);
			boolean res = checkSequence(query,isoTester.getSequence());			
			System.out.println(smarts[i] + " -- > " + (res?"OK":"FAILED"));
			if (!res)
				nError++;			
		}
		
		System.out.println("\nNumber of errors = " + nError);		
	}
		
	
	public boolean checkSequence(QueryAtomContainer query, Vector<SequenceElement> sequence)
	{
		IAtomContainer skelleton = SequenceElement.getCarbonSkelleton(sequence);		
		//System.out.println("skelleton = " + SmartsHelper.moleculeToSMILES(skelleton));
		try
		{
			boolean res = UniversalIsomorphismTester.isSubgraph(skelleton, query);			
			return(res);
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
		}
		return(false);
	}
	
		
	
	//-------------------------------------------------------------------------------
	
	
	public static void main(String[] args)
	{
		TestUtilities tu = new TestUtilities();
		//tu.testSmartsManagerBoolSearch("[!$([OH1,SH1])]C(=O)[Br,Cl,F,I]","CN(C)C(=O)Cl");
		//tu.testSmartsManagerBoolSearch("[x1;C]", "CCCC");
		//tu.testSmartsManagerAtomMapping("N", "CCNCCNCC");
		//tu.testSmartsManagerAtomMapping("[x2]", "C1CCC12CC2");
		//tu.testSmartsManagerBoolSearch("c1ccccc1[N+]", "c1ccccc1[N+]");		
		//tu.testSmartsManagerBoolSearch("(CCC.C1CC12CC2).C=CC#C.CCN.(ClC)", "CCCCC");
		//tu.testSmartsManagerBoolSearch("(CCCC.CC.CCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(Cl.CCCC.CC.CCCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(CCCC.CC).(CCCN).N.C", "CCCCC.CCCN");
		//tu.testSmartsManagerBoolSearch("(CCBr.CCN).(OCC)", "BrCCCCC.CCCN.OCCC");
		//tu.testSmartsManagerBoolSearch("(CCBr).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC");
		
		//String smarts[] = {"CCC", "CCCCC", "C1CCC(C2CCC2C)CCCCC1"};
		//tu.testAtomSequencing(smarts);		
		//tu.testAtomSequencingFromFile("\\NCI001000.txt");
		
		//tu.testIsomorphismTester("C1C(CC)CCC1","CCCCC");
		//tu.testIsomorphismTester("C1CCC1CC2CCC2","CCCCC");
		
		//tu.getCarbonSkelletonsFromString();
		
		tu.testAtomIndexesForMapping(4, 5);
	}
	
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles)
	{	
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		Vector<IAtom> atoms = man.getAtomMappings(mol);
		System.out.println(smarts + " mapped against " + smiles + 
						" gave " + atoms.size()+" atoms:");
		for (int i = 0; i < atoms.size(); i++)
		{
			System.out.print(" "+mol.getAtomNumber(atoms.get(i)));
		}
		System.out.println();
	}
	
	void modify(int a[])
	{
		a[1] = 10;
		a[2] = 20;
	}
	
	public void testIntArray()
	{
		int a[] = new int [4];
		a[0] = 1;
		a[1] = 2;
		a[2] = 3;
		a[3] = 4;
		int c[] = a.clone();
		SmartsHelper.printIntArray(a);
		SmartsHelper.printIntArray(c);
		modify(a);
		SmartsHelper.printIntArray(a);
		SmartsHelper.printIntArray(c);
	}
	
	public void testAtomIndexesForMapping(int nGA, int nTA)
	{
		if (nGA == 2)
		{
			for(int i = 0; i < nTA; i++)			
				//if (el.atoms[0].matches(targetAt.get(i)))					
					for(int j = 0; j < nTA; j++)						
						if (i != j)
							//if (el.atoms[1].matches(targetAt.get(j)))
							{
								System.out.println(i+" "+j);
							}
					
			return;
		}
		
		if (nGA == 3)
		{
			for(int i = 0; i < nTA; i++)			
				//if (el.atoms[0].matches(targetAt.get(i)))					
					for(int j = 0; j < nTA; j++)						
						if (i != j)
							//if (el.atoms[1].matches(targetAt.get(j)))
								for(int k = 0; k < nTA; k++)
									if ((k != i) && (k != j))
										//if (el.atoms[2].matches(targetAt.get(k)))
										{
										System.out.println(i+" "+j+ " "+k);
										}
					
			return;
		}
		
		//This case should be very rare (el.atoms.length >= 4)
				
		//a stack which is used for obtaining all
		//posible mappings between el.atoms and targetAt
		//The stack element is an array t[], where t[k] means that 
		//el.atoms[k] is mapped against atom targetAt(t[k])
		//t[t.lenght-1] is used as a work variable which describes how mamy 
		//element of the t array are mapped
		Stack<int[]> st = new Stack<int[]>();
		
		int num = 0;
		
		//Stack initialization
		for(int i = 0; i < nTA; i++)
		{
			//if (el.atoms[0].matches(targetAt.get(i)))
			{
				int t[] = new int[nGA+1];
				t[t.length-1] = 1;
				t[0] = i;				
				st.push(t);
				
				/*
				System.out.print("push in ");
				for(int k = 0; k < t.length-1; k++)
					System.out.print(t[k]+" ");
				System.out.println();
				*/
			}
		}
		
		while (!st.isEmpty())
		{
			int t[] = st.pop();
			int n = t[t.length-1];
			
			if (n == t.length-1)
			{
				/*
				for(int k = 0; k < t.length-1; k++)
					System.out.print(t[k]+" ");
				System.out.println();
				*/
				num++;
				continue;
			}
			
			for(int i = 0; i < nTA; i++)
			{	
				//Check whether i is among first elements of t
				boolean Flag = true;
				for (int k = 0; k < n; k++)
					if ( i == t[k]) 
					{
						Flag = false;
						break;
					}
				
				if (Flag)
				//if (el.atoms[n].matches(targetAt.get(i)))
				{	
					//new stack element
					int tnew[] = new int[nGA+1];
					for(int k = 0; k < n; k++)
						tnew[k] = t[k];
					tnew[n] = i;
					tnew[t.length-1] = n+1;
					st.push(tnew);
					
					/*
					System.out.print("push in ");
					for(int k = 0; k < t.length-1; k++)
						System.out.print(tnew[k]+" ");
					System.out.println();
					*/
				}
			}
		}
		System.out.println("num = "+num);
	}
}
