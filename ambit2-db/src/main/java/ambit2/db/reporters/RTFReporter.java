package ambit2.db.reporters;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;

public class RTFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public RTFReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}

	
	@Override
	public void processItem(IStructureRecord item, Document document) {

		try {
			//TODO generate PDFcontent
			document.add(new Paragraph(item.getContent()));			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Document output, Q query) {};
	public void header(Document output, Q query) {};
}