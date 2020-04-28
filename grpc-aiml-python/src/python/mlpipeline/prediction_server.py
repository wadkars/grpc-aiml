
from concurrent import futures
import logging

import grpc
import math
import mlpipeline_pb2
import mlpipeline_pb2_grpc


class PredictionServer(mlpipeline_pb2_grpc.PredictionServiceServicer):

    def predict(self, request, context):
        model_id = "log_reg_model/v2";

        device_id = request.device_id
        beta0 = 0.006
        beta1 = 0.03
        beta2 = 0.05
        beta3 = 0.01
        beta4 = 0.02
        logistic_regression_result = beta0
        feature_list = request.feature
        m = {}

        for f in feature_list :
            m[f.name] = f.value


        logistic_regression_result = logistic_regression_result + beta1 * m['sensor_1/v1'] + beta2 + m['sensor_2/v1']+ beta3 + m['sensor_3/v1'] + beta4 + m['sensor_4/v1'];

        logistic_regression_result = 1 / (1 + 1 * math.exp(-1 * logistic_regression_result));
        
        result = {}
        result['logistic_regression_result'] = str(logistic_regression_result)
        return mlpipeline_pb2.Prediction(result=result, modelId=model_id, deviceId=device_id)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    mlpipeline_pb2_grpc.add_PredictionServiceServicer_to_server(PredictionServer(), server)
    server.add_insecure_port('[::]:10011')
    print('Starting server on port 10011')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
