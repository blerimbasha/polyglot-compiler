/*
 * SourceLoadException.java
 * 
 * Author: nystrom
 * Creation date: Dec 21, 2004
 */
package polyglot.types;

import polyglot.frontend.Job;
import polyglot.util.Position;

/**
 * Comment for <code>SourceLoadException</code>
 *
 * @author nystrom
 */
public class SourceLoadException extends NoClassException {
    Job job;
    
    /**
     * @param className
     */
    public SourceLoadException(Job job, String className) {
        super(className);
        this.job = job;
    }

    /**
     * @param className
     * @param scope
     */
    public SourceLoadException(Job job, String className, Named scope) {
        super(className, scope);
        this.job = job;
    }

    /**
     * @param className
     * @param position
     */
    public SourceLoadException(Job job, String className, Position position) {
        super(className, position);
        this.job = job;
    }
    
    public Job job() {
        return job;
    }
}
