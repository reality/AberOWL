/* 
 * Copyright 2014 Luke Slater (lus11@aber.ac.uk).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.aber.lus11.sparqowlapi.request;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.aber.lus11.sparqowlapi.util.NewShortFormProvider;

/**
 * Manchester OWL Syntax query manager for a particular reasoned ontology.
 * 
 * @author Luke Slater
 */
public class QueryEngine {
    private final OWLReasoner oReasoner;
    private final QueryParser parser;
    private final NewShortFormProvider sProvider;
    
    QueryEngine(OWLReasoner oReasoner, NewShortFormProvider sProvider) {
        this.oReasoner = oReasoner;
        this.sProvider = sProvider;
        this.parser = new QueryParser(oReasoner.getRootOntology(), sProvider);
    }
    
    /**
     * Return a set of classes relevant to a class description represented by a
     * Manchester OWL Syntax string. Returned classes can either be superclasses,
     * subclasses, equivalent classes or a combination of all relevant classes.
     * 
     * @param mOwl Manchester OWL Syntax query (form of a raw class description)
     * @param requestType Type of class to return.
     * @see RequestType
     * @return A HashSet of classes relevant to the given class description in 
     * mOwl corresponding to the type of request.
     */
    public Set<OWLClass> getClasses(String mOwl, RequestType requestType) {
        if(mOwl == null || mOwl.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression cExpression = parser.parse(mOwl);
        Set<OWLClass> classes = new HashSet<>();
  
        switch(requestType) {
	case SUPERCLASS:
	    classes.addAll(sClasses(cExpression)); break;
	case EQUIVALENT:
	    classes.addAll(eClasses(cExpression)); break;
	case SUBCLASS:
	    classes.addAll(subClasses(cExpression)); break;
	case SUBEQ:
	    classes.addAll(subClasses(cExpression)); break;
	    classes.addAll(eClasses(cExpression)); break;
	case SUPEQ:
	    classes.addAll(sClasses(cExpression)); break;
	    classes.addAll(eClasses(cExpression)); break;
	default: // default is a subeq query
	    classes.addAll(subClasses(cExpression));
	    classes.addAll(eClasses(cExpression)); break;
	    break;
        }
        return classes;
    }

    private Set<OWLClass> sClasses(OWLClassExpression cExpression) {
        return oReasoner.getSuperClasses(cExpression, false).getFlattened();
    }
    
    private Set<OWLClass> eClasses(OWLClassExpression cExpression) {
        Node<OWLClass> equivalentClasses = oReasoner.getEquivalentClasses(cExpression);
        Set<OWLClass> result;
        result = equivalentClasses.getEntities();
        return result;
    }
    
    private Set<OWLClass> subClasses(OWLClassExpression cExpression) {
        NodeSet<OWLClass> subClasses = oReasoner.getSubClasses(cExpression, false);
        return subClasses.getFlattened();
    }

    /**
     * @return the oReasoner
     */
    public OWLReasoner getoReasoner() {
        return oReasoner;
    }

    /**
     * @return the parser
     */
    public QueryParser getParser() {
        return parser;
    }

    /**
     * @return the sProvider
     */
    public NewShortFormProvider getsProvider() {
        return sProvider;
    }
}