package com.van1164.security.config

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver
import org.thymeleaf.templatemode.TemplateMode


@Configuration
@EnableWebFlux
class WebFluxConfig(
    private var ctx : ApplicationContext
) : ApplicationContextAware, WebFluxConfigurer{

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }
    @Bean
    fun thymeleafTemplateResolver(): SpringResourceTemplateResolver {
        val resolver = SpringResourceTemplateResolver()
        resolver.setApplicationContext(this.ctx)
        resolver.prefix = "classpath:/templates/"
        resolver.suffix = ".html"
        resolver.templateMode = TemplateMode.HTML
        resolver.isCacheable = false
        resolver.checkExistence = false
        return resolver
    }

    @Bean
    fun thymeleafTemplateEngine(): ISpringWebFluxTemplateEngine {
        val templateEngine = SpringWebFluxTemplateEngine()
        templateEngine.setTemplateResolver(thymeleafTemplateResolver())
        return templateEngine
    }

    @Bean
    fun thymeleafChunkedAndDataDrivenViewResolver(): ThymeleafReactiveViewResolver {
        val viewResolver = ThymeleafReactiveViewResolver()
        viewResolver.templateEngine = thymeleafTemplateEngine()
        viewResolver.responseMaxChunkSizeBytes = 8192
        return viewResolver
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.viewResolver(thymeleafChunkedAndDataDrivenViewResolver())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600)
    }


}