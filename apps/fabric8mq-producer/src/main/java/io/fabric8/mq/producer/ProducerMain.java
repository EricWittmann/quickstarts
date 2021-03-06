/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.mq.producer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dataset.DataSet;
import org.apache.camel.component.dataset.SimpleDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

public class ProducerMain {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerMain.class);

    private static int interval;

    private static String queueName;

    private static int messageSize;

    private static long messageCount;

    public static void main(String args[]) {
        try {
            try {
                queueName = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        String result = System.getenv("AMQ_QUEUENAME");
                        result = (result == null || result.isEmpty()) ? System.getProperty("org.apache.activemq.AMQ_QUEUENAME", "TEST.FOO") : result;
                        return result;
                    }
                });

                String intervalStr = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        String result = System.getenv("AMQ_INTERVAL");
                        result = (result == null || result.isEmpty()) ? System.getProperty("org.apache.activemq.AMQ_INTERVAL", "0") : result;
                        return result;
                    }
                });
                if (intervalStr != null && intervalStr.length() > 0) {
                    interval = Integer.parseInt(intervalStr);
                }

                String messageSizeInBytesStr = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        String result = System.getenv("AMQ_MESSAGE_SIZE_BYTES");
                        result = (result == null || result.isEmpty()) ? System.getProperty("org.apache.activemq.AMQ_MESSAGE_SIZE_BYTES", "1024") : result;
                        return result;
                    }
                });
                if (messageSizeInBytesStr != null && messageSizeInBytesStr.length() > 0) {
                    messageSize = Integer.parseInt(messageSizeInBytesStr);
                }

                String messageCountStr = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        String result = System.getenv("AMQ_MESSAGE_COUNT_LONG");
                        result = (result == null || result.isEmpty()) ? System.getProperty("org.apache.activemq.AMQ_MESSAGE_COUNT_LONG", "10000") : result;
                        return result;
                    }
                });
                if (messageCountStr != null && messageCountStr.length() > 0) {
                    messageCount = Long.parseLong(messageCountStr);
                }

            } catch (Throwable e) {
                LOG.warn("Failed to look up System properties for host and port", e);
            }

            if (queueName == null) {
                queueName = "TEST.FOO";
            }

            if (messageSize <= 1) {
                messageSize = 1024;
            }

            if (messageCount <= 0) {
                messageCount = 10000;
            }


            // create a camel route to produce messages to our queue
            org.apache.camel.main.Main main = new org.apache.camel.main.Main();

            main.bind("myDataSet", createDataSet());

            main.enableHangupSupport();

            main.addRouteBuilder(new RouteBuilder() {
                public void configure() {
                    from("dataset:myDataSet?produceDelay=" + interval).to("amq:" + queueName);
                }
            });

            main.run(args);
        } catch (Throwable e) {
            LOG.error("Failed to connect to Fabric8 MQ", e);
        }
    }

    static DataSet createDataSet() {
        char[] chars = new char[messageSize];
        Arrays.fill(chars, 'a');

        String messageBody = new String(chars);

        SimpleDataSet dataSet = new SimpleDataSet();
        dataSet.setSize(messageCount);
        dataSet.setDefaultBody(messageBody);

        return dataSet;
    }

    public static int getInterval() {
        return interval;
    }

    public static String getQueueName() {
        return queueName;
    }

    public static int getMessageSize() {
        return messageSize;
    }

    public static long getMessageCount() {
        return messageCount;
    }
}
