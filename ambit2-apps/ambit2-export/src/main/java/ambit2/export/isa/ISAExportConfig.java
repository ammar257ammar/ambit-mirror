package ambit2.export.isa;

import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;

import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.base.ISALocation;
import ambit2.rules.json.JSONParsingUtils;

public class ISAExportConfig 
{
	protected final static Logger logger = Logger.getLogger(ISAExportConfig.class.getName());
	
	public ISAFormat isaFormat = ISAFormat.JSON;
	public ISAVersion isaVersion = ISAVersion.Ver1_0;
	
	//Data locations
	public ISALocation protocolParamLoc = null;
	public String protocolParamLocString = "study.0.process[1]";
	
	public ISALocation effectRecordLoc = null;
	public String effectRecordLocString = "assay.0.process[2]"; 
	
	
	public void parseJSONConfig(JsonNode rootNode) throws Exception
	{	
		JsonNode curNode;
		
		//Handling section EXPORT_INFO
		curNode = rootNode.path("EXPORT_INFO");
		if (curNode.isMissingNode())
			throw new Exception ("Section \"EXPORT_INFO\" is missing!");
		else
		{
			//ISA_FORMAT
			//ISA_VERSION
			//TODO
		}		

		//Handling section ISA_MAPPING
		curNode = rootNode.path("ISA_MAPPING");
		if (curNode.isMissingNode())
		{	
			//If missing default values are used
		}	
		else
		{
			
			//TODO
		}
		
		parseISALocations();
	}
	
	public void parseISALocations() throws Exception
	{
		protocolParamLoc = parseISALocationString(protocolParamLocString, "ISA Location for protocolParam");
		effectRecordLoc = parseISALocationString(effectRecordLocString, "ISA Location for effectRecord");
	}
	
	protected ISALocation parseISALocationString(String isaLocStr, String contexInfo) throws Exception
	{
		try
		{
			ISALocation loc = ISALocation.parseString(isaLocStr);
			return loc;
		}
		catch(Exception e)
		{
			throw new Exception("Error in " + contexInfo + "/n" + e.getMessage());
		}
	}
	
	public static void fillDefaultISAConfig(ISAExportConfig cfg)
	{
		try
		{
			cfg.parseISALocations();
		}
		catch(Exception e){};
	}
	
}