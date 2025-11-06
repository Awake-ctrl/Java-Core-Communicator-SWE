package com.controller.RPCinterface;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public interface AbstractRPC {
    void subscribe(String methodName, Function<byte[], byte[]> method);
    Thread connect() throws IOException, InterruptedException, ExecutionException;
    CompletableFuture<byte[]> call(String methodName, byte[] data);
}
