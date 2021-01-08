package ambit2.rest.substance.study;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.config.Preferences;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.search.QueryExecutor;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.ReadSubstances;
import ambit2.db.substance.study.ReadSubstanceStudy;
import ambit2.db.substance.study.ReadSubstanceStudyFlat;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.study.jsonld.EffectRecordJsonldEntity;
import ambit2.rest.substance.study.jsonld.SubstanceStudyJsonldEntity;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;

public class SubstanceStudyResource<Q extends IQueryRetrieval<ProtocolApplication>> extends QueryResource<Q,ProtocolApplication> { 

	public final static String study = OpenTox.URI.study.getURI();
	public final static String idstudy = OpenTox.URI.study.getKey();
	public final static String studyID = OpenTox.URI.study.getResourceID();
	protected String substanceUUID;
	
	public SubstanceStudyResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "substancestudy.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else 
		*/
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			SubstanceStudyJSONReporter cmpreporter = new SubstanceStudyJSONReporter(getRequest(),jsonpcallback);
			return new OutputWriterConvertor<ProtocolApplication, Q>(
					cmpreporter,
					MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
			SubstanceStudyJSONReporter cmpreporter = new SubstanceStudyJSONReporter(getRequest(),null);
			return new OutputWriterConvertor<ProtocolApplication, Q>(
					cmpreporter,
					MediaType.APPLICATION_JSON,filenamePrefix);
		}
	}	
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object key = request.getAttributes().get(SubstanceResource.idsubstance);
		if (key==null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		} else {
			substanceUUID = key.toString();
			try {
				Form form = getRequest().getResourceRef().getQueryAsForm();
				String topCategory = form.getFirstValue("top");
				String category = form.getFirstValue("category");
				String property = form.getFirstValue("property");
				String property_uri = form.getFirstValue("property_uri");
				String document_uuid = form.getFirstValue("document_uuid");
				String investigation_uuid = form.getFirstValue("investigation");
				ReadSubstanceStudy q = new ReadSubstanceStudy();
				q.setFieldname(substanceUUID);
				if (topCategory!=null || category!=null || property != null || property_uri!=null || investigation_uuid!=null || document_uuid!=null) {
					Protocol p = new ambit2.base.data.study.Protocol("");
					p.setTopCategory(topCategory);
					p.setCategory(category);
					ProtocolApplication papp = new ProtocolApplication(p);
					papp.setDocumentUUID(document_uuid);
					papp.setInvestigationUUID(investigation_uuid);
					if (property_uri!=null) try {
						//not nice REST style, but easiest to parse the URI
						//not nice REST style, but easiest to parse the URI
						Reference puri = new Reference(property_uri.endsWith("/")?property_uri.substring(0, property_uri.length()-2):property_uri);
						//the very last segment denotes protocol, then study type, then one is the endpoint hash
						if (puri.getSegments().get(puri.getSegments().size()-1).indexOf("-") > 0) //this is the protocol
						    property=puri.getSegments().get(puri.getSegments().size()-3);
						else    
						    property=puri.getSegments().get(puri.getSegments().size()-2);
						if (property.length()!=40) property = null;
					} catch (Exception x) {}
					if (property!=null) {
						EffectRecord effect = new EffectRecord();
						effect.setSampleID(property);
						papp.addEffect(effect);
					}
					q.setValue(papp);
					
				}
				//q.setValue(new SubstanceRecord(Integer.parseInt(key.toString())));
				//q.setFieldname(relation);
				return (Q)q;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}

	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		
		map.put("jsonld", getJsonLd());
		map.put("substanceUUID", substanceUUID);
	}
	
	public String getJsonLd() {

		String jsonld = "";

		String datasetId = "";
		String citation = "The eNanoMapper database for nanomaterial safety information doi:10.3762/bjnano.6.165";
		String url = getRequest().getRootRef().toString();

		Map<String, EffectRecord<String, IParams, String>> effectsMap = new HashMap<String, EffectRecord<String, IParams, String>>();

		List<EffectRecordJsonldEntity> effectsList = new ArrayList<EffectRecordJsonldEntity>();

		Connection c = null;
		ResultSet rs = null;
		QueryExecutor executor = null;

		try {

			DBConnection dbc = new DBConnection(getContext());
			c = dbc.getConnection(30, true, 5);

			ReadSubstanceStudyFlat q = new ReadSubstanceStudyFlat();
			SubstanceRecord r = new SubstanceRecord();
			r.setSubstanceUUID(substanceUUID);
			q.setFieldname(r);
			q.setRecord(new ArrayList<ProtocolApplication>());

			executor = new QueryExecutor();
			executor.setConnection(c);
			executor.open();
			rs = executor.process(q);

			while (rs.next()) {

				List<ProtocolApplication> protocolApps = q.getObject(rs);

				for (ProtocolApplication<Protocol, IParams, String, IParams, String> m : protocolApps) {

					datasetId = m.getCompanyUUID();

					for (EffectRecord<String, IParams, String> effect : m.getEffects()) {
						effectsMap.put(effect.getEndpoint(), effect);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					executor.close();
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Map.Entry<String, EffectRecord<String, IParams, String>> entry : effectsMap.entrySet()) {

			String name = entry.getKey().toLowerCase();

			if (name.contains("core size")) {

				if(!effectsList.stream().anyMatch(o -> o.getName().equals("Size"))){
					effectsList.add(getEffectRecordEntity("Size", entry.getValue()));					
				}
				
			} else if (name.contains("specific surface area")) {

				if(!effectsList.stream().anyMatch(o -> o.getName().equals("Specific surface area"))){
					effectsList.add(getEffectRecordEntity("Specific surface area", entry.getValue()));					
				}
				
			} else if (name.contains("circularity") || name.contains("aspect ratio") || name.contains("roundness")) {

				if(!effectsList.stream().anyMatch(o -> o.getName().equals("Shape"))){
					effectsList.add(getEffectRecordEntity("Shape", entry.getValue()));					
				}

			} else if (name.contains("zeta potential")) {

				if(!effectsList.stream().anyMatch(o -> o.getName().equals("Zeta potential"))){
					effectsList.add(getEffectRecordEntity("Zeta potential", entry.getValue()));					
				}
			}
		}

		SubstanceStudyJsonldEntity studyEntity = new SubstanceStudyJsonldEntity();
		studyEntity.setDatasetId(datasetId);
		studyEntity.setCitation(citation);
		studyEntity.setUrl(url);
		studyEntity.setVariableMeasured(effectsList);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JsonldModule());
    	objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		try {
			jsonld = objectMapper.writeValueAsString(JsonldResource.Builder.create().build(studyEntity));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonld;
	}

	public EffectRecordJsonldEntity getEffectRecordEntity(String name, EffectRecord<String, IParams, String> effect) {

		EffectRecordJsonldEntity effectEntity = new EffectRecordJsonldEntity();
		effectEntity.setName(name);

		if (effect.getUnit() != null && !effect.getUnit().trim().equals("")) {
			effectEntity.setUnitText("reported");
		}

		return effectEntity;
	}
}
