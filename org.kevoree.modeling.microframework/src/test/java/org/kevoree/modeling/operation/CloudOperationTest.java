package org.kevoree.modeling.operation;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.KOperation;
import org.kevoree.modeling.cloudmodel.CloudUniverse;
import org.kevoree.modeling.cloudmodel.CloudModel;
import org.kevoree.modeling.cloudmodel.CloudView;
import org.kevoree.modeling.cloudmodel.Node;
import org.kevoree.modeling.cloudmodel.meta.MetaNode;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;

public class CloudOperationTest {

    public static void main(String[] args) {
        final CloudModel model = new CloudModel(DataManagerBuilder.buildDefault());
        model.connect(new KCallback<Throwable>() {
            @Override
            public void on(Throwable throwable) {
                CloudUniverse dimension = model.newUniverse();
                CloudView view = dimension.time(0L);
                Node n = view.createNode();

                model.setOperation(MetaNode.OP_TRIGGER, new KOperation<Node, String>() {
                    @Override
                    public void on(Node source, Object[] params, KCallback<String> result) {
                        String parameters = "[";
                        for (int i = 0; i < params.length; i++) {
                            if (i != 0) {
                                parameters = parameters + ", ";
                            }
                            parameters = parameters + params[i].toString();
                        }
                        parameters = parameters + "]";
                        result.on("Hey. I received Parameter:" + parameters + " on element:(" + source.universe() + "," + source.now() + "," + source.uuid() + ")");
                    }
                });

                n.trigger("MyParam", null, new KCallback<String>() {
                    @Override
                    public void on(String s) {
                        System.out.println("Operation execution result :  " + s);
                    }
                });
            }
        });

    }

}
