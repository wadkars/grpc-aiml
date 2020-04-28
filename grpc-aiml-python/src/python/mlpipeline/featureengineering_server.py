
from concurrent import futures
import logging

import grpc
import math
import mlpipeline_pb2
import mlpipeline_pb2_grpc


class FeatureEngineeringServer(mlpipeline_pb2_grpc.FeatureEngineeringServiceServicer):

    def engineerFeatures(self, request, context):
        device_id = request.device_id
        event_list = request.event
        m = {'sensor_1': 0.0, 'sensor_2':0.0, 'sensor_3': 0.0, 'sensor_4': 0.0}
        for e in event_list:
            m['sensor_1'] = m['sensor_1'] + e.sensor1
            m['sensor_2'] = m['sensor_2'] + e.sensor2
            m['sensor_3'] = m['sensor_3'] + e.sensor3
            m['sensor_4'] = m['sensor_4'] + e.sensor4

        feature_1 = mlpipeline_pb2.Feature(name='sensor_1/v1', value=m['sensor_1']/max(len(event_list), 1))
        feature_2 = mlpipeline_pb2.Feature(name='sensor_2/v1', value=m['sensor_2']/max(len(event_list), 1))
        feature_3 = mlpipeline_pb2.Feature(name='sensor_3/v1', value=m['sensor_3']/max(len(event_list), 1))
        feature_4 = mlpipeline_pb2.Feature(name='sensor_4/v1', value=m['sensor_4']/max(len(event_list), 1))

        feature_list = [feature_1, feature_2, feature_3, feature_4]
        return mlpipeline_pb2.FeatureList(device_id=device_id, feature=feature_list)



def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    mlpipeline_pb2_grpc.add_FeatureEngineeringServiceServicer_to_server(FeatureEngineeringServer(), server)
    server.add_insecure_port('[::]:10001')
    print('Starting server on port 10001')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
