package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountProtocolApplications   extends QueryCount<SubstanceByCategoryFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6001454887114379950L;
	
	protected SubstanceEndpointsBundle bundle = null;
	
	public SubstanceEndpointsBundle getBundle() {
		return bundle;
	}
	public void setBundle(SubstanceEndpointsBundle bundle) {
		this.bundle = bundle;
	}
	public QueryCountProtocolApplications(String facetURL) {
		super(facetURL);
		setPageSize(Integer.MAX_VALUE);
	}
	/**
	 * 
	 */
	
	private static final String sql = 
		"SELECT topcategory,count(*),endpointcategory,count(distinct(concat(substance_prefix,substance_uuid)))  FROM substance_protocolapplication p %s group by topcategory,endpointcategory";
	
	private static final String sql_bundle =
		"SELECT p.topcategory,count(*),p.endpointcategory,count(distinct(concat(substance_prefix,substance_uuid)))  FROM substance_protocolapplication p join bundle_endpoints b where b.topcategory=p.topcategory and b.endpointcategory=p.endpointcategory and idbundle=? %s group by p.topcategory,p.endpointcategory";
	
	/*
select p.topcategory,p.endpointcategory,count(*) from substance_protocolapplication p, bundle_endpoints b 
where p.topcategory=b.topcategory
and p.endpointcategory=b.endpointcategory
and idbundle=1
group by p.topcategory,p.endpointcategory
order by p.topcategory,p.endpointcategory
 
	 */
	private static final String w_topcategory = "p.topcategory=?";
	private static final String w_endpointcategory = "p.endpointcategory=?";
	 
		
	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = ((bundle!=null) && (bundle.getID()>0))?"\nand ":"\nwhere ";
		if (getFieldname()!=null) { b.append(d); b.append(w_topcategory); d = " and ";}
		if (getValue()!=null) { b.append(d); b.append(w_endpointcategory); d = " and ";}
		return String.format(((bundle!=null) && (bundle.getID()>0))?sql_bundle:sql,b.toString());
	}
	@Override
	public void setValue(String value) {
		if (value!=null && !value.endsWith("_SECTION")) super.setValue(value+"_SECTION");
		else super.setValue(value);
	}
	
	@Override
	protected SubstanceByCategoryFacet createFacet(String facetURL) {
		return new SubstanceByCategoryFacet(facetURL);
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((bundle!=null) && (bundle.getID()>0)) params.add(new QueryParam<Integer>(Integer.class, getBundle().getID())); 
		if (getFieldname()!=null) params.add(new QueryParam<String>(String.class, getFieldname().toString()));
		if (getValue()!=null) params.add(new QueryParam<String>(String.class, getValue().toString()));
		return params;
	}
	@Override
	public SubstanceByCategoryFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			facet.setSubstancesCount(rs.getInt(4));
			
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	

}
