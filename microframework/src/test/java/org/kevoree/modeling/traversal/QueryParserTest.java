package org.kevoree.modeling.traversal;

/**
 * Created by duke on 04/02/15.
 */
public class QueryParserTest {

//
//    @Test
//    public void queryParseTest() {
//
//        List<Query> queryList = Query.buildChain("children[*]");
//        Assert.assertEquals(queryList.size(), 1);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, "*");
//
//        queryList = Query.buildChain("children");
//        Assert.assertEquals(queryList.size(), 1);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//
//        queryList = Query.buildChain("children/children");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, null);
//
//        queryList = Query.buildChain("children[*]/children");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, "*");
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, null);
//
//        queryList = Query.buildChain("children/children[*]");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, "*");
//
//        queryList = Query.buildChain("children[]/children[*]");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, "*");
//
//        queryList = Query.buildChain("children[*/children[*]");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, "*");
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, "*");
//
//        queryList = Query.buildChain("children[]/children[*");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, "*");
//
//        queryList = Query.buildChain("childr\\/en[]/children[*");
//        Assert.assertEquals(queryList.size(), 2);
//        Assert.assertEquals(queryList.getPrimitiveType(0).relationName, "childr\\/en");
//        Assert.assertEquals(queryList.getPrimitiveType(0).params, null);
//        Assert.assertEquals(queryList.getPrimitiveType(1).relationName, "children");
//        Assert.assertEquals(queryList.getPrimitiveType(1).params, "*");
//
//    }

}
