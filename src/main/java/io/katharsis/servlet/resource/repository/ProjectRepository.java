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
package io.katharsis.servlet.resource.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import io.katharsis.queryParams.QueryParams;
import io.katharsis.repository.annotations.JsonApiDelete;
import io.katharsis.repository.annotations.JsonApiFindAll;
import io.katharsis.repository.annotations.JsonApiFindAllWithIds;
import io.katharsis.repository.annotations.JsonApiFindOne;
import io.katharsis.repository.annotations.JsonApiResourceRepository;
import io.katharsis.repository.annotations.JsonApiSave;
import io.katharsis.resource.exception.ResourceNotFoundException;
import io.katharsis.servlet.resource.model.Project;

@JsonApiResourceRepository(Project.class)
public class ProjectRepository {
	public ProjectRepository() {
		init();
	}

	private void init() {
		for (int i = 0; i < 10; i++) {
			Project project = new Project();
			project.setId((long) i);
			project.setName("Project " + i);
			REPOSITORY.put((long) i, project);
		}
	}

	private static final Map<Long, Project> REPOSITORY = new ConcurrentHashMap<>();
	private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

	@JsonApiSave
	public <S extends Project> S save(S entity) {
		if (entity.getId() == null) {
			entity.setId(ID_GENERATOR.getAndIncrement());
		}
		REPOSITORY.put(entity.getId(), entity);
		return entity;
	}

	@JsonApiFindOne
	public Project findOne(Long id) {
		Project project = REPOSITORY.get(id);
		if (project == null) {
			throw new ResourceNotFoundException("Project not found");
		}
		return project;
	}

	@JsonApiFindAll
	public Iterable<Project> findAll(QueryParams queryParams) {
		return REPOSITORY.values();
	}

	@JsonApiFindAllWithIds
	public Iterable<Project> findAll(Iterable<Long> iterable, QueryParams queryParams) {
		return REPOSITORY.entrySet().stream().filter(p -> Iterables.contains(iterable, p.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).values();
	}

	@JsonApiDelete
	public void delete(Long id) {
		REPOSITORY.remove(id);
	}
}
