/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.envite.greenbpm.connector.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.client.model.EmissionsData;
import io.swagger.client.model.EmissionsDataDTO;
import org.junit.jupiter.api.Test;
import org.threeten.bp.OffsetDateTime;

class CarbonAwareSDKServiceTest {

  final CarbonAwareSDKService service = new CarbonAwareSDKService();

  @Test
  void getCurrentEmission() throws CarbonAwareSDKException {
    EmissionsData currentEmission = service.getCurrentEmission(Locations.GERMANY_WEST_CENTRAL);
    assertThat(currentEmission).isNotNull();
    assertThat(currentEmission.getLocation()).isEqualTo("DE");
    assertThat(currentEmission.getRating()).isPositive();
  }

  @Test
  void getOptimalForecastUntil() throws CarbonAwareSDKException {
    EmissionsDataDTO optimalForecastUntil =
        service.getOptimalForecastUntil(
            Locations.GERMANY_WEST_CENTRAL, OffsetDateTime.now().plusHours(6));
    assertThat(optimalForecastUntil).isNotNull();
  }
}
