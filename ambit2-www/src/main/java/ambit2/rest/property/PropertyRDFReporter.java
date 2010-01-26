package ambit2.rest.property;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTClass;
import ambit2.rest.rdf.OT.OTProperty;
import ambit2.rest.reference.ReferenceRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Feature reporter
 * @author nina
 *
 * @param <Q>
 */
public class PropertyRDFReporter<Q extends IQueryRetrieval<Property>> extends QueryRDFReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected ReferenceURIReporter referenceReporter;
	public PropertyRDFReporter(Request request,MediaType mediaType) {
		super(request,mediaType);
		referenceReporter = new ReferenceURIReporter(request);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new PropertyURIReporter(reference);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		super.header(output, query);
		OT.OTClass.Feature.createOntClass(getJenaModel());
	
	}
	@Override
	public Object processItem(Property item) throws AmbitException {
		return addToModel(getJenaModel(), item, uriReporter,referenceReporter);

	}

	public static Individual addToModel(OntModel jenaModel,Property item, 
			QueryURIReporter<Property, IQueryRetrieval<Property>> uriReporter,
			ReferenceURIReporter referenceReporter
			) {
		Individual feature = null;
		OTClass featureType = OTClass.Feature;
		
		String id = uriReporter.getURI(item);
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null) || (item.getId()<0)) {
			if (item.getClazz() == Dictionary.class) {
				feature = jenaModel.createIndividual(id,featureType.getOntClass(jenaModel));
				feature.addLiteral(DC.identifier,
						jenaModel.createTypedLiteral(id,XSDDatatype.XSDanyURI));
			} else
				feature = jenaModel.createIndividual(featureType.getOntClass(jenaModel));
		} else {
			feature = jenaModel.createIndividual(id,featureType.getOntClass(jenaModel));
			feature.addLiteral(DC.identifier,
					jenaModel.createTypedLiteral(id,XSDDatatype.XSDanyURI));
		}
		if (item.isNominal())
			feature.addOntClass(OTClass.NominalFeature.getOntClass(jenaModel));
		
		if (item.getClazz()==Number.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Double.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Float.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Integer.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Long.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Dictionary.class) feature.addOntClass(OTClass.TupleFeature.getOntClass(jenaModel));
		
		feature.addProperty(DC.title, item.getName());
		feature.addProperty(OT.DataProperty.units.createProperty(jenaModel),item.getUnits());
		
		String uri = item.getLabel();
		if(uri==null) uri  = Property.guessLabel(item.getName());
		if ((uri!=null) && (uri.indexOf("http://")<0)) {
			uri = String.format("%s%s",OT.NS,Reference.encode(uri));
		}
		feature.addProperty(OWL.sameAs,jenaModel.createResource(uri));
		
		Individual reference = ReferenceRDFReporter.addToModel(jenaModel, item.getReference(), referenceReporter);
		feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), reference);
		
		//TODO remove, NumericFeature is used instead
		/*
		if (item.getClazz()!=null) {
			feature.addProperty(DC.type,
					 (item.getClazz()==Number.class)?
							AbstractPropertyRetrieval._PROPERTY_TYPE.NUMERIC.getXSDType():
							AbstractPropertyRetrieval._PROPERTY_TYPE.STRING.getXSDType()
							);
			

		}
		*/
		
		return feature;
	}	
	
	public void open() throws DbAmbitException {
		
	}

}
