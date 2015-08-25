package org.kevoree.modeling.drivers.websocket.test;

import org.junit.Assert;
import org.junit.Test;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KModel;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.KOperation;
import org.kevoree.modeling.cdn.KContentDeliveryDriver;
import org.kevoree.modeling.cdn.impl.MemoryContentDeliveryDriver;
import org.kevoree.modeling.drivers.websocket.WebSocketPeer;
import org.kevoree.modeling.drivers.websocket.gateway.WebSocketGateway;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.meta.KMetaClass;
import org.kevoree.modeling.meta.KMetaModel;
import org.kevoree.modeling.meta.KMetaOperation;
import org.kevoree.modeling.meta.KPrimitiveTypes;
import org.kevoree.modeling.meta.impl.MetaModel;
import org.kevoree.modeling.operation.OperationStrategies;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketPeerlTest {

    private static final int PORT = 9000;

    @Test
    public void test() {

        KContentDeliveryDriver memoryDriver = new MemoryContentDeliveryDriver();
        WebSocketGateway gateway = WebSocketGateway.expose(memoryDriver, PORT);
        gateway.start();

        KMetaModel dynamicMM = new MetaModel("mock");
        KMetaClass dynamicSensorClass = dynamicMM.addMetaClass("sensor");
        dynamicSensorClass.addAttribute("name", KPrimitiveTypes.STRING);
        dynamicSensorClass.addAttribute("value", KPrimitiveTypes.CONTINUOUS);
        KMetaOperation operationTrigger = dynamicSensorClass.addOperation("trigger");
        operationTrigger.setReturnType(KPrimitiveTypes.STRING);
        operationTrigger.addParam(KPrimitiveTypes.STRING);

        KModel model = dynamicMM.createModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketPeer("ws://localhost:" + PORT + "/testRoomId")).build());
        model.setOperation(operationTrigger, new KOperation() {
            @Override
            public void on(KObject source, Object[] params, KCallback result) {
                result.on("ThisIsARemoteResult");
            }
        });

        CountDownLatch latch = new CountDownLatch(1);

        model.connect(new KCallback() {
            @Override
            public void on(Object o) {
                KObject sensor = model.create(dynamicSensorClass, 0, 0);
                sensor.set(dynamicSensorClass.attribute("name"), "MyName");
                sensor.set(dynamicSensorClass.attribute("value"), "42.42");
                long sensorUUID = sensor.uuid();

                model.save(new KCallback() {
                    @Override
                    public void on(Object o) {

                        //Start a second model on this webSocket
                        KModel model2 = dynamicMM.createModel(DataManagerBuilder.create().withContentDeliveryDriver(new WebSocketPeer("ws://localhost:" + PORT + "/testRoomId")).build());
                        model2.connect(new KCallback() {
                            @Override
                            public void on(Object o) {
                                model2.lookup(0, 0, sensorUUID, new KCallback<KObject>() {
                                    @Override
                                    public void on(KObject kObject) {
                                        kObject.invokeOperationByName("trigger", new String[]{"hello"}, OperationStrategies.ONLY_ONE, new KCallback<String>() {
                                            @Override
                                            public void on(String operationResult) {

                                                Assert.assertEquals("ThisIsARemoteResult", operationResult);

                                                latch.countDown();
                                                Assert.assertEquals(kObject, sensor);
                                                Assert.assertEquals("{\"universe\":0,\"time\":0,\"uuid\":1,\"data\":{\"name\":\"MyName\",\"value\":[0.0,1.0,0.0,0.0,42.42]}}", sensor.toJSON());


                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });


        try {
            latch.await(4000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(latch.getCount(), 0);

        gateway.stop();

    }

}
