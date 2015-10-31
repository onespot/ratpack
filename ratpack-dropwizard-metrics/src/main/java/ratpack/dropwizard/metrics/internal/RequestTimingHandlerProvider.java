/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.dropwizard.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import ratpack.dropwizard.metrics.DropwizardMetricsConfig;
import ratpack.dropwizard.metrics.RequestTimingHandler;
import ratpack.dropwizard.metrics.RequestTimingHandlerConfig;
import ratpack.handling.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

/**
 * Provide an instance of a request timing handler.
 */
public class RequestTimingHandlerProvider implements Provider<RequestTimingHandler> {

    private final MetricRegistry metricRegistry;
    private final DropwizardMetricsConfig config;

    @Inject
    public RequestTimingHandlerProvider(MetricRegistry metricRegistry, DropwizardMetricsConfig config) {
        this.metricRegistry = metricRegistry;
        this.config = config;
    }

    @Override
    public RequestTimingHandler get() {
        RequestTimingHandler handler = new RequestTimingHandler(metricRegistry, config) {
            @Override
            public void handle(Context ctx) throws Exception {
                ctx.next();
            }
        };
        Optional<RequestTimingHandlerConfig> o = config.getHandler();
        if (o.isPresent()) {
            RequestTimingHandlerConfig handlerConfig = o.get();
            if (handlerConfig.isEnabled()) {
                handler = handlerConfig.getHandler().map(clazz -> {
                    try {
                        return clazz.getConstructor(MetricRegistry.class, DropwizardMetricsConfig.class).newInstance(metricRegistry, config);
                    } catch (Exception e) {
                        return null;
                    }
                }).orElse(new DefaultRequestTimingHandler(metricRegistry, config));
            }
        }
        return handler;
    }
}
