package org.springframework.samples.petclinic.owner;



import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerController {

       Logger logger = LogManager.getLogger(LoggerController.class);


        @RequestMapping("/")
        public String index() {

            logger.info("info processing....");


            return "Howdy! Check out the Logs to see the output...";
        }

}
