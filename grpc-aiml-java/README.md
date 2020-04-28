# Examples

- Feature Engineering gRPC server which takes a 10 hour window of raw events sampled at 2 minute interval. Each event has measurements for 4 sensors. Features returned are averages for each sensor measurements.
- A Prediction Server which applies a logistic regression algorithm with those 4 averages are feature values. 
- ML Client which emualates a Streaming Engine. It generates raw events for a 10 hour window sampled every 2 minutes. Then it invokes the feature engineering gRPC endpoint to get engineered features and passes these engineered features to the gRPC ML Inference Endpoint to generate inferences


# Goal

The goal is to expose any Feature Engineering and ML Inference Engine as a gRPC endpoint.

# Server-side To DO


Currently communication is over an insecure channel. We need to as the following capabilities :
0. Support compression as windows in streaming platforms can be quite long
1. Use TLS
2. Use oAuth
3. Apply load-balancing with a cluster of gRPC servers
4. Containerize









