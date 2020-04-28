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

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import io.grpc.utils.MLUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class MLPipelineClient {
  private static final Logger logger = Logger.getLogger(MLPipelineClient.class.getName());

  private final FeatureEngineeringServiceGrpc.FeatureEngineeringServiceBlockingStub featureEngineeringBlockingStub;
  private final PredictionServiceGrpc.PredictionServiceBlockingStub predictionBlockingStub;
  // Access a service running on the local machine on port 50051
  private static String featureEngineeringServiceTarget = "localhost:10001";
  private static String predictonServiceTarget = "localhost:10011";
  /** Construct client for accessing HelloWorld server using the existing channel. */
  public MLPipelineClient(Channel channel1, Channel channel2) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    featureEngineeringBlockingStub = FeatureEngineeringServiceGrpc.newBlockingStub(channel1);
    predictionBlockingStub = PredictionServiceGrpc.newBlockingStub(channel2);
  }

  //Date and time (GMT): Sunday, April 26, 2020 5:02:35 PM
  public static long referenceTime = 1587920555000l;
  public static final String deviceId = "device_123";
  public static RawEventList buildRawEventList() {
	  Random rnd = new Random(1);

	  RawEvent evt = null;
	  List<RawEvent> eventList = new ArrayList<>();
	  for(int i =0;i<300;i++) {
		  evt = RawEvent.newBuilder().setDeviceId(deviceId)
				                     .setTime(referenceTime + i * 2 * 60 * 1000)
                                     .setSensor1(rnd.nextFloat() * 1)
                                     .setSensor2(rnd.nextFloat() * 1)
                                     .setSensor3(rnd.nextFloat() * 1)
                                     .setSensor4(rnd.nextFloat() * 1)
                                     .build();	  
		  eventList.add(evt);
	  }
	  RawEventList rawEventList = RawEventList.newBuilder().setDeviceId(deviceId).addAllEvent(eventList).build();
	  return rawEventList;
  }
  

  
  public FeatureList getResponse(RawEventList evtList) {
	    FeatureList response = null;
	    try {
	      response = featureEngineeringBlockingStub.engineerFeatures(evtList);
	    } catch (StatusRuntimeException e) {
	      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
	    }
	    return response;
  }
  
  /** Engineer Features */
  public FeatureList doFeatureEngineering(RawEventList eventList) {
    logger.info("Engineer Features " + " invoking target " + this.featureEngineeringServiceTarget );
    
    FeatureList response  = this.getResponse(eventList);
    List<Feature> features = response.getFeatureList();
    Map<String,Float> featuresMap = new HashMap<>();
    for(Feature f:features) {
    	featuresMap.put(f.getName(), f.getValue());
    }
    
    logger.info("Feature List " + MLUtils.getJson(featuresMap));
    return response;
  }

  
  public Prediction invokePredictionPipeline(RawEventList eventList) {
	  logger.info("Call Inference Engine " + " invoking target " + this.predictonServiceTarget );
	    
	  FeatureList featureList  =  this.doFeatureEngineering(eventList);
	  Prediction prediction = null;
	  try {
		  prediction = predictionBlockingStub.predict(featureList);
	   } catch (StatusRuntimeException e) {
		      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
	  }
	  return prediction;
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting. The second argument is the target server.
   */
  public static void main(String[] args) throws Exception {
 
    // Allow passing in the user and target strings as command line arguments
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [target1] [target2] ");
        System.err.println("");
        System.err.println("  target1  The server to connect to. Defaults to " + featureEngineeringServiceTarget);
        System.err.println("  target2  The server to connect to. Defaults to " +     predictonServiceTarget);
        System.exit(1);
      }
     
    }
    if (args.length > 0) {
      featureEngineeringServiceTarget = args[0];
      predictonServiceTarget = args[1];   
    }

    logger.info("Feature Engineering Server Endpoint :" + featureEngineeringServiceTarget);
    logger.info("ML Inference Server Endpoint :" + predictonServiceTarget);
    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel1 = ManagedChannelBuilder.forTarget(featureEngineeringServiceTarget)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
    
    ManagedChannel channel2 = ManagedChannelBuilder.forTarget(predictonServiceTarget)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
    try {
      MLPipelineClient client = new MLPipelineClient(channel1,channel2);
      Prediction prediction = client.invokePredictionPipeline(MLPipelineClient.buildRawEventList());
      String deviceId = prediction.getDeviceId();
      String modelId = prediction.getModelId();
      Map<String,String> result = prediction.getResultMap();
      logger.info("Device Id: " + deviceId);
      logger.info("Model Id: " + modelId);
      logger.info("Result : " + MLUtils.getJson(result));
      
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel1.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      channel2.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
