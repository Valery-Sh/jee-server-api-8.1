package org.netbeans.modules.jeeserver.base.deployment.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlErrors {
    
    private boolean deferredException;

    private final List<XmlError> errorList = new ArrayList();
    
    public boolean isDeferredException() {
        return deferredException;
    }

    public void setDeferredException(boolean deferredException) {
        this.deferredException = deferredException;
    }

    public List<XmlError> getErrorList() {
        return new ArrayList(errorList);
    }
    
    public void addError(XmlError error) {
        errorList.add(error);
    }
    public void merge(XmlErrors other ) {
        this.errorList.addAll(other.getErrorList());
    }
    public boolean isEmpty() {
        return errorList.isEmpty();
    }
    
    public int size() {
        return errorList.size();
    }
    public void clear() {
        this.errorList.clear();
    }
    
    public static class XmlError {

        private final String message;
        private final RuntimeException exception;
        private final List<XmlElement> parentChainList;

        public XmlError(String message, RuntimeException exception, List<XmlElement> parentChainList) {
            this.message = message;
            this.exception = exception;
            this.parentChainList = new ArrayList(parentChainList);
        }

        public String getMessage() {
            return message;
        }

        public RuntimeException getException() {
            return exception;
        }

        public List<XmlElement> getParentChainList() {
            return parentChainList;
        }
    }//class XmlError
    public static class InvalidClassNameException extends RuntimeException {

        public InvalidClassNameException(String message) {
            super(message);
        }
        
    }
    public static class InvalidTagNameException extends RuntimeException {

        public InvalidTagNameException(String message) {
            super(message);
        }
        
    }    
}
