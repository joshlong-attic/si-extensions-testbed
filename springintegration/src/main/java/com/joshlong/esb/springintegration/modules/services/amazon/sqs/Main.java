/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

/**
 * AMazon SQS (simple queue service) is a powerful, REST-ful Message Queueu. You can use it with no fear of scalability
 * issues. To use it with Spring integration we need a scalable way of hittnig that REST endpoint and consuming messages
 * and then forwarding them onto the bus We will use Typica to do the hard work of talking to the service. Additionally,
 * we'll use only Amazon SQS2 ,not the older one pre-early 2008. The goals here are to a) build a inbound adapter that
 * can recieve SQS messages and b) send SQS messages to a given queue/topic whatever SQS2 supports.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class Main {
}
