package ambit2.rest.propertyvalue;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveModelTemplatesPropertyValue;
import ambit2.rest.StatusException;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Retrieves property/value pairs, predicted by a model
 * @author nina
 *
 * @param <T>
 */
public class PropertyModelResource<T> extends PropertyTemplateResource<T> {
	/*
	public static final String resource = "/model";
	public static final String compoundModel = String.format("%s%s",CompoundResource.compoundID,resource);
	public static final String compoundModelID = String.format("%s%s/{idmodel}",CompoundResource.compoundID,resource);
	public static final String conformerModelID =  String.format("%s%s/{idmodel}",ConformerResource.conformerID,resource);
*/
	@Override
	protected IQueryRetrieval<T> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveModelTemplatesPropertyValue  field = new RetrieveModelTemplatesPropertyValue();
		//field.setSearchByAlias(true);
		
		IStructureRecord record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(CompoundResource.idcompound).toString())));
		} catch (NumberFormatException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",request.getAttributes().get(CompoundResource.idcompound)))
					);
		}
		try {
			record.setIdstructure(Integer.parseInt(Reference.decode(request.getAttributes().get(ConformerResource.idconformer).toString())));
			field.setChemicalsOnly(false);
		
		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {
			field.setValue(record);
		}
		try {
			field.setFieldname(null);
			Object id = request.getAttributes().get("idmodel");
			if (id != null) {
				ModelQueryResults template = new ModelQueryResults();
				template.setId(Integer.parseInt(id.toString()));
				field.setFieldname(template);
			} 
		} catch (Exception x) {
			field.setFieldname(null);
		}
		return (IQueryRetrieval) field;
	}

}
