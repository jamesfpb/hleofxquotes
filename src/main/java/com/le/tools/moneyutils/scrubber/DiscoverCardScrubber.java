package com.le.tools.moneyutils.scrubber;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class DiscoverCardScrubber extends OfxScrubber {
    private static final Logger log = Logger.getLogger(DiscoverCardScrubber.class);

    public DiscoverCardScrubber(AbstractReplacer replacer) {
        super(replacer);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            Class clz = DiscoverCardScrubber.class;
            System.out.println("Usage: " + clz.getName() + " in.ofx scrubbed.ofx");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File outFile = new File(args[1]);

        log.info("inFile=" + inFile);
        log.info("outFile=" + outFile);

        AbstractReplacer replacer = null;
        replacer = new DiscoverCardReplacer();
        OfxScrubber scrubber = new OfxScrubber(replacer);
        try {
            scrubber.scrub(inFile, outFile);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("outFile=" + outFile);
            log.info("> DONE");
        }
    }

}
