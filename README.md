##Software versions##
- Python 3.8.1
- Java 1.8

##Build and Start Servers##

### Prepare your Python Runtime Environment ###
`python3 -m pip install --upgrade pip`

`python3 -m pip install grpcio`

` python3 -m pip install grpcio-tools`


### Build the Python Server ###
`cd grpc-aiml-python`

`cd src/python`	

`python3 -m grpc_tools.protoc -I ../../src/protos --python_out=. --grpc_python_out=. ../../src/protos/mlpipeline.proto`

### Start the Python Servers exposing the gRPC endpoints ###

- Start the Feature Engineering Server (port 10001)
` python3 mlpipeline/featureengineering_server.py`

- Start another terminal
`cd grpc-aiml-python/src/python`

- In that terminal tart the ML Inference Server (port 10011)
` python3 mlpipeline/prediction_server.py`

### Build the Java Server ###
`cd grpc-aiml-java`

`mvn clean install`	


### Start the Java Servers exposing the gRPC endpoints ###

- Start the Feature Engineering Server (port 9001)
` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.FeatureEngineeringServer"`

- Start another terminal
`cd grpc-aiml-java`

- Start the ML Inference Server (port 9011)
` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.PredictionServer"`

## Java client invoking the Feature Engineering and ML Inference Servers##


There is only one client program. It is a Java Client. However it is possible to build Python clients. I will add examples later.

This demonstrates how inter-language communication works seamlessly. Results from a Java/Python program can be passed to Python/Java programs respectively.

### Client Program -> Java Feature Engineering Server & Java ML Inference Server" ###

`cd grpc-aiml-java`

` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.MLPipelineClient" -Dexec.args="localhost:9001 localhost:9011"`
`
### Client Program -> Java Feature Engineering Server & Python ML Inference Server" ###

`cd grpc-aiml-java`

` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.MLPipelineClient" -Dexec.args="localhost:9001 localhost:10011"`
`
### Client Program -> Python Feature Engineering Server & Python ML Inference Server" ###

`cd grpc-aiml-java`

` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.MLPipelineClient" -Dexec.args="localhost:10001 localhost:10011"`
`
### Client Program -> Python Feature Engineering Server & Java ML Inference Server" ###

`cd grpc-aiml-java`

` mvn exec:java -Dexec.mainClass="io.grpc.examples.ml.MLPipelineClient" -Dexec.args="localhost:10001 localhost:9011"`
`
# Streaming Engine To Do

1. Kafka platform which send raw events for a large number of devices which have 4 sensors each
2. State-management in Flink. Flink is responsible for managing a mult-hour hour window. As each new event comes it Flink stateful operator will verify if it is the most recent event (events can be late). If it is the most recent event, Flink Operator will remove events which are older than the window inteval. If it is not the most recent event Flink Operator will add the new event to the existing window state if it is within the current  window (wrt the most recent event in the window)
3. Flink will invoke the Feature Engineering and Inference pipeline for each event (high velocity) as well as every 5 minutes (to demonstrate the timer capability of Flink process functions)
4. Run Flink on Kubernetes

