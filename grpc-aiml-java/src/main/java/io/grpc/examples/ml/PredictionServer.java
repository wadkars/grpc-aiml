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
import io.grpc.examples.ml.Feature.Builder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Server that manages startup/shutdown of a {@code PredictionService} server.
 */
public class PredictionServer {
  private static final Logger logger = Logger.getLogger(PredictionServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 9011;
    server = ServerBuilder.forPort(port)
        .addService(new PredictionServiceImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
        	PredictionServer.this.stop();
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
    final PredictionServer server = new PredictionServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class PredictionServiceImpl extends PredictionServiceGrpc.PredictionServiceImplBase {

    @Override
    public void predict(FeatureList req, StreamObserver<Prediction> responseObserver) {
    
      String modelId = "log_reg_model/v1";
      String deviceId = req.getDeviceId();
      
      float beta0=0.006f;
      float beta1=0.03f;
      float beta2=0.05f;
      float beta3=0.01f;
      float beta4=0.02f;
      
      double logisticRegressionResult = beta0;
      
      List<Feature> featureLst = req.getFeatureList();
      Map<String,Float> m = new HashMap<>();
     
      for(Feature f:featureLst) {
    	  m.put(f.getName(),f.getValue());
      }
      
      logisticRegressionResult = logisticRegressionResult + beta1 * m.get("sensor_1/v1") 
                                                          + beta2 + m.get("sensor_2/v1")
                                                          + beta3 + m.get("sensor_3/v1")
                                                          + beta4 + m.get("sensor_4/v1");
      logisticRegressionResult = 1/(1  + 1*Math.exp(-1*logisticRegressionResult));
      
      Map<String,String> result = new HashMap<>();
      result.put("logistic_regression_result", Double.toString(logisticRegressionResult));
      
      Prediction reply = Prediction.newBuilder().setDeviceId(deviceId)
    		                                    .setModelId(modelId).putAllResult(result).build();
      
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
    

  }
}
