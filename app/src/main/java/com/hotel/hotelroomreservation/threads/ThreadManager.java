package com.hotel.hotelroomreservation.threads;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    public static final int THREADS = 4;
    private final ExecutorService executorService;

    public ThreadManager() {
       this(Executors.newFixedThreadPool(THREADS));
    }

    public ThreadManager(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <Params, Progress, Result> void executeOperation(final ExecutingOperations<Params, Progress, Result> operations,
                                                            final Params params, final OnResultCallback<Progress, Result> onResultCallback) {
        final Handler handler = new Handler();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final Result result = operations.executing(params, new OnProgressCallback<Progress>() {

                        @Override
                        public void onProgressChanged(final Progress progress) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultCallback.onProgressChanged(progress);
                                }
                            });
                        }
                    });

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            onResultCallback.onSuccess(result);
                        }
                    });
                } catch (Exception e) {
                    onResultCallback.onError(e);
                }
            }
        });
    }

}