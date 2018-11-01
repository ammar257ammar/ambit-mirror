package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.TautomerConst;



public class ZwitterionManager 
{
	protected static final Logger logger = Logger
			.getLogger(ZwitterionManager.class.getName());
	
	IAtomContainer originalMolecule = null;
	IAtomContainer molecule = null;
	List<IAcidicCenter> initialAcidicCenters = new ArrayList<IAcidicCenter>();
	List<IBasicCenter> initialBasicCenters = new ArrayList<IBasicCenter>();	
	List<IAcidicCenter> acidicCenters = new ArrayList<IAcidicCenter>();
	List<IBasicCenter> basicCenters = new ArrayList<IBasicCenter>();
	
	//TODO add flags for S and P based acids
	
	List<String> errors = new ArrayList<String>();
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
		
	
	
	public ZwitterionManager() 
	{
		init();
	}
	
	void init() 
	{	
	}
	
	public void setStructure(IAtomContainer str) throws Exception {
		molecule = str;
		originalMolecule = str;
	}	
	
	public List<IAtomContainer> generateZwitterions() throws Exception {
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		
		searchAllAcidicAndBasicCenters();
		
		//combinatorial zwitterion generation
		//TODO
		
		return null;
	}	
	
	void searchAllAcidicAndBasicCenters()
	{
		//TODO groups search and filtration
		
	}
}