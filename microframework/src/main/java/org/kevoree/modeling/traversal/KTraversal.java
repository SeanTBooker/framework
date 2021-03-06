package org.kevoree.modeling.traversal;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KView;
import org.kevoree.modeling.meta.KMetaAttribute;
import org.kevoree.modeling.meta.KMetaRelation;

public interface KTraversal {

    KTraversal traverse(KMetaRelation metaReference);

    KTraversal traverseQuery(String metaReferenceQuery);

    KTraversal attributeQuery(String attributeQuery);

    KTraversal withAttribute(KMetaAttribute attribute, Object expectedValue);

    KTraversal withoutAttribute(KMetaAttribute attribute, Object expectedValue);

    KTraversal filter(KTraversalFilter filter);

    void then(KCallback<KObject[]> cb);

    void eval(String expression, KCallback<Object[]> callback);

    void map(KMetaAttribute attribute, KCallback<Object[]> cb);

    KTraversal collect(KMetaRelation metaReference, KTraversalFilter continueCondition);

    KTraversal traverseTime(long timeOffset, long steps, KTraversalFilter continueCondition);

    KTraversal traverseUniverse(long universeOffset, KTraversalFilter continueCondition);

    KTraversal traverseIndex(String indexName, String attributes);

    void exec(KObject[] origins, KView view, KCallback<Object[]> callback);

}


