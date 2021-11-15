package com.looseboxes.websocket.server;

import com.looseboxes.websocket.server.config.WebsocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The base package class for this spring websocket server library.
 * 
 * <p>
 * To add this library to an existing spring boot project simply add it to 
 * the list of base package classes to scan by the {@code @SpringBootApplication}
 * annotation. 
 * </p>
 * <p>
 * For example, given an application class named {@code WebsocketServerApplication}
 * the library could be added as shown:
 * </p>
 * <pre>
 * <code>
 * @SpringBootApplication(scanBasePackageClasses = {WebsocketServerLibBasePackageClass.class, WebsocketServerApplication.class})
 * @EnableConfigurationProperties({WebsocketProperties.class})
 * public class WebsocketServerApplication{
 * 
 *     public static void main(String[] args) {
 *          SpringApplication.run(WebsocketDemoServerApplication.class, args);
 *     }
 * } 
 * </code>
 * </pre>
 * @author chinomso ikwuagwu
 */
@EnableConfigurationProperties({WebsocketProperties.class})
public class WebsocketServerLibBasePackageClass {

}
