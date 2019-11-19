/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.s3;

import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;

public class S3ComponentGetObjectRangeTest extends CamelTestSupport {

	@BindToRegistry("amazonS3Client")
	AmazonS3 clientMock = new AmazonS3ClientMock();

	@EndpointInject
	private ProducerTemplate template;

	@EndpointInject("mock:result")
	private MockEndpoint result;

	@Test
	public void sendIn() throws Exception {
		result.expectedMessageCount(1);

		template.send("direct:getObjectRange", new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeader(S3Constants.KEY, "pippo.txt");
				exchange.getIn().setHeader(S3Constants.RANGE_START, 0);
				exchange.getIn().setHeader(S3Constants.RANGE_END, 9);
			}
		});
		assertMockEndpointsSatisfied();

	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				String awsEndpoint = "aws-s3://mycamelbucket?amazonS3Client=#amazonS3Client&operation=getObjectRange";

				from("direct:getObjectRange").to(awsEndpoint).to("mock:result");

			}
		};
	}
}