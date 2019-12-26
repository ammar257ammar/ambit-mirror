package ambit2.groupcontribution.correctionfactors;

import ambit2.groupcontribution.transformations.IValueTransformation;

public class DescriptorInfo 
{
	public double contribution = 0.0;
	public String fullString = "";
	public String name = "";
	public IValueTransformation valueTranform = null;
	
	public double getContribution()
	{
		return contribution; 
	}
	
	public void setContribution(double contribution)
	{
		this.contribution = contribution;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	//public int calculateFor(IAtomContainer mol);
	//public Type getType();
}
