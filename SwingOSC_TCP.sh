#!/bin/sh

where=`dirname $0`
cd ${where}
java -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel -Xmx512m -jar SwingOSC.jar -t 57111 -L -h 127.0.0.1:57120
