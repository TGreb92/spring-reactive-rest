package com.tgreb.skeleton.springreactiverestmongo.config;

import com.tgreb.skeleton.springreactiverestmongo.controller.PeopleController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutingConfig implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> petShopRouter(final PeopleController peopleController){
        return route(path("/favicon.ico"), request -> ServerResponse.ok().build())
                .andNest(path(""), route()
                        .nest(path("/people"), () -> peopleRoutes(peopleController))
                        .build());
    }

    private RouterFunction<ServerResponse> peopleRoutes(final PeopleController peopleController){
        return route()
                .GET("", peopleController::findAll)
                .GET("/{id}", peopleController::findById)
                .POST("", peopleController::create)
                .PATCH("/{id}", peopleController::update)
                .DELETE("/{id}", peopleController::deleteById)
                .build();
    }



    private static final String[] METHODS = {"GET"};
    private static final String[] HEADERS = {"x-pagination", "X-Pagination", "authorization", "Authorization",
            "content-type", "Content-Type"};


    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/v1/**")
                .allowedOrigins("*")
                .allowedMethods(METHODS)
                .allowedHeaders(HEADERS)
                .exposedHeaders(HEADERS);
    }
}
