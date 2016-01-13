package com.sic.dev.main;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.dispatcher.RequestDispatcher;
import io.katharsis.dispatcher.registry.ControllerRegistry;
import io.katharsis.dispatcher.registry.ControllerRegistryBuilder;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistryBuilder;
import io.katharsis.jackson.JsonApiModuleBuilder;
import io.katharsis.locator.SampleJsonServiceLocator;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathBuilder;
import io.katharsis.resource.field.ResourceFieldNameTransformer;
import io.katharsis.resource.information.ResourceInformationBuilder;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ResourceRegistryBuilder;
import io.katharsis.response.BaseResponse;
import io.katharsis.servlet.resource.model.Project;
import io.katharsis.utils.parser.TypeParser;

public class Hello {
	public static final String ResourcePackage = "io.katharsis.servlet.resource";
	public static final String DomainName = "http://localhost:8080";
	public static final String PpathPrefix = "/api";

	public static void main(String[] args) throws Exception {

		String path = "/projects/";
		String requestType = "GET";

		ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(new SampleJsonServiceLocator(),
				new ResourceInformationBuilder(new ResourceFieldNameTransformer()));

		ResourceRegistry resourceRegistry = registryBuilder.build(ResourcePackage, DomainName);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule((new JsonApiModuleBuilder()).build(resourceRegistry));

		TypeParser typeParser = new TypeParser();
		ControllerRegistryBuilder controllerRegistryBuilder = new ControllerRegistryBuilder(resourceRegistry,
				typeParser, objectMapper);
		ControllerRegistry controllerRegistry = controllerRegistryBuilder.build();

		RequestDispatcher dispatcher = new RequestDispatcher(controllerRegistry,
				(new ExceptionMapperRegistryBuilder()).build(ResourcePackage));

		PathBuilder pathBuilder = new PathBuilder(resourceRegistry);
		JsonPath jsonPath = pathBuilder.buildPath(path);

		System.out.println(jsonPath);
		
		BaseResponse<?> baseResponse = dispatcher.dispatchRequest(jsonPath, requestType, new QueryParams(), null, null);
		
		for(Project project: (Iterable<Project>)  baseResponse.getData()){
			System.out.println(project);
		}

	}
}
