package activity.tracker

import org.springframework.boot.web.support.SpringBootServletInitializer

@org.springframework.boot.autoconfigure.SpringBootApplication
@org.springframework.context.annotation.PropertySource(value = "classpath:application.properties")
open class Application : SpringBootServletInitializer() {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            org.springframework.boot.SpringApplication.run(activity.tracker.Application::class.java, *args)
        }
    }
}