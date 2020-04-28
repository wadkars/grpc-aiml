/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.ml;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Server that manages startup/shutdown of a {@code FeatureEngineeringService} server.
 */
public class FeatureEngineeringServer {
  private static final Logger logger = Logger.getLogger(FeatureEngineeringServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 9001;
    server = ServerBuilder.forPort(port)
        .addService(new FeatureEngineeringServiceImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
        	FeatureEngineeringServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final FeatureEngineeringServer server = new FeatureEngineeringServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class FeatureEngineeringServiceImpl extends FeatureEngineeringServiceGrpc.FeatureEngineeringServiceImplBase {

    @Override
    public void engineerFeatures(RawEventList req, StreamObserver<FeatureList> responseObserver) {
    	
      String deviceId = req.getDeviceId();
      List<RawEvent> rawEventList = req.getEventList();
      Map<String,Float> m = new HashMap<>();
     
      for(RawEvent e:rawEventList) {
    	  m.put("sensor_1", m.getOrDefault("sensor_1", 0f) + e.getSensor1());
    	  m.put("sensor_2", m.getOrDefault("sensor_2", 0f) + e.getSensor2());
    	  m.put("sensor_3", m.getOrDefault("sensor_3", 0f) + e.getSensor3());
    	  m.put("sensor_4", m.getOrDefault("sensor_4", 0f) + e.getSensor4());
      }
	  m.put("sensor_1", m.getOrDefault("sensor_1", 0f)/Math.max(rawEventList.size(),1));
	  m.put("sensor_2", m.getOrDefault("sensor_2", 0f) /Math.max(rawEventList.size(),1));
	  m.put("sensor_3", m.getOrDefault("sensor_3", 0f) /Math.max(rawEventList.size(),1));
	  m.put("sensor_4", m.getOrDefault("sensor_4", 0f) /Math.max(rawEventList.size(),1));
      Feature sensor1 = Feature.newBuilder().setName("sensor_1/v1").setValue(m.get("sensor_1")).build();
      Feature sensor2 = Feature.newBuilder().setName("sensor_2/v1").setValue(m.get("sensor_2")).build();
      Feature sensor3 = Feature.newBuilder().setName("sensor_3/v1").setValue(m.get("sensor_3")).build();
      Feature sensor4 = Feature.newBuilder().setName("sensor_4/v1").setValue(m.get("sensor_4")).build();
      
      FeatureList reply = FeatureList.newBuilder().setDeviceId(deviceId)
    		                                      .addFeature(sensor1)
    		                                      .addFeature(sensor2)
    		                                      .addFeature(sensor3)
    		                                      .addFeature(sensor4).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
    

  }
}
