package org.openmrs.module.sync2.api.sync;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.fhir.api.client.ClientHttpEntity;
import org.openmrs.module.fhir.api.client.ClientHttpRequestInterceptor;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.InnerRequest;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.ClientHelperFactory;
import org.openmrs.module.sync2.client.RequestWrapperConverter;
import org.openmrs.module.sync2.client.rest.RESTClientHelper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_DELETED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_RETIRED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_REST_ENDPOINT;
import static org.openmrs.module.sync2.SyncConstants.WS_REST_V1;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getLocalBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getParentBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getSyncConfigurationService;

public class SyncClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncClient.class);

	private String username;

	private String password;

	private RestTemplate restTemplate = new RestTemplate();

	public Object pullData(SyncCategory category, String clientName, String resourceUrl, OpenMRSSyncInstance instance) {
		Object result = null;
		setUpCredentials(clientName, instance);

		ClientHelper clientHelper = ClientHelperFactory.createClient(clientName);
		prepareRestTemplate(clientHelper);
		String destinationUrl = getDestinationUri(instance, clientName);

		try {
			result = retrieveObject(category, resourceUrl, destinationUrl, clientName, instance);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
				throw new SyncException("Unauthorized error during reading parent object: ", e);
			}
			if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new SyncException("Error during reading local object: ", e);
			}
		}
		catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	public ResponseEntity<String> pushData(SyncCategory category, Object object, String clientName,
			String resourceUrl, String action, OpenMRSSyncInstance instance) throws TemporaryDataIntegrityException {
		ResponseEntity<String> result = null;
		setUpCredentials(clientName, instance);
		String destinationUrl = getDestinationUri(instance, clientName);
		ClientHelper clientHelper = ClientHelperFactory.createClient(clientName);
		prepareRestTemplate(clientHelper);

		try {
			switch (action) {
				case ACTION_CREATED:
					result = createObject(category, resourceUrl, destinationUrl, object, clientName, instance);
					break;
				case ACTION_UPDATED:
					result = updateObject(category, resourceUrl, destinationUrl, object, clientName, instance);
					break;
				case ACTION_VOIDED:
				case ACTION_DELETED:
				case ACTION_RETIRED:
					result = deleteObject(category, resourceUrl, destinationUrl, (String) object, clientName, instance);
					break;
				default:
					LOGGER.warn(String.format("Sync push exception. Unrecognized action: %s", action));
					break;
			}
		}
		catch (HttpClientErrorException | HttpServerErrorException e) {
			if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new SyncException(String.format("Object posting error. Code: %d. Details: \n%s",
						e.getStatusCode().value(), e.getResponseBodyAsString()), e);
			}
			LOGGER.error(e.getMessage(), e);
		}
		catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	private void setUpCredentials(String clientName, OpenMRSSyncInstance instance) {
		this.username = SyncUtils.getClientLogin(clientName, instance);
		this.password = SyncUtils.getClientPassword(clientName, instance);
	}

	private void prepareRestTemplate(ClientHelper clientHelper) {
		List<HttpMessageConverter<?>> converters = new ArrayList<>(clientHelper.getCustomMessageConverter());
		converters.add(new RequestWrapperConverter());
		restTemplate.setMessageConverters(converters);
	}

	private Object retrieveObject(SyncCategory category, String resourceUrl, String destinationUrl, String clientName,
			OpenMRSSyncInstance instance)
			throws RestClientException, URISyntaxException {
		ClientHelper helper = ClientHelperFactory.createClient(clientName);
		Class<?> clazz = helper.resolveClassByCategory(category.getCategory());

		ClientHttpEntity request = helper.retrieveRequest(resourceUrl);
		if (shouldWrappMessage(clientName, instance)) {
			request = sendRequest(category, destinationUrl, clientName, new InnerRequest(request));
		}
		return exchange(helper, request, clazz).getBody();
	}

	private ResponseEntity<String> createObject(SyncCategory category, String resourceUrl, String destinationUrl,
			Object object,
			String clientName, OpenMRSSyncInstance instance) throws RestClientException, URISyntaxException, TemporaryDataIntegrityException {
		ClientHelper helper = ClientHelperFactory.createClient(clientName);
		if(helper instanceof RESTClientHelper && object instanceof SimpleObject) {
			// Analyse this to ensure no problems whatsoever an prevent it from being synchronized.
			Field[] fields = category.getClazz().getFields();
			SimpleObject toBeCreated = (SimpleObject) object;
			for(Field field: fields) {
				if(BaseOpenmrsData.class.isAssignableFrom(field.getType())) {
					if(toBeCreated.containsKey(field.getName())) {
						Object fieldValue = toBeCreated.get(field.getName());
						String resourceUuid = null;
						if(fieldValue instanceof String) {
							// Assume it is a uuid.
							resourceUuid = (String) fieldValue;
						} else if(fieldValue instanceof Map) {
							Map mapFieldValue = (Map) fieldValue;
							if(mapFieldValue.containsKey("uuid")) {
								resourceUuid = (String) mapFieldValue.get("uuid");
							}
						}

						if(resourceUuid != null) {
							String checkUrl = getDestinationUriForResourceAvailabilityCheck(instance, clientName, category, resourceUuid);
							if(!isResourceAvailable(checkUrl, helper)) {
								// Record this problem, stop
								// TODO: Keep doing it until you uncover all issues.
								TemporaryQueue queueItem = new TemporaryQueue();
								queueItem.setInstance(instance);
								queueItem.setObject(toBeCreated);
								queueItem.setReason(category.getCategory() + " resource  with uuid " + resourceUuid + " is not yet created");
								queueItem.setStatus(TemporaryQueue.Status.PENDING);
								queueItem.setDateCreated(new Date());
								queueItem.setAction(ACTION_CREATED);
								queueItem.setSyncCategory(category);
								throw new TemporaryDataIntegrityException(queueItem);
							}
						}
					}
				}
			}
		}
		ClientHttpEntity request = helper.createRequest(resourceUrl, object);
		if (shouldWrappMessage(clientName, instance)) {
			request = sendRequest(category, destinationUrl, clientName, new InnerRequest(request));
		}
		return exchange(helper, request, String.class);
	}

	private ResponseEntity<String> deleteObject(SyncCategory category, String resourceUrl, String destinationUrl,
			String uuid,
			String clientName, OpenMRSSyncInstance instance) throws URISyntaxException {
		ClientHelper helper = ClientHelperFactory.createClient(clientName);

		ClientHttpEntity request = helper.deleteRequest(resourceUrl, uuid);
		if (shouldWrappMessage(clientName, instance)) {
			request = sendRequest(category, destinationUrl, clientName, new InnerRequest(request));
		}
		return exchange(helper, request, null);
	}

	private ResponseEntity<String> updateObject(SyncCategory category, String resourceUrl, String destinationUrl,
			Object object,
			String clientName, OpenMRSSyncInstance instance) throws URISyntaxException {
		ClientHelper helper = ClientHelperFactory.createClient(clientName);

		ClientHttpEntity request = helper.updateRequest(resourceUrl, object);
		if (shouldWrappMessage(clientName, instance)) {
			request = sendRequest(category, destinationUrl, clientName, new InnerRequest(request));
		}
		return exchange(helper, request, String.class);
	}

	private HttpHeaders setRequestHeaders(ClientHelper clientHelper, HttpHeaders headers) {
		for (ClientHttpRequestInterceptor interceptor :
				clientHelper.getCustomInterceptors(this.username, this.password)) {
			interceptor.addToHeaders(headers);
		}
		return headers;
	}

	private ResponseEntity exchange(ClientHelper helper, ClientHttpEntity request, Class clazz) {
		HttpHeaders headers = new HttpHeaders();
		setRequestHeaders(helper, headers);
		HttpEntity entity = new HttpEntity(request.getBody(), headers);
		return restTemplate.exchange(request.getUrl(), request.getMethod(), entity, clazz);
	}

	private ClientHttpEntity<RequestWrapper> sendRequest(SyncCategory category, String destinationUrl, String clientName,
			InnerRequest request) throws URISyntaxException {
		ClientHelper clientHelper = ClientHelperFactory.createClient(clientName);
		Class<?> clazz = clientHelper.resolveClassByCategory(category.getCategory());
		String instanceId = getSyncConfigurationService().getSyncConfiguration().getGeneral().getLocalInstanceId();

		RequestWrapper wrapper = new RequestWrapper();
		wrapper.setInstanceId(instanceId);
		wrapper.setClassName(clazz.getCanonicalName());
		wrapper.setClientName(clientName);
		wrapper.setRequest(request);

		return new ClientHttpEntity<>(wrapper, HttpMethod.POST, new URI(destinationUrl));
	}

	private boolean shouldWrappMessage(String clientName, OpenMRSSyncInstance instance) {
		return !SyncUtils.clientHasSpecificAddress(clientName, instance);
	}

	private String getDestinationUri(OpenMRSSyncInstance instance, String clientName) {
		return getBaseDestinationUri(instance, clientName) + SYNC2_REST_ENDPOINT;
	}

	private String getDestinationUriForResourceAvailabilityCheck(OpenMRSSyncInstance instance, String clientName, SyncCategory category, String uuid) {
		return new StringBuilder(getBaseDestinationUri(instance, clientName))
			.append(WS_REST_V1)
			.append(category.getClazz().getSimpleName().toLowerCase())
			.append(uuid).toString();
	}

	private String getBaseDestinationUri(OpenMRSSyncInstance instance, String clientName) {
		switch (instance) {
			case PARENT:
				return getParentBaseUrl(clientName);
			case CHILD:
				return getLocalBaseUrl();
		}
		return "";
	}

	private boolean isResourceAvailable(String resourceUrl, ClientHelper helper) {
		HttpHeaders headers = new HttpHeaders();
		setRequestHeaders(helper, headers);
		HttpEntity entity = new HttpEntity(null, headers);
		ResponseEntity response = restTemplate.exchange(resourceUrl, HttpMethod.GET, entity, String.class);

		return response.getStatusCode() == HttpStatus.OK;
	}
}
