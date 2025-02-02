package ambit2.sln.dictionary;

import ambit2.sln.SLNParser;

public class PredefinedSLNDictionary 
{
	public static final String dictionaryObjects[] = 
		{
			"{Hev:Any[type!=1]}",
			"{Het:Any[type!=1;type!=6]}"
		};
	
	public static SLNDictionary getDictionary(SLNParser parser)
	{
		SLNDictionary dict = SLNDictionary.getDictionary(dictionaryObjects, parser);
		return dict;
	}
}
