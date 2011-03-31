package ambit2.rest.facet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.datasets.EndpointCompoundFacetQuery;
import ambit2.db.search.StringCondition;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.query.QueryResource;

public class DatasetsByEndpoint extends FacetResource<EndpointCompoundFacetQuery> {
	public static final String resource = "/ndatasets_endpoint";
	

	@Override
	protected EndpointCompoundFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		

		
				
		String endpoint = getParams().getFirstValue(MetadatasetResource.search_features.feature_sameas.toString());
		EndpointCompoundFacetQuery q = new EndpointCompoundFacetQuery(getRequest().getResourceRef().toString());
		if (endpoint != null) {
			Property p  = new Property("");
			p.setLabel(endpoint);
			q.setFieldname(p);
		}
		q.setValue(getStructure());
		
		StringCondition c = StringCondition.getInstance(StringCondition.C_REGEXP);
		String param = getParams().getFirstValue(QueryResource.condition.toString());
		try {
			if (param != null)	c = StringCondition.getInstance(param);
		} catch (Exception x) {	
		} finally {
			q.setCondition(c);
		}
		return q;
	}
}
