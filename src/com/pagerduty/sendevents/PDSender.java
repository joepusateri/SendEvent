package com.pagerduty.sendevents;

import java.time.OffsetDateTime;
import java.util.Map;

import org.json.JSONObject;

import com.github.dikhan.pagerduty.client.events.PagerDutyEventsClient;
import com.github.dikhan.pagerduty.client.events.domain.Payload;
import com.github.dikhan.pagerduty.client.events.domain.Severity;
import com.github.dikhan.pagerduty.client.events.domain.TriggerIncident;
import com.github.dikhan.pagerduty.client.events.exceptions.NotifyEventException;

public class PDSender {

	public static void main(String[] args) {
		
		// Do this once for the session
		PagerDutyEventsClient pagerDutyEventsClient = PagerDutyEventsClient.create();

		// Create or obtain the JSON for any (optional) "Custom details" you want in the payload. For example:
		Map<String, String> customDetails = Map.of(
				"Description", "Cisco IOS Software, C2960 Software (C2960-LANLITEK9-M), Version 12.2(55)SE11", 
				"Application Name","SolarWinds.Core.Common", 
				"Node Availability", "Node status is Down.", 
				"Node","atl14adoob-01.internal.secureworks.net");		
		JSONObject jsonCustomDetails = new JSONObject(customDetails);
				
		// Create the payload, setting required fields (summary, source and severity) and others if desired
		Payload payload = Payload.Builder.newBuilder()
				.setSummary("atl14adoob-01.internal.secureworks.net is down.") 	//title of incident - required
				.setSource("atl14adoob-01.internal.secureworks.net")			//host name or other source - required
				.setSeverity(Severity.CRITICAL)									//severity - required
				.setComponent("Cisco Catalyst 2960-48TC-S")						//component - optional
				.setTimestamp(OffsetDateTime.now())								//time of event - optional
				.setCustomDetails(jsonCustomDetails)							//see above - optional
				.build();

		// create the trigger with that payload, directing it to a specific routing (a.k.a. integration) key
		TriggerIncident incident = TriggerIncident.TriggerIncidentBuilder
				.newBuilder("<your-routing-key>", 	//Routing key to the service
						payload)
				.setDedupKey("uniqueGuid") 		//optional - set to indicate a unique id for duplicate suppression
				.build();
		try {
			// Send it to PagerDuty
			pagerDutyEventsClient.trigger(incident);
		} catch (NotifyEventException e) {
			e.printStackTrace();
		}

	}

}
