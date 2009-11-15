package ambit2.base.processors;

import ambit2.base.exceptions.AmbitException;

/**
 * Implementation of {@link Reporter}
 * @author nina
 *
 * @param <Content>
 * @param <Output>
 */
public abstract class AbstractReporter<Content, Output> extends DefaultAmbitProcessor<Content,Output> implements Reporter<Content, Output> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4876342393001789437L;
	protected Output output;
	protected long timeout = 0;

	public Output getOutput() throws AmbitException {
		return output;
	}
	public void setOutput(Output output) throws AmbitException {
		this.output = output;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
		
	}
	


}
