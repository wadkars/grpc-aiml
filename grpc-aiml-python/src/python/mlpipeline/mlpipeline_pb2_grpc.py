# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import mlpipeline_pb2 as mlpipeline__pb2


class FeatureEngineeringServiceStub(object):
    """The feature engineering service definition.
    """

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.engineerFeatures = channel.unary_unary(
                '/mlpipeline.FeatureEngineeringService/engineerFeatures',
                request_serializer=mlpipeline__pb2.RawEventList.SerializeToString,
                response_deserializer=mlpipeline__pb2.FeatureList.FromString,
                )


class FeatureEngineeringServiceServicer(object):
    """The feature engineering service definition.
    """

    def engineerFeatures(self, request, context):
        """Calculate engineered features
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_FeatureEngineeringServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'engineerFeatures': grpc.unary_unary_rpc_method_handler(
                    servicer.engineerFeatures,
                    request_deserializer=mlpipeline__pb2.RawEventList.FromString,
                    response_serializer=mlpipeline__pb2.FeatureList.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'mlpipeline.FeatureEngineeringService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class FeatureEngineeringService(object):
    """The feature engineering service definition.
    """

    @staticmethod
    def engineerFeatures(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/mlpipeline.FeatureEngineeringService/engineerFeatures',
            mlpipeline__pb2.RawEventList.SerializeToString,
            mlpipeline__pb2.FeatureList.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)


class PredictionServiceStub(object):
    """The feature engineering service definition.
    """

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.predict = channel.unary_unary(
                '/mlpipeline.PredictionService/predict',
                request_serializer=mlpipeline__pb2.FeatureList.SerializeToString,
                response_deserializer=mlpipeline__pb2.Prediction.FromString,
                )


class PredictionServiceServicer(object):
    """The feature engineering service definition.
    """

    def predict(self, request, context):
        """Inference Method
        """
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_PredictionServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'predict': grpc.unary_unary_rpc_method_handler(
                    servicer.predict,
                    request_deserializer=mlpipeline__pb2.FeatureList.FromString,
                    response_serializer=mlpipeline__pb2.Prediction.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'mlpipeline.PredictionService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class PredictionService(object):
    """The feature engineering service definition.
    """

    @staticmethod
    def predict(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/mlpipeline.PredictionService/predict',
            mlpipeline__pb2.FeatureList.SerializeToString,
            mlpipeline__pb2.Prediction.FromString,
            options, channel_credentials,
            call_credentials, compression, wait_for_ready, timeout, metadata)
