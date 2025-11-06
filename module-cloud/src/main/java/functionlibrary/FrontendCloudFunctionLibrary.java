package functionlibrary;

import com.controller.RPCinterface.AbstractRPC;
import com.fasterxml.jackson.databind.ObjectMapper;
import datastructures.Entity;
import datastructures.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class CloudFunctionLibrary {

    private final AbstractRPC rpc;
    private final ObjectMapper objectMapper;

    /** Constructor accepts the initialized RPC from the Controller module */
    public CloudFunctionLibrary(AbstractRPC rpcInstance) {
        this.rpc = rpcInstance;
        this.objectMapper = new ObjectMapper();
    }

    // Common internal function to call backend through RPC
    private CompletableFuture<Response> callThroughRPC(String method, Entity request) {
        try {
            byte[] requestBytes = objectMapper.writeValueAsBytes(request);
            CompletableFuture<byte[]> rpcResponse = rpc.call(method, requestBytes);

            return rpcResponse.thenApply(responseBytes -> {
                try {
                    return objectMapper.readValue(responseBytes, Response.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    Response error = new Response();
                    error.setStatus("FAILED");
                    error.setMessage("Deserialization error: " + e.getMessage());
                    return error;
                }
            });
        } catch (Exception e) {
            CompletableFuture<Response> failed = new CompletableFuture<>();
            Response error = new Response();
            error.setStatus("FAILED");
            error.setMessage(e.getMessage());
            failed.complete(error);
            return failed;
        }
    }

    // ---------------------------------------------
    // ✅ Public RPC methods
    // ---------------------------------------------

    public CompletableFuture<Response> cloudCreate(final Entity request) {
        return callThroughRPC("cloudCreate", request);
    }

    public CompletableFuture<Response> cloudDelete(final Entity request) {
        return callThroughRPC("cloudDelete", request);
    }

    public CompletableFuture<Response> cloudGet(final Entity request) {
        return callThroughRPC("cloudGet", request);
    }

    public CompletableFuture<Response> cloudPost(final Entity request) {
        return callThroughRPC("cloudPost", request);
    }

    public CompletableFuture<Response> cloudUpdate(final Entity request) {
        return callThroughRPC("cloudUpdate", request);
    }
}
