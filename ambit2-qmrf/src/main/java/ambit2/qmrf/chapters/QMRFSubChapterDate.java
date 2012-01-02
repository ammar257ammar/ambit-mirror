package ambit2.qmrf.chapters;

import ambit2.base.interfaces.IAmbitEditor;
import ambit2.qmrf.swing.QMRFSubChapterDateEditor;


public class QMRFSubChapterDate extends QMRFSubChapterText {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8909298588797560800L;
	
    public QMRFSubChapterDate(String elementID) {
        super(elementID);
        setMultiline(false);
    }   	
    @Override
    public IAmbitEditor editor(boolean editable) {
        return new QMRFSubChapterDateEditor(this,editable);
    }
}
